import { app } from '@tauri-apps/api';
import { emitTo } from '@tauri-apps/api/event';
import { WebviewWindow } from '@tauri-apps/api/webviewWindow';
import { getCurrentWindow } from '@tauri-apps/api/window';
import { createContext, ReactNode, useCallback, useContext, useEffect, useState } from 'react';
import { combatMonitor } from '../lib/combatMonitor';
import { overlayNames } from '../overlays/overlayConsts';
import { CombatLogData, MainWindowContextType, MainWindowOverlaysState, SelectedMenu } from './mainWindowTypes';

const MAX_OVERLAYS = 16;
export const OVERLAY_UPDATE = 'sdps-update-overlays'; // main window -> overlays
export const OVERLAY_UPDATE_REQUEST = 'sdps-overlay-update-request'; // overlays -> main window
export const COMBAT_UPDATE = 'sdps-update-combat'; // main window -> overlays
export const COMBAT_UPDATE_REQUEST = 'sdps-combat-update-request'; // overlays -> main window
export const MONITOR_UPDATE = 'sdps-monitor-update'; // rust -> main window

let unlistenToOverlayUpdateRequests: (() => void) | null = null;
let unlistenToCombatUpdateRequests: (() => void) | null = null;
let unlistenToMonitorUpdates: (() => void) | null = null;

const MainWindowContext = createContext<MainWindowContextType | null>(null);

export const MainWindowContextProvider = ({ children }: { children: ReactNode }) => {

  const [version, setVersion] = useState<MainWindowContextType['version']>('');
  const [{ selectedMenu, overlays }, setOverlaysState] = useState<MainWindowOverlaysState>({
    selectedMenu: { id: 'About' },
    overlays: []
  });
  const [combatLogData, setCombatLogData] = useState<CombatLogData | null>(null);

  useEffect(() => initEffect(setVersion, setCombatLogData), []);
  useEffect(() => overlayDataEffect(overlays, combatLogData), [overlays]);
  useEffect(() => combatLogDataEffect(overlays, combatLogData), [combatLogData]);

  const setSelectedMenu = useCallback(
    (selectedMenu: SelectedMenu) => setOverlaysState((prevOverlaysState) => ({ ...prevOverlaysState, selectedMenu })),
    []
  );
  const newOverlay = useCallback(() => newOverlayCallback(overlays, setOverlaysState), [overlays]);
  const updateSelectedOverlay = useCallback<MainWindowContextType['updateSelectedOverlay']>(
    (mutator) => { updateSelectedOverlayCallback(mutator, setOverlaysState) },
    []
  );

  return (
    <MainWindowContext
      value={{
        version,
        selectedMenu,
        overlays,
        combatLogData,
        setSelectedMenu,
        newOverlay,
        updateSelectedOverlay
      }}
    >
      {children}
    </MainWindowContext>
  );
};

export const useMainWindowContext = () => {
  const context = useContext(MainWindowContext);
  if (!context) { throw Error('useMainWindowContext must be used from within a MainWindowContextProvider'); }
  return context;
}

/**
 * - Fetches the application version and updates the state
 * - Attaches combat log monitor listener through MONITOR_UPDATE
 */
function initEffect(
  setVersion: React.Dispatch<React.SetStateAction<string>>,
  setCombatLogData: React.Dispatch<React.SetStateAction<CombatLogData | null>>
) {
  app.getVersion().then((result) => setVersion(result));

  // update the MONITOR_UPDATE listener
  if (unlistenToMonitorUpdates) {
    unlistenToMonitorUpdates();
    unlistenToMonitorUpdates = null;
  }
  const window = getCurrentWindow();
  window.listen<{ Combat: [string, [number, string][]] }>(MONITOR_UPDATE, combatMonitor(setCombatLogData))
    .then((unlistenFn) => unlistenToMonitorUpdates = unlistenFn);
}

/**
 * - Notifies overlay windows of new overlay data through OVERLAY_UPDATE
 * - Refreshes OVERLAY_UPDATE_REQUEST listener
 * - Refreshes COMBAT_UPDATE_REQUEST listener
 */
function overlayDataEffect(overlays: OverlayData[], combatLogData: CombatLogData | null) {
  // notify overlay windows of overlay state changes
  overlays.forEach((overlayData) => { emitTo(overlayData.windowLabel, OVERLAY_UPDATE, overlayData); });

  // update the OVERLAY_UPDATE_REQUEST listener
  if (unlistenToOverlayUpdateRequests) {
    unlistenToOverlayUpdateRequests();
    unlistenToOverlayUpdateRequests = null;
  }
  getCurrentWindow().listen<string>(
    OVERLAY_UPDATE_REQUEST,
    ({ payload: windowLabel }) => {
      const overlay = overlays.find((overlay) => overlay.windowLabel === windowLabel);
      if (overlay) {
        emitTo(windowLabel, OVERLAY_UPDATE, overlay);
      }
    }
  ).then((unlistenFn) => unlistenToOverlayUpdateRequests = unlistenFn);

  // update the COMBAT_UPDATE_REQUEST listener
  if (unlistenToCombatUpdateRequests) {
    unlistenToCombatUpdateRequests();
    unlistenToCombatUpdateRequests = null;
  }
  getCurrentWindow().listen<string>(
    COMBAT_UPDATE_REQUEST,
    ({ payload: windowLabel }) => {
      const overlay = overlays.find((overlay) => overlay.windowLabel === windowLabel);
      if (overlay) {
        emitTo(windowLabel, COMBAT_UPDATE, combatLogData);
      }
    }
  ).then((unlistenFn) => { unlistenToCombatUpdateRequests = unlistenFn });
}

/** Notifies overlay windows of combat log changes through COMBAT_UPDATE */
function combatLogDataEffect(overlays: OverlayData[], combatLogData: CombatLogData | null) {
  overlays.forEach(({ windowLabel }) => { emitTo(windowLabel, COMBAT_UPDATE, combatLogData); });
}

/**
 * - Loads default overlay data for a new overlay
 * - Creates new window for the new overlay
 */
function newOverlayCallback(
  overlays: OverlayData[],
  setOverlaysState: React.Dispatch<React.SetStateAction<MainWindowOverlaysState>>
) {
  // new overlay window label
  const nextWindowIndex = Array(MAX_OVERLAYS).fill(undefined).map((_, i) => i)
    .find((i) => !overlays.some(({ windowLabel }) => windowLabel === `overlay-${i}`));
  if (nextWindowIndex === undefined) { return; } // do nothing if max overlay count reached
  const windowLabel = `overlay-${nextWindowIndex}`;

  // add new overlay to overlays
  setOverlaysState({
    selectedMenu: { id: 'Overlay', index: overlays.length },
    overlays: updateOverlayNames([
      ...overlays,
      { windowLabel, overlayName: '', windowState: 'adjust', type: 'empty' } as OverlayData
    ])
  });

  const window = new WebviewWindow(windowLabel, {
    title: 'SDPS Overlay',
    minWidth: 64,
    minHeight: 64,
    transparent: true,
    decorations: false,
    shadow: false,
    dragDropEnabled: false,
    zoomHotkeysEnabled: true,
    skipTaskbar: true,
    alwaysOnTop: true
  });

  // when the window is destroyed...
  window.once('tauri://destroyed', () => {
    // update overlays & selectedMenu
    setOverlaysState(({ selectedMenu: prevSelectedMenu, overlays: prevOverlays }) => {
      const newOverlays: OverlayData[] = prevOverlays
        .filter((overlay) => overlay.windowLabel !== window.label);

      let newSelectedMenu: MainWindowContextType['selectedMenu'] = prevSelectedMenu;
      // if the selected menu was an overlay...
      if (newSelectedMenu.id === 'Overlay') {
        // if there are no overlays...
        if (newOverlays.length === 0) {
          newSelectedMenu = { id: 'About' };
        }
        // if there are overlays...
        else {
          const closedWindowMenuIndex = prevOverlays.findIndex((overlay) => overlay.windowLabel === window.label);
          // if the selected menu was below the closed overlay window...
          if (newSelectedMenu.index > closedWindowMenuIndex) {
            newSelectedMenu = { id: 'Overlay', index: Math.max(newSelectedMenu.index - 1, 0) };
          }
          newSelectedMenu = {
            id: 'Overlay',
            index: Math.min(newOverlays.length - 1, newSelectedMenu.index)
          };
        }
      }

      return { selectedMenu: newSelectedMenu, overlays: newOverlays };
    });
  });
}

/** Updates the selected overlay using `mutator` */
function updateSelectedOverlayCallback(
  mutator: (prevOverlayData: OverlayData) => OverlayData,
  setOverlaysState: React.Dispatch<React.SetStateAction<MainWindowOverlaysState>>
) {
  setOverlaysState(({ selectedMenu: prevSelectedMenu, overlays: prevOverlays }) => ({
    selectedMenu: prevSelectedMenu,
    overlays: prevOverlays.map((prevOverlayData, i) => (
      prevSelectedMenu.id === 'Overlay' && prevSelectedMenu.index === i
        ? mutator(prevOverlayData)
        : prevOverlayData
    ))
  }));
}

function updateOverlayNames(overlays: OverlayData[]): OverlayData[] {
  return overlays.map((overlay, i, allOverlays) => {
    // if there is only one overlay of this type
    if (allOverlays.filter((o) => o.type === overlay.type).length <= 1) {
      return { ...overlay, overlayName: overlayNames[overlay.type].shortName };
    }
    // if there are multiple overlays of this type
    else {
      const numPrecedingSameOverlays = allOverlays.filter((o, j) => j < i && o.type === overlay.type).length;
      return {
        ...overlay,
        overlayName: `${overlayNames[overlay.type].shortName} ${numPrecedingSameOverlays + 1}`
      }
    }
  });
}
