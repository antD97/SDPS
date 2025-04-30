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

type StyleSettingsStringEntry<O extends BaseOverlayData> = {
  type: 'string';
  label: string;
  getValue: (overlayData: O) => {
    value?: string;
    default: string;
  };
  setValue: (value: string, draft: Draft<O>) => void;
};

type StyleSettingsNumberEntry<O extends BaseOverlayData> = {
  type: 'number';
  label: string;
  min: number;
  max: number;
  step: number;
  getValue: (overlayData: O) => {
    value?: number;
    default: number;
  };
  setValue: (value: number, draft: Draft<O>) => void;
};

type StyleSettingsBooleanEntry<O extends BaseOverlayData> = {
  type: 'boolean';
  label: string;
  getValue: (OverlayData: O) => boolean;
  setValue: (value: boolean, draft: Draft<O>) => void;
};

type StyleSettingsEnumEntry<O extends BaseOverlayData> = {
  type: 'enum';
  label: string;
  options: string[];
  getValue: (OverlayData: O) => string;
  setValue: (value: string, draft: Draft<O>) => void;
};

type StyleSettingsEntry<O extends BaseOverlayData> =
  StyleSettingsStringEntry<O>
  | StyleSettingsNumberEntry<O>
  | StyleSettingsBooleanEntry<O>
  | StyleSettingsEnumEntry<O>;

type StyleSettingsEntries<O extends BaseOverlayData> = {
  type: 'entries',
  entries: StyleSettingsEntry<O>[];
}

type CustomSection = {
  type: 'custom',
  content: ReactNode
}

type StyleSettingsAccordionData<O extends BaseOverlayData> = {
  type: 'accordion';
  title?: string;
  sections: (StyleSettingsAccordionData<O> | StyleSettingsEntries<O> | CustomSection)[];
};

type CapitalizationStyle = 'lowercase' | 'Capitalize' | 'UPPERCASE';
