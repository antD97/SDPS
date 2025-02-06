import CombatTableData from "./combattable/combatTableData";
import EmptyOverlayData from "./emptyoverlay/emptyOverlayData";

export interface BaseOverlayData {
  windowLabel: string;
  overlayName: string;
  windowState: 'hide' | 'adjust' | 'overlay';
}

type OverlayData =
  EmptyOverlayData
  | CombatTableData;

export const overlayStates: BaseOverlayData['windowState'][] = ['hide', 'adjust', 'overlay'];

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
