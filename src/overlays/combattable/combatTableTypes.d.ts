import { BaseOverlayData, CapitalizationStyle } from '../overlayTypes';

/** Data specific to a combat table overlay */
export interface CombatTableData extends BaseOverlayData {
  type: 'combat table';
  styles: {
    window: {
      backgroundColor: StyleStringValue;
      corners: StyleNumberValue;
      padding: StyleNumberValue;
    };
    headers: {
      backgroundColor: StyleStringValue;
      textColor: StyleStringValue;
      textSize: StyleNumberValue;
      textCapitalization: CapitalizationStyle;
      textBolded: boolean;
      borderColor: StyleStringValue;
      borderThickness: StyleNumberValue;
      borderSpacing: StyleNumberValue;
    };
    rows: {
      default: {
        backgroundColor: StyleStringValue;
        textColor: StyleStringValue;
        textSize: StyleNumberValue;
      };
      odd: RowStyleType;
      even: RowStyleType;
      rowTypes: {
        damageDealt: RowStyleType;
        damageReceived: RowStyleType;
        healDealt: RowStyleType;
        healReceived: RowStyleType;
        killPlayer: RowStyleType;
        killNpc: RowStyleType;
        death: RowStyleType;
        assist: RowStyleType;
        level: RowStyleType;
        abilityPurchase: RowStyleType;
      };
    };
  };
}

type RowStyleType = {
  backgroundColor: StyleStringValue;
  textColor: StyleStringValue;
};

type StyleStringValue = {
  default: string;
  value?: string;
};

type StyleNumberValue = {
  default: number;
  value?: number;
};
