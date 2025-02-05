import { BaseOverlayData } from "../../overlays/overlayData";

export interface CombatTableData extends BaseOverlayData {
  type: 'combat table'
}

export default function CombatTableSettings() {
  return (
    <div>
      <h1 className="text-2xl self-center border-b border-cyan-600">Empty Overlay</h1>
      combat table
    </div>
  );
}
