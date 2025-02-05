import Container from "../../components/ui/container";
import OverlayData from "../../overlays/overlayData";
import { useMainWindowContext } from "../mainWindowContext";
import CombatTableSettings from "../overlaysettings/combatTableSettings";
import CommonOverlaySettings from "../overlaysettings/commonOverlaySettings";
import EmptyOverlaySettings from "../overlaysettings/emptyOverlaySettings";
import AboutPanel from "./aboutPanel";
import SettingsPanel from "./settingsPanel";

export default function MainPanel() {
  const { state: { selectedMenu, overlays } } = useMainWindowContext()
  switch (selectedMenu.id) {
    case 'About': return (<AboutPanel />);
    case 'Settings': return (<SettingsPanel />);
    case 'Overlay':
      return (
        <Container>
          <CommonOverlaySettings />
          <OverlaySettings type={overlays[selectedMenu.index].type} />
        </Container>
      );
  };
}

function OverlaySettings({ type }: { type: OverlayData['type'] }) {
  switch (type) {
    case 'empty': return (<EmptyOverlaySettings />);
    case 'combat table': return (<CombatTableSettings />);
  }
}
