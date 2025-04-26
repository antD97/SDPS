import { getCurrentWindow } from '@tauri-apps/api/window';
import { createContext, ReactNode, useContext, useEffect } from 'react';
import { Updater, useImmer } from 'use-immer';
import { COMBAT_UPDATE, COMBAT_UPDATE_REQUEST, OVERLAY_UPDATE, OVERLAY_UPDATE_REQUEST } from '../mainwindow/mainWindowContext';
import { CombatLogData } from '../mainwindow/mainWindowTypes';

const OverlayContext = createContext<{
  overlayData: OverlayData;
  combatLogData: CombatLogData | null;
} | null>(null);

const defaultOverlayData: OverlayData = {
  windowLabel: '',
  overlayName: '',
  windowState: 'adjust',
  type: 'empty'
};

let unlistenToOverlayUpdates: (() => void) | null = null;
let unlistenToCombatUpdates: (() => void) | null = null;

export const OverlayContextProvider = ({ children }: { children: ReactNode }) => {
  const [overlayData, setOverlayData] = useImmer<OverlayData>(defaultOverlayData);
  const [combatLogData, setCombatLogData] = useImmer<CombatLogData | null>(null);
  const { overlayName, windowState } = overlayData;

  useEffect(() => initEffect(setOverlayData, setCombatLogData), []);
  useEffect(() => overlayNameEffect(overlayName), [overlayName])
  useEffect(() => windowStateEffect(windowState), [windowState]);

  return (<OverlayContext value={{ overlayData, combatLogData }}>{children}</OverlayContext>);
};

export const useOverlayContext = () => {
  const context = useContext(OverlayContext);
  if (!context) { throw Error('useOverlayContext must be used from within a OverlayContextProvider'); }
  return context;
}

/** 
 * - Attaches overlay update listener through OVERLAY_UPDATE 
 * - Attaches combat log update listener through COMBAT_UPDATE
 */
function initEffect(
  setOverlayData: Updater<OverlayData>,
  setCombatLogData: Updater<CombatLogData | null>
) {
  // update the OVERLAY_UPDATE listener
  if (unlistenToOverlayUpdates) {
    unlistenToOverlayUpdates();
    unlistenToOverlayUpdates = null;
  }
  const window = getCurrentWindow();
  window.listen<OverlayData>(OVERLAY_UPDATE, (event) => setOverlayData(event.payload))
    .then((unlistenFn) => unlistenToOverlayUpdates = unlistenFn);

  window.emit(OVERLAY_UPDATE_REQUEST, window.label);

  // update COMBAT_UPDATE listener
  if (unlistenToCombatUpdates) {
    unlistenToCombatUpdates();
    unlistenToCombatUpdates = null;
  }
  window.listen<CombatLogData>(COMBAT_UPDATE, (event) => setCombatLogData(event.payload))
    .then((unlistenFn) => unlistenToCombatUpdates = unlistenFn);

  window.emit(COMBAT_UPDATE_REQUEST, window.label);
}

/** Updates window name */
function overlayNameEffect(overlayName: string) {
  const title = `SDPS Overlay: ${overlayName}`;
  getCurrentWindow().setTitle(title);
  document.title = title;
}

/** Updates window cursor event ignoring behavior */
function windowStateEffect(windowState: WindowState) {
  const window = getCurrentWindow();
  switch (windowState) {
    case 'adjust':
      window.setIgnoreCursorEvents(false);
      break;
    case 'hide':
    case 'overlay':
      window.setIgnoreCursorEvents(true);
      break;
  }
}
