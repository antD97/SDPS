// TODO finalize values
export const defaultCombatTableData: CombatTableData = {
  windowLabel: '',
  overlayName: '',
  windowState: 'adjust',
  type: 'combat table',
  styles: {
    window: {
      backgroundColor: 'TEST TEST',
      corners: 'Square',
      padding: 5
    },
    headers: {
      backgroundColor: '',
      textColor: '',
      textSize: 5,
      textCapitalization: 'Capitalize',
      textBolded: true,
      borderColor: '',
      borderThickness: 1,
      borderSpacing: 1
    },
    rows: {
      default: {
        backgroundColor: '',
        textColor: '',
        textSize: 5
      },
      odd: {
        backgroundColor: undefined,
        textColor: undefined
      },
      even: {
        backgroundColor: undefined,
        textColor: undefined
      },
      rowTypes: {
        damageDealt: {
          backgroundColor: undefined,
          textColor: undefined
        },
        damageReceived: {
          backgroundColor: undefined,
          textColor: undefined
        },
        healDealt: {
          backgroundColor: undefined,
          textColor: undefined
        },
        healReceived: {
          backgroundColor: undefined,
          textColor: undefined
        },
        killPlayer: {
          backgroundColor: undefined,
          textColor: undefined
        },
        killNpc: {
          backgroundColor: undefined,
          textColor: undefined
        },
        death: {
          backgroundColor: undefined,
          textColor: undefined
        },
        assist: {
          backgroundColor: undefined,
          textColor: undefined
        },
        level: {
          backgroundColor: undefined,
          textColor: undefined
        },
        abilityPurchase: {
          backgroundColor: undefined,
          textColor: undefined
        }
      }
    }
  }
};
