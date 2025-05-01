import { H } from '../../components/header';
import { StyleSettingsAccordion } from '../../components/styleSettings';
import { combatTableStyleSettingsAccordionData } from '../../overlays/combatTable/combatTableConsts';

export const CombatTableSettings = () => (
  <div className="flex flex-col gap-4">

    <H level="1">Combat Table</H>

    combat table settings...

    <StyleSettingsAccordion styleSettingsAccordionData={combatTableStyleSettingsAccordionData} />
  </div>
);
