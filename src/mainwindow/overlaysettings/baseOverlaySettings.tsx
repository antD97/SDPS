import OverlayData, { overlayNames, overlayStates } from "../../overlays/overlayData";
import { useMainWindowContext } from "../mainWindowContext";

export default function BaseOverlaySettings() {
  const { selectedMenu, overlays, updateSelectedOverlay } = useMainWindowContext();

  if (selectedMenu.id !== 'Overlay') {
    throw Error('BaseOverlaySettings should only be rendered when an overlay menu is selected.');
  }

  const selectedOverlay = overlays[selectedMenu.index];

  return (
    <div className="flex flex-col items-center gap-4">
      <label className="flex gap-2">
        Overlay:
        <select
          value={selectedOverlay.type}
          onChange={(event) => {
            const overlayType = event.target.value as OverlayData['type'];
            updateSelectedOverlay((prevOverlayData) => {
              switch (overlayType) {
                case 'empty': return { ...prevOverlayData, type: 'empty' };
                case 'combat table': return { ...prevOverlayData, type: 'combat table' };
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
            const selected = selectedOverlay.windowState === state
              ? 'bg-cyan-600'
              : 'bg-neutral-300 hover:bg-neutral-100 active:text-black active:bg-cyan-600';
            return (
              <button
                key={i}
                onClick={() => { updateSelectedOverlay((prevOverlayData) => ({ ...prevOverlayData, windowState: state })); }}
                className={`px-2 text-black ${first} ${last} ${selected}`}
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
