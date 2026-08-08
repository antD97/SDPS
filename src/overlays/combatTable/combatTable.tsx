import { css, SerializedStyles } from '@emotion/react';
import { motion } from 'framer-motion';
import { ReactNode, useEffect } from 'react';
import { twMerge } from 'tailwind-merge';
import { useDebouncedCallback } from 'use-debounce';
import { useImmer } from 'use-immer';
import { useOverlayContext } from '../overlayContext';

export const CombatTable = () => {
  const { overlayData } = useOverlayContext();
  if (overlayData.type !== 'combat table') { return (<></>); }

  const numCols = 4;

  const [cssObj, setCssObj] = useImmer<SerializedStyles>(css`${overlayData.styles}`);
  const parseStyles = useDebouncedCallback(() => {
    setCssObj(css`${overlayData.styles.replaceAll('{{{numCols}}}', `${numCols}`)}`);
  }, 500);
  useEffect(parseStyles, [overlayData.styles]);

  return (
    <div css={cssObj}>
      <div className="window-padding window-padding-t" />
      <div className="window-vert-padding-content">
        <div className="window-padding window-padding-l" />

        <div className="padding-content">
          <CombatTableHeader />
          <CombatTableData />
          <div className="row-fill" />
        </div>

        <div className="window-padding window-padding-r" />
      </div>
      <div className="window-padding window-padding-b" />
    </div>
  );
}

const CombatTableHeader = () => {
  return (
    <div className="header">
      <div>damage</div>
      <div>mitigated</div>
      <div>target</div>
      <div>reason</div>
    </div>
  )
};

const CombatTableData = () => {
  const { combatLogData } = useOverlayContext();
  const combatLines = combatLogData?.combatLines ?? [];
  return (
    <div className="table-data">
      <div className="row">
        {combatLines.map((combatLine, i) => (
          <CombatRow key={i} combatLine={combatLine} />
        ))}
      </div>
    </div>
  );
}

const CombatRow = ({ combatLine }: { combatLine: CombatLine }) => {
  const { overlayData } = useOverlayContext();
  if (overlayData.type !== 'combat table') { return (<></>); }

  switch (combatLine.type) {
    case 'damage-dealt':
      return (
        <>
          <TableCell className="damage-dealt-row damage-cell">{combatLine.damage}</TableCell>
          <TableCell className="damage-dealt-row mitigated-cell">({combatLine.mitigated})</TableCell>
          <TableCell className="damage-dealt-row target-cell">{combatLine.target}</TableCell>
          <TableCell className="damage-dealt-row from-cell">{combatLine.from}</TableCell>
        </>
      );
  }
  // return (<><TableCell>{combatLine.type}</TableCell><TableCell /><TableCell /><TableCell /></>);
};

const TableCell = ({ children, className }: { children?: ReactNode, className?: string }) => (
  <motion.div
    initial={{ height: 0, opacity: 0 }}
    animate={{ height: 'auto', opacity: 1 }}
    className={twMerge('cell', className)}
  >
    {children}
  </motion.div>
)
