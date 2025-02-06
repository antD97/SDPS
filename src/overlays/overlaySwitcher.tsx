import { FC } from "react";
import CombatTable from "./combattable/combatTable";
import EmptyOverlay from "./emptyoverlay/emptyOverlay";
import { useOverlayContext } from "./overlayContext";

const OverlaySwitcher: FC = () => {
  const { type } = useOverlayContext();
  switch (type) {
    case 'empty': return (<EmptyOverlay />);
    case 'combat table': return (<CombatTable />);
  }
}
export default OverlaySwitcher;
