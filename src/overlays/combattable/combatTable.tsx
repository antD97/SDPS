import { motion } from 'framer-motion';
import { ReactNode, useMemo } from 'react';
import { useOverlayContext } from '../overlayContext';

export const CombatTable = () => {
  const { overlayData } = useOverlayContext();
  if (overlayData.type !== 'combat table') { return (<></>); }
  const { styles: { window } } = overlayData;

  const borderRadius = useMemo(() => (
    `${window.corners.value !== undefined ? window.corners.value : window.corners.default}px`
  ), [window.corners.value]);

  const paddingSize = useMemo(() => {
    const size = `${window.padding.value !== undefined ? window.padding.value : window.padding.default}px`;
    return { x: { width: size }, y: { height: size } };
  }, [window.padding.value]);

  const backgroundColor = useMemo(() => (
    window.backgroundColor.value !== undefined ? window.backgroundColor.value : window.backgroundColor.default
  ), [window.backgroundColor.value]);

  return (
    <div className="flex flex-col overflow-hidden" style={{ borderRadius }} >
      <div style={{ ...paddingSize.y, backgroundColor }} />
      <div className="grow flex overflow-hidden">
        <div style={{ ...paddingSize.x, backgroundColor }} />

        <div className="grow flex flex-col overflow-hidden">
          <CombatTableHeader />
          <CombatTableData />
        </div>

        <div style={{ ...paddingSize.x, backgroundColor }} />
      </div>
      <div style={{ ...paddingSize.y, backgroundColor }} />
    </div>
  );
}

const CombatTableHeader = () => {
  const { overlayData } = useOverlayContext();
  if (overlayData.type !== 'combat table') { throw new Error('Bad overlay data'); }

  const { styles: { window, headers } } = overlayData;
  const backgroundColor: string = useMemo(() => {
    if (headers.backgroundColor.value !== undefined) { return headers.backgroundColor.value; }
    else if (headers.backgroundColor.default !== '') { return headers.backgroundColor.default; }
    else if (window.backgroundColor.value !== undefined) { return window.backgroundColor.value; }
    else { return window.backgroundColor.default; }
  }, [headers.backgroundColor, window.backgroundColor]);

  return (
    <div
      className="grid grid-cols-4 font-bold pb-1 border-b"
      style={{ backgroundColor }}
    >
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
    <div className="flex flex-col justify-end overflow-hidden">
      <div className="grid grid-cols-4">
        {combatLines.map((combatLine, i) => (<CombatRow key={i} combatLine={combatLine} />
        ))}
      </div>
    </div>
  );
}

const CombatRow = ({ combatLine }: { combatLine: CombatLine }) => {
  switch (combatLine.type) {
    case 'damage-dealt':
      return (
        <>
          <TableCell>{combatLine.damage}</TableCell>
          <TableCell>({combatLine.mitigated})</TableCell>
          <TableCell>{combatLine.target}</TableCell>
          <TableCell>{combatLine.from}</TableCell>
        </>
      );
  }
  return (<><TableCell>{combatLine.type}</TableCell><TableCell /><TableCell /><TableCell /></>);
};

const TableCell = ({ children }: { children?: ReactNode }) => (
  <motion.div
    initial={{ height: 0, opacity: 0 }}
    animate={{ height: 'auto', opacity: 1 }}
    className="truncate"
  >
    {children}
  </motion.div>
)
