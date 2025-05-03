import { Draft } from 'immer';
import { CombatTableData } from './combatTable/combatTableTypes';
import { EmptyOverlayData } from './emptyOverlay/emptyOverlayTypes';

type WindowState = 'hide' | 'adjust' | 'overlay';

export interface BaseOverlayData {
  windowLabel: string;
  overlayName: string;
  windowState: WindowState;
}

type OverlayData =
  EmptyOverlayData
  | CombatTableData;
