import { CapitalizationStyle, StyleSettingsAccordionData } from '../overlayTypes';
import { CombatTableData } from './combatTableTypes';

// TODO finalize values
export const initialCombatTableData: CombatTableData = {
  windowLabel: '',
  overlayName: '',
  windowState: 'adjust',
  type: 'combat table',
  styles: {
    window: {
      backgroundColor: { default: 'oklch(20.5% 0 0 / 97%)' },
      corners: { default: 0 },
      padding: { default: 10 }
    },
    headers: {
      backgroundColor: { default: '' },
      textColor: { default: 'TODO' },
      textSize: { default: 999 },
      textCapitalization: 'Capitalize',
      textBolded: true,
      borderColor: { default: 'TODO' },
      borderThickness: { default: 999 },
      borderSpacing: { default: 999 }
    },
    rows: {
      default: {
        backgroundColor: { default: 'TODO' },
        textColor: { default: 'TODO' },
        textSize: { default: 999 }
      },
      odd: {
        backgroundColor: { default: 'TODO' },
        textColor: { default: 'TODO' }
      },
      even: {
        backgroundColor: { default: 'TODO' },
        textColor: { default: 'TODO' }
      },
      rowTypes: {
        damageDealt: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        damageReceived: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        healDealt: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        healReceived: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        killPlayer: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        killNpc: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        death: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        assist: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        level: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        },
        abilityPurchase: {
          backgroundColor: { default: 'TODO' },
          textColor: { default: 'TODO' }
        }
      }
    }
  }
};

export const combatTableStyleSettingsAccordionData: StyleSettingsAccordionData<CombatTableData> = {
  type: 'accordion',
  sections: [
    // window
    {
      type: 'accordion',
      title: 'Window',
      sections: [
        {
          type: 'entries',
          entries: [
            {
              type: 'string',
              label: 'Background Color',
              getValue: ({ styles }) => styles.window.backgroundColor,
              setValue: (value, draft) => {
                draft.styles.window.backgroundColor.value = value !== '' ? value : undefined;
              }
            }, {
              type: 'number',
              label: 'Corners',
              min: 0,
              max: 100,
              step: 1,
              getValue: ({ styles }) => styles.window.corners,
              setValue: (value, draft) => {
                draft.styles.window.corners.value = !isNaN(value) ? value : undefined;
              }
            }, {
              type: 'number',
              label: 'Padding',
              min: 0,
              max: 100,
              step: 1,
              getValue: ({ styles }) => styles.window.padding,
              setValue: (value, draft) => {
                draft.styles.window.padding.value = !isNaN(value) ? value : undefined;
              }
            }
          ]
        }
      ]
    },

    // headers
    {
      type: 'accordion',
      title: 'Headers',
      sections: [
        {
          type: 'entries',
          entries: [
            {
              type: 'string',
              label: 'Background Color',
              getValue: ({ styles }) => styles.headers.backgroundColor,
              setValue: (value, draft) => {
                draft.styles.headers.backgroundColor.value = value !== '' ? value : undefined;
              }
            }, {
              type: 'string',
              label: 'Text Color',
              getValue: ({ styles }) => styles.headers.textColor,
              setValue: (value, draft) => {
                draft.styles.headers.textColor.value = value !== '' ? value : undefined;
              }
            }, {
              type: 'number',
              label: 'Text Size',
              min: 1,
              max: 100,
              step: 1,
              getValue: ({ styles }) => styles.headers.textSize,
              setValue: (value, draft) => {
                draft.styles.headers.textSize.value = !isNaN(value) ? value : undefined;
              }
            }, {
              type: 'enum',
              label: 'Text Capitalization',
              options: ['lowercase', 'Capitalize', 'UPPERCASE'],
              getValue: ({ styles }) => styles.headers.textCapitalization,
              setValue: (value, draft) => { draft.styles.headers.textCapitalization = value as CapitalizationStyle; }
            }, {
              type: 'boolean',
              label: 'Text Bolded',
              getValue: ({ styles }) => styles.headers.textBolded,
              setValue: (value, draft) => { draft.styles.headers.textBolded = value; }
            }, {
              type: 'string',
              label: 'Border Color',
              getValue: ({ styles }) => styles.headers.borderColor,
              setValue: (value, draft) => {
                draft.styles.headers.borderColor.value = value !== '' ? value : undefined;
              }
            }, {
              type: 'number',
              label: 'Border Thickness',
              min: 0,
              max: 10,
              step: 1,
              getValue: ({ styles }) => styles.headers.borderThickness,
              setValue: (value, draft) => {
                draft.styles.headers.borderThickness.value = !isNaN(value) ? value : undefined;
              }
            }, {
              type: 'number',
              label: 'Border Spacing',
              min: 0,
              max: 10,
              step: 1,
              getValue: ({ styles }) => styles.headers.borderSpacing,
              setValue: (value, draft) => {
                draft.styles.headers.borderSpacing.value = !isNaN(value) ? value : undefined;
              }
            }
          ]
        }
      ]
    },

    // rows
    {
      type: 'accordion',
      title: 'Rows',
      sections: [
        {
          type: 'custom',
          content: (
            <p>
              Row styling precedence follows the order:
              "Row&nbsp;Type"&nbsp;&gt;&nbsp;"Odd/Even"&nbsp;&gt;&nbsp;"Default&nbsp;Row&nbsp;Style"
            </p>
          )
        },

        // default row styles
        {
          type: 'accordion',
          title: 'Default Row Styles',
          sections: [
            {
              type: 'entries',
              entries: [
                {
                  type: 'string',
                  label: 'Background Color',
                  getValue: ({ styles }) => styles.rows.default.backgroundColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.default.backgroundColor.value = value !== '' ? value : undefined;
                  }
                }, {
                  type: 'string',
                  label: 'Text Color',
                  getValue: ({ styles }) => styles.rows.default.textColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.default.textColor.value = value !== '' ? value : undefined;
                  }
                }, {
                  type: 'number',
                  label: 'Text Size',
                  min: 1,
                  max: 100,
                  step: 1,
                  getValue: ({ styles }) => styles.rows.default.textSize,
                  setValue: (value, draft) => {
                    draft.styles.rows.default.textSize.value = !isNaN(value) ? value : undefined;
                  }
                }
              ]
            }
          ]
        },

        // odd rows
        {
          type: 'accordion',
          title: 'Odd Rows',
          sections: [
            {
              type: 'entries',
              entries: [
                {
                  type: 'string',
                  label: 'Background Color',
                  getValue: ({ styles }) => styles.rows.odd.backgroundColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.odd.backgroundColor.value = value !== '' ? value : undefined;
                  }
                },
                {
                  type: 'string',
                  label: 'Text Color',
                  getValue: ({ styles }) => styles.rows.odd.textColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.odd.textColor.value = value !== '' ? value : undefined;
                  }
                }
              ]
            }
          ]
        },

        // even rows
        {
          type: 'accordion',
          title: 'Even Rows',
          sections: [
            {
              type: 'entries',
              entries: [
                {
                  type: 'string',
                  label: 'Background Color',
                  getValue: ({ styles }) => styles.rows.even.backgroundColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.even.backgroundColor.value = value !== '' ? value : undefined;
                  }
                },
                {
                  type: 'string',
                  label: 'Text Color',
                  getValue: ({ styles }) => styles.rows.even.textColor,
                  setValue: (value, draft) => {
                    draft.styles.rows.even.textColor.value = value !== '' ? value : undefined;
                  }
                }
              ]
            }
          ]
        },

        // row types
        {
          type: 'accordion',
          title: 'Row Types',
          sections: [

            // damage dealt
            {
              type: 'accordion',
              title: 'Damage Dealt',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.damageDealt.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.damageDealt.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.damageDealt.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.damageDealt.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // damage received
            {
              type: 'accordion',
              title: 'Damage Received',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.damageReceived.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.damageReceived.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.damageReceived.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.damageReceived.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // heal dealt
            {
              type: 'accordion',
              title: 'Heal Dealt',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.healDealt.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.healDealt.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.healDealt.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.healDealt.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // heal received
            {
              type: 'accordion',
              title: 'Heal Received',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.healReceived.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.healReceived.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.healReceived.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.healReceived.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // kill player
            {
              type: 'accordion',
              title: 'Kill Player',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.killPlayer.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.killPlayer.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.killPlayer.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.killPlayer.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // kill npc
            {
              type: 'accordion',
              title: 'Kill NPC',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.killNpc.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.killNpc.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.killNpc.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.killNpc.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // death
            {
              type: 'accordion',
              title: 'Death',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.death.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.death.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.death.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.death.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // assist
            {
              type: 'accordion',
              title: 'Assist',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.assist.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.assist.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.assist.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.assist.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // level
            {
              type: 'accordion',
              title: 'Level',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.level.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.level.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.level.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.level.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            },

            // level
            {
              type: 'accordion',
              title: 'Ability Purchase',
              sections: [
                {
                  type: 'entries',
                  entries: [
                    {
                      type: 'string',
                      label: 'Background Color',
                      getValue: ({ styles }) => styles.rows.rowTypes.abilityPurchase.backgroundColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.abilityPurchase.backgroundColor.value = value !== '' ? value : undefined;
                      }
                    },
                    {
                      type: 'string',
                      label: 'TextColor',
                      getValue: ({ styles }) => styles.rows.rowTypes.abilityPurchase.textColor,
                      setValue: (value, draft) => {
                        draft.styles.rows.rowTypes.abilityPurchase.textColor.value = value !== '' ? value : undefined;
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
};
