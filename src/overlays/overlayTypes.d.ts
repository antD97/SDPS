type WindowState = 'hide' | 'adjust' | 'overlay';

interface BaseOverlayData {
  windowLabel: string;
  overlayName: string;
  windowState: WindowState;
}

type OverlayData =
  EmptyOverlayData
  | CombatTableData;
