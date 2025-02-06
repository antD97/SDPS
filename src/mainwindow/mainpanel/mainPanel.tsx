import Container from "../../components/ui/container";
import OverlayData from "../../overlays/overlayData";
import { useMainWindowContext } from "../mainWindowContext";
import BaseOverlaySettings from "../overlaysettings/baseOverlaySettings";
import CombatTableSettings from "../overlaysettings/combatTableSettings";
import EmptyOverlaySettings from "../overlaysettings/emptyOverlaySettings";
import AboutPanel from "./aboutPanel";
import PresetsPanel from "./presetsPanel";
import SettingsPanel from "./settingsPanel";

export default function MainPanel() {
  const { selectedMenu, overlays } = useMainWindowContext();

  switch (selectedMenu.id) {
    case 'About': return (<AboutPanel />);
    case 'Presets': return (<PresetsPanel />);
    case 'Settings': return (<SettingsPanel />);
    case 'Overlay':
      return (
        <Container>
          <BaseOverlaySettings />
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
