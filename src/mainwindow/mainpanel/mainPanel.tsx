import { Container } from '../../components/container';
import { OverlayData } from '../../overlays/overlayTypes';
import { useMainWindowContext } from '../mainWindowContext';
import { BaseOverlaySettings } from '../overlaySettings/baseOverlaySettings';
import { CombatTableSettings } from '../overlaySettings/combatTableSettings';
import { EmptyOverlaySettings } from '../overlaySettings/emptyOverlaySettings';
import { AboutPanel } from './aboutPanel';
import { PresetsPanel } from './presetsPanel';
import { SettingsPanel } from './settingsPanel';

export const MainPanel = () => {
  const { selectedMenu, selectedOverlayData } = useMainWindowContext();

  switch (selectedMenu.id) {
    case 'About': return (<AboutPanel />);
    case 'Presets': return (<PresetsPanel />);
    case 'Settings': return (<SettingsPanel />);
    case 'Overlay':
      return (
        <Container>
          <BaseOverlaySettings />
          <OverlaySettings type={selectedOverlayData!.type} />
        </Container>
      );
  };
}

const OverlaySettings = ({ type }: { type: OverlayData['type'] }) => {
  switch (type) {
    case 'empty': return (<EmptyOverlaySettings />);
    case 'combat table': return (<CombatTableSettings />);
  }
}
