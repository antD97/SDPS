import { createContext, ReactNode, useContext, useState } from "react";
import OverlayData from "./overlayData";

type OverlayStateType = {
  windowState: 'overlay' | 'draggable' | 'hidden';
  type: OverlayData['type'];
};

const defaultState: OverlayStateType = {
  windowState: 'draggable',
  type: 'empty'
};

const OverlayContext = createContext<[OverlayStateType, (overlayState: OverlayStateType) => void] | null>(null);

export const OverlayContextProvider = ({ children }: { children: ReactNode }) => {
  const [overlayState, setOverlayState] = useState<OverlayStateType>(defaultState);
  return (<OverlayContext value={[overlayState, setOverlayState]}>{children}</OverlayContext>);
};

export const useOverlayContext = () => {
  const context = useContext(OverlayContext);
  if (!context) { throw Error('useOverlayContext must be used from within a OverlayContextProvider'); }
  return context;
}
