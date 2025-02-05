import { CombatTableData } from "../mainwindow/overlaysettings/combatTableSettings";
import { EmptyOverlayData } from "../mainwindow/overlaysettings/emptyOverlaySettings";

export interface BaseOverlayData {
  windowLabel: string;
  windowState: 'hidden' | 'draggable' | 'overlay';
}

type OverlayData =
  EmptyOverlayData
  | CombatTableData;

export const overlayStates: BaseOverlayData['windowState'][] = ['hidden', 'draggable', 'overlay'];

export const overlayNames: Record<OverlayData['type'], { shortName: string, longName: string }> = {
  empty: {
    shortName: 'Empty',
    longName: 'Empty Overlay'
  },
  'combat table': {
    shortName: 'Combat',
    longName: 'Combat Table'
  }
}

export default OverlayData;
