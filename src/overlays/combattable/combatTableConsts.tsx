import { CombatTableData } from './combatTableTypes';

export const initialCombatTableData: CombatTableData = {
  windowLabel: '',
  overlayName: '',
  windowState: 'adjust',
  type: 'combat table',
  styles: `
--bg: oklch(20.5% 0 0 / 97%);

display: flex;
flex-direction: column;
overflow: hidden;

.window-padding {
  min-height: 10px;
  min-width: 10px;
  background: var(--bg);
}

.window-vert-padding-content {
  flex-grow: 1;
  display: flex;
  overflow: hidden;
}

.padding-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  border-bottom-width: 1px;
  padding-bottom: 5px;
  background: var(--bg);
  font-weight: bold;
  text-transform: capitalize;
}

.table-data {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  overflow: hidden;
}

.row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  background: var(--bg);
}

.row-fill {
  flex-grow: 1;
  background: var(--bg);
}

.cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
`.trim()
};
