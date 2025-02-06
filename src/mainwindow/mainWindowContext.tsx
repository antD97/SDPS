import { app } from "@tauri-apps/api";
import { emitTo } from "@tauri-apps/api/event";
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";
import { getCurrentWindow } from "@tauri-apps/api/window";
import { createContext, ReactNode, useCallback, useContext, useEffect, useState } from "react";
import OverlayData from "../overlays/overlayData";
import updateOverlayNames from "../util/updateOverlayNames";

type SelectedMenu = { id: 'About' } | { id: 'Presets' } | { id: 'Settings' } | { id: 'Overlay', index: number };

type ContextType = {
  version: string;
  selectedMenu: SelectedMenu;
  overlays: OverlayData[];
  setSelectedMenu: (selectedMenu: SelectedMenu) => void;
  newOverlay: () => void;
  updateSelectedOverlay: (mutator: (prevOverlayData: OverlayData) => OverlayData) => void;
}

const MAX_OVERLAYS = 16;
export const OVERLAY_UPDATE = 'sdps-update-overlays';
export const OVERLAY_UPDATE_REQUEST = 'sdps-request-overlay-update';

let unlistenToOverlayUpdates: (() => void) | null = null;

const MainWindowContext = createContext<ContextType | null>(null);

export const MainWindowContextProvider = ({ children }: { children: ReactNode }) => {
  const [version, setVersion] = useState<ContextType['version']>('');
  const [{ selectedMenu, overlays }, setOverlaysState] = useState<{
    selectedMenu: ContextType['selectedMenu'];
    overlays: ContextType['overlays'];
  }>({
    selectedMenu: { id: 'About' },
    overlays: []
  });

  // on first render
  useEffect(() => { app.getVersion().then((result) => setVersion(result)); }, []);

  // on overlays change
  useEffect(() => {
    // notify overlay windows of changes
    overlays.forEach((overlayData) => {
      emitTo(overlayData.windowLabel, OVERLAY_UPDATE, overlayData);
    });

    // update the OVERLAY_UPDATE_REQUEST listener
    if (unlistenToOverlayUpdates) {
      unlistenToOverlayUpdates();
      unlistenToOverlayUpdates = null;
    }
    getCurrentWindow().listen<string>(
      OVERLAY_UPDATE_REQUEST,
      (event) => {
        const windowLabel = event.payload;
        const overlay = overlays.find((overlay) => overlay.windowLabel === windowLabel);
        if (overlay) {
          emitTo(windowLabel, OVERLAY_UPDATE, overlay);
        }
      }
    ).then((unlistenFn) => unlistenToOverlayUpdates = unlistenFn);

  }, [overlays]);

  const setSelectedMenu = useCallback((selectedMenu: SelectedMenu) => {
    setOverlaysState((prevOverlaysState) => ({ ...prevOverlaysState, selectedMenu }));
  }, []);

  const newOverlay = useCallback(() => {
    // new window label
    const availableWindowLabels = Array(MAX_OVERLAYS).fill(undefined).map((_, i) => i)
      .filter((i) => !overlays.some(({ windowLabel }) => windowLabel === `overlay-${i}`));
    // do nothing if max overlay count reached
    if (availableWindowLabels.length === 0) { return; }
    const windowLabel = `overlay-${availableWindowLabels[0]}`;

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

        let newSelectedMenu: ContextType['selectedMenu'] = prevSelectedMenu;
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
  }, [overlays]);

  const updateSelectedOverlay = useCallback<ContextType['updateSelectedOverlay']>((mutator) => {
    setOverlaysState(({ selectedMenu: prevSelectedMenu, overlays: prevOverlays }) => ({
      selectedMenu: prevSelectedMenu,
      overlays: prevOverlays.map((prevOverlayData, i) => (
        prevSelectedMenu.id === 'Overlay' && prevSelectedMenu.index === i
          ? mutator(prevOverlayData)
          : prevOverlayData
      ))
    }));
  }, []);

  return (
    <MainWindowContext value={{ version, selectedMenu, overlays, setSelectedMenu, newOverlay, updateSelectedOverlay }}>
      {children}
    </MainWindowContext>
  );
};

export const useMainWindowContext = () => {
  const context = useContext(MainWindowContext);
  if (!context) { throw Error('useMainWindowContext must be used from within a MainWindowContextProvider'); }
  return context;
}
