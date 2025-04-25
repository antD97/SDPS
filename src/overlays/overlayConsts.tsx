export const overlayStates = ['hide', 'adjust', 'overlay'] as const;

export const overlayNames = {
  empty: {
    shortName: 'Empty',
    longName: 'Empty Overlay'
  },
  'combat table': {
    shortName: 'Combat',
    longName: 'Combat Table'
  }
} as const;
