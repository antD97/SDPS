import { useCallback } from "react";
import { Accordion } from "../../components/accordion";
import { H } from "../../components/header";
import { FieldContainer, NumField, SelectField, TextField } from "../../components/inputFields";
import { useMainWindowContext } from "../mainWindowContext";

export const CombatTableSettings = () => (
  <div className="flex flex-col gap-4">

    <H level="1">Combat Table</H>

    combat table settings...

    <StyleSettings />
  </div>
);

const StyleSettings = () => {
  const { selectedOverlayData, setSelectedOverlayData } = useMainWindowContext();
  if (selectedOverlayData === null || selectedOverlayData.type !== 'combat table') { return (<></>); }

  // const textFieldProps = useCallback((path: string) => {
  //   return {
  //     value: _.get(selectedOverlayData, path, `FAILED TO FIND VALUE WITH PATH: "${path}"`),
  //     onChange: (e: React.ChangeEvent<HTMLInputElement>) => {
  //       const value = e.target.value;
  //       updateSelectedOverlayData((prev) => {
  //         const result = _.cloneDeep(prev);
  //         _.set(result, path, value);
  //         return result;
  //       });
  //     }
  //   };
  // }, [selectedOverlayData, updateSelectedOverlayData]);

  // const selectFieldProps = useCallback((path: string) => {
  //   return {
  //     value: _.get(selectedOverlayData, path, `FAILED TO FIND VALUE WITH PATH: "${path}"`),
  //     onChange: (e: React.ChangeEvent<HTMLSelectElement>) => {
  //       const value = e.target.value;
  //       updateSelectedOverlayData((prev) => {
  //         const result = _.cloneDeep(prev);
  //         _.set(result, path, value);
  //         return result;
  //       });
  //     }
  //   };
  // }, [selectedOverlayData, updateSelectedOverlayData]);

  return (
    <Accordion title="Overlay Style" contentClassName="gap-4">

      <Accordion title="Window" color="gray1">
        <FieldContainer>
          {/* <TextField label="Background Color" {...textFieldProps('styles.window.backgroundColor')} />
          <SelectField
            label="Corners"
            options={['Square', 'Small rounded', 'Medium rounded', 'Large rounded', 'XL rounded']}
            {...selectFieldProps('styles.window.corners')}
          /> */}
          <NumField label="Padding" min={0} max={100} value={100} />
        </FieldContainer>
      </Accordion>

      {/* <Accordion title="Headers" color="gray1">
        <FieldContainer>
          <TextField label="Background Color" value="" />
          <TextField label="Text Color" value="" />
          <NumField label="Text Size" min={1} max={100} value={12} />
          <SelectField
            label="Text Capitalization"
            options={['lowercase', 'Capitalize', 'UPPERCASE']}
            value="Capitalize"
          />
          <CheckboxField label="Text Bolded" checked={true} />
          <TextField label="Border Color" value="" />
          <NumField label="Border Thickness" min={1} max={10} value={1} />
          <NumField label="Border Spacing" min={1} max={10} value={5} />
        </FieldContainer>
      </Accordion>

      <Accordion title="Rows" color="gray1" contentClassName="gap-4">
        <p>
          Row styling precedence follows the order:
          "Row&nbsp;Type"&nbsp;&gt;&nbsp;"Odd/Even"&nbsp;&gt;&nbsp;"Default&nbsp;Row&nbsp;Style"
        </p>

        <Accordion title="Default Row Styles" color="blue2">
          <FieldContainer>
            <TextField label="Background Color" value="" />
            <TextField label="Text Color" value="" />
            <NumField label="Text Size" min={1} max={100} value={12} />
          </FieldContainer>
        </Accordion>

        <Accordion title="Odd Rows" color="blue2">
          <FieldContainer>
            <TextField label="Background Color" value="" />
            <TextField label="Text Color" value="" />
          </FieldContainer>
        </Accordion>

        <Accordion title="Even Rows" color="blue2">
          <FieldContainer>
            <TextField label="Background Color" value="" />
            <TextField label="Text Color" value="" />
          </FieldContainer>
        </Accordion>

        <Accordion title="Row Types" color="blue2" contentClassName="gap-4">
          {
            [
              'Damage Dealt',
              'Damage Received',
              'Heal Dealt',
              'Heal Received',
              'Kill Player',
              'Kill NPC',
              'Death',
              'Assist',
              'Level',
              'Ability Purchase'
            ].map((rowType) => (
              <Accordion key={rowType} title={rowType} color="gray2">
                <FieldContainer>
                  <TextField label="Background Color" value="" />
                  <TextField label="Text Color" value="" />
                </FieldContainer>
              </Accordion>
            ))
          }
        </Accordion>
      </Accordion> */}
    </Accordion>
  );
};
