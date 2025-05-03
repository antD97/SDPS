import { BaseOverlayData } from '../overlayTypes';

/** Data specific to a combat table overlay */
export interface CombatTableData extends BaseOverlayData {
  type: 'combat table';
  styles: string;
}
