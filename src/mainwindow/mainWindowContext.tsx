import { app } from '@tauri-apps/api';
import { emitTo } from '@tauri-apps/api/event';
import { WebviewWindow } from '@tauri-apps/api/webviewWindow';
import { getCurrentWindow } from '@tauri-apps/api/window';
import { produce } from 'immer';
import { createContext, ReactNode, useCallback, useContext, useEffect } from 'react';
import { Updater, useImmer } from 'use-immer';
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

  const [version, setVersion] = useImmer('');
  const [{ selectedMenu, overlays }, setOverlaysState] = useImmer<MainWindowOverlaysState>({
    selectedMenu: { id: 'About' },
    overlays: []
  });
  const [combatLogData, setCombatLogData] = useImmer<CombatLogData | null>(null);

  useEffect(() => initEffect(setVersion, setCombatLogData), []);
  useEffect(() => overlayDataEffect(overlays, combatLogData), [overlays]);
  useEffect(() => combatLogDataEffect(overlays, combatLogData), [combatLogData]);

  const setSelectedMenu
    = useCallback((selectedMenu: SelectedMenu) => setOverlaysState((draft) => { draft.selectedMenu = selectedMenu; }), []);
  const newOverlay = useCallback(newOverlayCallback(overlays, setOverlaysState), [overlays]);
  const setSelectedOverlayData = useCallback(setSelectedOverlayDataCallback(setOverlaysState), [setOverlaysState]);

  return (
    <MainWindowContext
      value={{
        version,
        selectedMenu,
        overlays,
        selectedOverlayData: selectedMenu.id === 'Overlay' ? overlays[selectedMenu.index] : null,
        combatLogData,
        setSelectedMenu,
        newOverlay,
        setSelectedOverlayData
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
  setVersion: Updater<string>,
  setCombatLogData: Updater<CombatLogData | null>
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
  setOverlaysState: Updater<MainWindowOverlaysState>
) {
  return () => {

    // new overlay window label
    const nextWindowIndex = Array(MAX_OVERLAYS).fill(undefined).map((_, i) => i)
      .find((i) => !overlays.some(({ windowLabel }) => windowLabel === `overlay-${i}`));
    if (nextWindowIndex === undefined) { return; } // do nothing if max overlay count reached
    const windowLabel = `overlay-${nextWindowIndex}`;

    setOverlaysState((draft) => {
      draft.selectedMenu = { id: 'Overlay', index: draft.overlays.length };
      draft.overlays.push({ windowLabel, overlayName: '', windowState: 'adjust', type: 'empty' });
      draft.overlays = updateOverlayNames(draft.overlays);
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
    window.once('tauri://destroyed', () => { // TODO don't overlay names have to be updated on window destroyed?

      // update overlays & selectedMenu
      setOverlaysState((draft) => {
        const prevOverlays = draft.overlays;
        draft.overlays = draft.overlays.filter((overlay) => overlay.windowLabel !== window.label);

        // if the selected menu was an overlay...
        if (draft.selectedMenu.id === 'Overlay') {
          // if there are no more overlays...
          if (draft.overlays.length === 0) {
            draft.selectedMenu = { id: 'About' };
          }
          // if there are still overlays...
          else {
            const closedWindowMenuIndex = prevOverlays.findIndex((overlay) => overlay.windowLabel === windowLabel);
            // if the selected menu was below the closed overlay window...
            if (draft.selectedMenu.index > closedWindowMenuIndex) {
              draft.selectedMenu.index = Math.max(draft.selectedMenu.index - 1, 0);
            }
            draft.selectedMenu.index = Math.min(draft.selectedMenu.index, draft.overlays.length - 1);
          }
        }
      });
    });
  };
}

/** Updates the currently selected overlay's data using `updater` as an `OverlayData` or `Updater` */
function setSelectedOverlayDataCallback(setOverlaysState: Updater<MainWindowOverlaysState>): Updater<OverlayData> {
  return (updater) => {
    setOverlaysState((draft) => {
      if (typeof updater === 'function') { // DraftFunction<OverlayData
        if (draft.selectedMenu.id !== 'Overlay' || draft.overlays.length === 0) { return; }
        draft.overlays[draft.selectedMenu.index] = produce(draft.overlays[draft.selectedMenu.index], updater);
      } else { // OverlayData
        return updater;
      }
    });
  };
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
