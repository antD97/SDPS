import { useOverlayContext } from "../overlayContext";

function CombatTable() {
  const { combatLogData } = useOverlayContext();
  const combatLines = combatLogData?.combatLines ?? [];

  return (
    <div className="flex flex-col justify-end overflow-hidden bg-neutral-900/97">
      combat table
      <div className="flex flex-col">
        {combatLines.map((combatLine) => {

          const parts: (string | number)[] = [combatLine.type];

          switch (combatLine.type) {
            case 'end': break;
            case 'damage-dealt':
              parts.push(combatLine.damage);
              parts.push(combatLine.mitigated);
              parts.push(combatLine.reason);
              parts.push(combatLine.time);
              break;
            case 'damage-received':
              parts.push(combatLine.damage);
              parts.push(combatLine.mitigated);
              parts.push(combatLine.reason);
              parts.push(combatLine.time);
              break;
            case 'heal-dealt':
              parts.push(combatLine.amount);
              parts.push(combatLine.reason);
              parts.push(combatLine.time);
              break;
            case 'heal-received':
              parts.push(combatLine.amount);
              parts.push(combatLine.reason);
              parts.push(combatLine.time);
              break;
          }

          return (<div>{parts.join(' | ')}</div>);
        })}
      </div>
    </div>
  );
}

export default CombatTable;
