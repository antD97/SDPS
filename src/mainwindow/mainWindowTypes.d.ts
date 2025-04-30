import { Updater } from 'use-immer';
import { OverlayData } from '../overlays/overlayTypes';
import { CombatLine } from './combatLine';

type SelectedMenu =
  { id: 'About' }
  | { id: 'Presets' }
  | { id: 'Settings' }
  | { id: 'Overlay', index: number };

type CombatLogData = {
  ign: string | null;
  filename: string;
  debugLines: string[];
  combatLines: CombatLine[];
  potentialHiddenCombat: boolean;
};

type MainWindowContextType = {
  version: string;
  selectedMenu: SelectedMenu;
  overlays: OverlayData[];
  selectedOverlayData: OverlayData | null;
  combatLogData: CombatLogData | null;
  setSelectedMenu: (selectedMenu: SelectedMenu) => void;
  newOverlay: () => void;
  setSelectedOverlayData: Updater<OverlayData>;
};

type MainWindowOverlaysState = {
  selectedMenu: SelectedMenu;
  overlays: OverlayData[];
};
