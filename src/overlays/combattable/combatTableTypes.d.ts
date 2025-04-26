/** Data specific to a combat table overlay */
interface CombatTableData extends BaseOverlayData {
  type: 'combat table';
  styles: {
    window: {
      backgroundColor: string;
      corners: 'Square' | 'Small rounded' | 'Medium rounded' | 'Large rounded' | 'XL rounded';
      padding: number;
    };
    headers: {
      backgroundColor: string;
      textColor: string;
      textSize: number;
      textCapitalization: 'lowercase' | 'Capitalize' | 'UPPERCASE';
      textBolded: boolean;
      borderColor: string;
      borderThickness: number;
      borderSpacing: number;
    };
    rows: {
      default: {
        backgroundColor: string;
        textColor: string;
        textSize: number;
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
  backgroundColor?: string;
  textColor?: string;
};
