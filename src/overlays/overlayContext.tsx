import { emit } from "@tauri-apps/api/event";
import { getCurrentWindow } from "@tauri-apps/api/window";
import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from "react";
import { OVERLAY_UPDATE, OVERLAY_UPDATE_REQUEST } from "../mainwindow/mainWindowContext";
import OverlayData from "./overlayData";

const OverlayContext = createContext<OverlayData | null>(null);

const defaultOverlayData: OverlayData = {
  windowLabel: '',
  overlayName: '',
  windowState: 'adjust',
  type: 'empty'
};

let unlistenToOverlayUpdates: (() => void) | null = null;

export const OverlayContextProvider = ({ children }: { children: ReactNode }) => {
  const [overlayData, setOverlayData] = useState<OverlayData>(defaultOverlayData);
  const { overlayName, windowState } = overlayData;

  const window = useMemo(getCurrentWindow, []);

  // on first render
  useEffect(() => {
    // update the OVERLAY_UPDATE listener
    if (unlistenToOverlayUpdates) {
      unlistenToOverlayUpdates();
      unlistenToOverlayUpdates = null;
    }
    window.listen<OverlayData>(OVERLAY_UPDATE, (event) => { setOverlayData(event.payload); })
      .then((unlistenFn) => unlistenToOverlayUpdates = unlistenFn);

    emit(OVERLAY_UPDATE_REQUEST, window.label);
  }, []);

  // on overlay name change
  useEffect(() => {
    const title = `SDPS Overlay: ${overlayName}`;
    window.setTitle(title);
    document.title = title;
  }, [overlayName])

  // on window state change
  useEffect(() => {
    switch (windowState) {
      case 'adjust':
        window.setIgnoreCursorEvents(false);
        break;
      case 'hide':
      case 'overlay':
        window.setIgnoreCursorEvents(true);
        break;
    }
  }, [windowState]);

  return (<OverlayContext value={overlayData}>{children}</OverlayContext>);
};

export const useOverlayContext = () => {
  const context = useContext(OverlayContext);
  if (!context) { throw Error('useOverlayContext must be used from within a OverlayContextProvider'); }
  return context;
}
