import { Draft } from 'immer';
import { Fragment } from 'react/jsx-runtime';
import { useMainWindowContext } from '../mainWindow/mainWindowContext';
import { BaseOverlayData, StyleSettingsAccordionData, StyleSettingsEntry } from '../overlays/overlayTypes';
import { Accordion } from './accordion';
import { CheckboxField, FieldContainer, NumField, SelectField, TextField } from './inputFields';

const accordionColorOrder = ['blue1', 'gray1', 'blue2', 'gray2'] as const;

export const StyleSettingsAccordion = <O extends BaseOverlayData>(
  {
    styleSettingsAccordionData,
    depth = 0
  }: {
    styleSettingsAccordionData: StyleSettingsAccordionData<O>,
    depth?: number
  }
) => (
  <Accordion
    title={styleSettingsAccordionData.title ?? 'Overlay Style'}
    color={accordionColorOrder[depth % accordionColorOrder.length]}
    contentClassName="gap-4"
    className="col-span-2"
  >
    {
      styleSettingsAccordionData.sections.map((section, i) => {
        switch (section.type) {
          case 'accordion': return (<StyleSettingsAccordion key={i} styleSettingsAccordionData={section} depth={depth + 1} />);
          case 'entries': return (<StyleSettingsEntries key={i} entries={section.entries} />);
          case 'custom': return (<Fragment key={i}>{section.content}</Fragment>);
        }
      })
    }
  </Accordion>
);

const StyleSettingsEntries = <O extends BaseOverlayData>({ entries }: { entries: StyleSettingsEntry<O>[] }) => {
  const { selectedOverlayData, setSelectedOverlayData } = useMainWindowContext();
  if (selectedOverlayData === null) { return (<></>); }
  return (
    <FieldContainer>
      {
        entries.map((entry, i) => {
          switch (entry.type) {
            case 'string': return (
              <TextField
                key={i}
                value={entry.getValue(selectedOverlayData as BaseOverlayData as O).value}
                placeholder={entry.getValue(selectedOverlayData as BaseOverlayData as O).default}
                onChange={(e) => {
                  const value = e.target.value;
                  setSelectedOverlayData((draft) => {
                    entry.setValue(value, draft as Draft<BaseOverlayData> as Draft<O>);
                  });
                }}
                label={entry.label}
              />
            );
            case 'number': return (
              <NumField
                key={i}
                value={entry.getValue(selectedOverlayData as BaseOverlayData as O).value}
                placeholder={entry.getValue(selectedOverlayData as BaseOverlayData as O).default}
                onChange={(e) => {
                  const value = e.target.value;
                  setSelectedOverlayData((draft) => {
                    entry.setValue(parseInt(value), draft as Draft<BaseOverlayData> as Draft<O>);
                  });
                }}
                label={entry.label}
                min={entry.min}
                max={entry.max}
                step={entry.step}
              />
            );
            case 'boolean': return (
              <CheckboxField
                key={i}
                checked={entry.getValue(selectedOverlayData as BaseOverlayData as O)}
                onChange={(e) => {
                  const value = e.target.checked;
                  setSelectedOverlayData((draft) => {
                    entry.setValue(value, draft as Draft<BaseOverlayData> as Draft<O>);
                  });
                }}
                label={entry.label}
              />
            );
            case 'enum': return (
              <SelectField
                key={i}
                value={entry.getValue(selectedOverlayData as BaseOverlayData as O)}
                onChange={(e) => {
                  const value = e.target.value;
                  setSelectedOverlayData((draft) => {
                    entry.setValue(value, draft as Draft<BaseOverlayData> as Draft<O>)
                  })
                }}
                label={entry.label}
                options={entry.options}
              />
            );
          }
        })
      }
    </FieldContainer>
  );
}
