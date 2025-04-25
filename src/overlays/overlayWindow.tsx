import { AdjustPanel } from './adjustPanel';
import { CombatTable } from './combatTable/combatTable';
import { EmptyOverlay } from './emptyOverlay/emptyOverlay';
import { useOverlayContext } from './overlayContext';

export const OverlayWindow = () => {
  const { overlayData: { windowState, type } } = useOverlayContext();

  if (windowState === 'hide') { return (<></>); }

  return (
    <main className="min-h-screen h-screen max-h-screen min-w-screen w-screen max-w-screen grid text-white select-none">
      {windowState === 'adjust' && <AdjustPanel />}
      <OverlaySwitcher type={type} />
    </main>
  );
}

const OverlaySwitcher = ({ type }: { type: OverlayData['type'] }) => {
  switch (type) {
    case 'empty': return (<EmptyOverlay />);
    case 'combat table': return (<CombatTable />);
  }
};
