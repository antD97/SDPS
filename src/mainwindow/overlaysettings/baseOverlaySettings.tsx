import { initialCombatTableData } from '../../overlays/combatTable/combatTableConsts';
import { overlayNames, overlayStates } from '../../overlays/overlayConsts';
import { OverlayData } from '../../overlays/overlayTypes';
import { useMainWindowContext } from '../mainWindowContext';

export const BaseOverlaySettings = () => {
  const { selectedMenu, selectedOverlayData, setSelectedOverlayData } = useMainWindowContext();

  if (selectedMenu.id !== 'Overlay') {
    throw Error('BaseOverlaySettings should only be rendered when an overlay menu is selected.');
  }

  if (selectedOverlayData === null) { return (<></>); }

  return (
    <div className="flex flex-col items-center gap-4">
      <label className="flex gap-2">
        Overlay:
        <select
          value={selectedOverlayData.type}
          onChange={(event) => {
            const overlayType = event.target.value as OverlayData['type'];
            setSelectedOverlayData((prevOverlayData) => {
              switch (overlayType) {
                case 'empty': return { ...prevOverlayData, type: 'empty' };
                case 'combat table': return { ...initialCombatTableData, ...prevOverlayData, type: 'combat table' };
              }
            });
          }}
          className="px-1 rounded bg-cyan-600 text-black"
        >
          {Object.entries(overlayNames).map(([type, { longName }]) => (
            <option key={type} value={type} className="bg-white">
              {longName}
            </option>
          ))}
        </select>
      </label>

      <div className="flex gap-1">
        {
          overlayStates.map((state, i) => {
            const first = i === 0 && 'rounded-l-md';
            const last = i === overlayStates.length - 1 && 'rounded-r-md';
            const selected = selectedOverlayData.windowState === state
              ? 'bg-cyan-600'
              : 'bg-neutral-300 hover:bg-neutral-100 active:text-black active:bg-cyan-600';
            return (
              <button
                key={i}
                onClick={() => { setSelectedOverlayData((prevOverlayData) => ({ ...prevOverlayData, windowState: state })); }}
                className={`px-2 text-black ${first} ${last} ${selected} transition-colors duration-300`}
              >
                {state}
              </button>
            );
          })
        }
      </div>
    </div >
  );
}
