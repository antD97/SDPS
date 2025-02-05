import { overlayNames, overlayStates } from "../../overlays/overlayData";

export default function CommonOverlaySettings() {

  return (
    <div className="flex flex-col items-center gap-4">
      <label className="flex gap-2">
        Overlay:
        <select className="px-1 rounded bg-cyan-600 text-black">
          {Object.entries(overlayNames).map(([key, { longName }]) => (
            <option key={key} value={key} className="bg-white">
              {longName}
            </option>
          ))}
        </select>
      </label>

      <div className="flex gap-[2px]">
        {
          overlayStates.map((state, i) => {
            const first = i === 0 && 'rounded-l';
            const last = i === overlayStates.length - 1 && 'rounded-r';
            const selected = false
              ? 'text-black bg-cyan-600'
              : 'bg-neutral-800 hover:bg-neutral-700 active:text-black active:bg-cyan-600';
            return (
              <button
                key={i}
                className={`px-2 ${first} ${last} ${selected}`}
              >
                {state}
              </button>
            );
          })
        }
      </div>
    </div>
  );
}
