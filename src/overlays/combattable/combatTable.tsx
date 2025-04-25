import { motion } from "framer-motion";
import { ReactNode } from "react";
import { CombatLine } from "../../mainwindow/combatLine";
import { useOverlayContext } from "../overlayContext";

function CombatTable() {
  const { combatLogData } = useOverlayContext();
  const combatLines = combatLogData?.combatLines ?? [];

  return (
    <div className="grid bg-neutral-900/97 p-4 overflow-hidden rounded-2xl">
      <div className="grid grid-cols-4 font-bold pb-1 border-b">
        <div>damage</div>
        <div>mitigated</div>
        <div>target</div>
        <div>reason</div>
      </div>
      <div className="flex flex-col justify-end overflow-hidden">
        <div className="grid grid-cols-4">
          {combatLines.map((combatLine, i) => (<CombatRow key={i} combatLine={combatLine} />
          ))}
        </div>
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
    transition={{ duration: 0.3 }}
  >
    {children}
  </motion.div>
)

export default CombatTable;
