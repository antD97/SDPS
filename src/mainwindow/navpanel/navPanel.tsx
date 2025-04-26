import { getAllWindows } from '@tauri-apps/api/window';
import { H } from '../../components/header';
import { overlayNames } from '../../overlays/overlayConsts';
import { useMainWindowContext } from '../mainWindowContext';
import { NavButton } from './navButton';

export const NavPanel = () => {
  const { selectedMenu, overlays, setSelectedMenu, newOverlay } = useMainWindowContext();

  const mainBtnList: ['About', 'Presets', 'Settings'] = ['About', 'Presets', 'Settings'];

  return (
    <div className="flex flex-col items-stretch border-r-1 border-neutral-700 text-center">

      <H level="2" className="border-none px-8 py-1 text-cyan-600">SDPS</H>
      {
        mainBtnList.map((btnName, i) => (
          <NavButton
            key={i}
            onClick={() => setSelectedMenu({ id: btnName })}
            selected={selectedMenu.id === btnName}
          >
            {btnName}
          </NavButton>
        ))
      }

      <H level="2" className="text-cyan-600 px-8 py-1 mt-1 border-b-0 border-t border-neutral-700">Overlays</H>
      {
        overlays.map(({ windowLabel, type, windowState }, i) => {
          const selected = selectedMenu.id === 'Overlay' && selectedMenu.index === i;
          const suffix = (overlays.some(({ type: t }, j) => j !== i && t === type))
            ? ` ${overlays.filter(({ type: t }, j) => j < i && t === type).length + 1}`
            : '';
          return (
            <NavButton
              key={windowLabel}
              onClick={() => setSelectedMenu({ id: 'Overlay', index: i })}
              onClose={async () => {
                (await getAllWindows()).find((window) => window.label === windowLabel)!.close();
              }}
              selected={selected}
              className={windowState === 'hide' ? 'line-through text-white/50' : ''}
            >
              {`${overlayNames[type].shortName}${suffix}`}
            </NavButton>
          )
        })
      }
      {overlays.length < 16 && (<NavButton onClick={newOverlay}>+Overlay</NavButton>)}
    </div >
  );
}
