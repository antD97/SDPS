import { app } from "@tauri-apps/api";
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";
import { createContext, ReactNode, useCallback, useContext, useEffect, useState } from "react";
import OverlayData from "../overlays/overlayData";

type SelectedMenu = { id: 'About' } | { id: 'Settings' } | { id: 'Overlay', index: number };

type StateType = {
  version: string;
  selectedMenu: SelectedMenu;
  overlays: OverlayData[];
};

const defaultState: StateType = {
  version: '...',
  selectedMenu: { id: 'About' },
  overlays: []
};

type ContextType = {
  state: StateType;
  setSelectedMenu: (selectedMenu: SelectedMenu) => void;
  newOverlay: () => void;
}

const MAX_OVERLAYS = 16;

const MainWindowContext = createContext<ContextType | null>(null);

export const MainWindowContextProvider = ({ children }: { children: ReactNode }) => {
  const [state, setState] = useState(defaultState);

  useEffect(() => {
    app.getVersion().then((result) => setState((prevState) => ({ ...prevState, version: result })));
  }, []);

  const newOverlay = useCallback(() => {
    const availableWindowLabels = Array(MAX_OVERLAYS).fill(undefined).map((_, i) => i)
      .filter((i) => !state.overlays.some(({ windowLabel }) => windowLabel === `overlay-${i}`));

    // do nothing if max overlay count reached
    if (availableWindowLabels.length === 0) { return; }

    const overlayIndex = availableWindowLabels[0];
    const windowLabel = `overlay-${overlayIndex}`;
    const window = new WebviewWindow(windowLabel, {
      title: `SDPS Overlay ${overlayIndex + 1}`,
      minWidth: 64,
      minHeight: 64,
      transparent: true,
      decorations: false,
      shadow: false,
      dragDropEnabled: false,
      zoomHotkeysEnabled: true
    });

    window.setAlwaysOnTop(true);
    window.setIgnoreCursorEvents(true);

    // save the overlay to state
    setState({
      ...state,
      overlays: [...state.overlays, { windowLabel, windowState: 'draggable', type: 'empty' }]
    });

    // when the window is destroyed...
    window.once('tauri://destroyed', () => {

      setState((prevState) => {

        const newOverlays: OverlayData[] = prevState.overlays
          .filter((overlay) => overlay.windowLabel !== window.label);

        let newSelectedMenu: StateType['selectedMenu'] = prevState.selectedMenu;
        // if the selected menu was an overlay...
        if (newSelectedMenu.id === 'Overlay') {
          // if there are no overlays...
          if (newOverlays.length === 0) {
            newSelectedMenu = { id: 'About' };
          }
          // if there are overlays...
          else {
            const closedWindowMenuIndex = prevState.overlays
              .findIndex((overlay) => overlay.windowLabel === window.label);
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

        return { ...prevState, overlays: newOverlays, selectedMenu: newSelectedMenu };
      });
    });
  }, [state]);

  const setSelectedMenu = useCallback((selectedMenu: SelectedMenu) => {
    setState({ ...state, selectedMenu });
  }, [state]);

  return (
    <MainWindowContext value={{ state, setSelectedMenu, newOverlay }}>
      {children}
    </MainWindowContext>
  );
};

export const useMainWindowContext = () => {
  const context = useContext(MainWindowContext);
  if (!context) { throw Error('useMainWindowContext must be used from within a MainWindowContextProvider'); }
  return context;
}
