import { getCurrentWindow } from "@tauri-apps/api/window";
import { useEffect, useState } from "react";
import { GoArrowDownLeft, GoArrowDownRight, GoArrowUpLeft, GoArrowUpRight } from "react-icons/go";
import { useOverlayContext } from "./overlayContext";

function DragOverlay() {
  const [{ windowState }] = useOverlayContext();
  const window = getCurrentWindow();

  const [windowSize, setWindowSize] = useState<{ width: number, height: number }>({ width: 0, height: 0 });
  useEffect(() => {
    window.innerSize().then(size => setWindowSize(size));
  });
  window.innerSize

  return windowState === 'draggable' ? (
    <div
      onMouseDown={(e) => { e.stopPropagation(); window.startDragging(); }}
      className="absolute w-full h-full flex items-center justify-center bg-neutral-900/95 border-4 border-cyan-600 text-center text-2xl"
    >
      Adjust
      {windowSize.width > 160 && windowSize.height > 160 && (
        <div className="w-full h-full absolute grid grid-cols-2 text-4xl">
          {
            [(<GoArrowUpLeft />), (<GoArrowUpRight />), (<GoArrowDownLeft />), (<GoArrowDownRight />)]
              .map((arrow, i) => (
                <div key={i} className="flex items-center justify-center">
                  {arrow}
                </div>
              ))
          }
        </div>
      )}
    </div>
  ) : (<></>);
}

export default DragOverlay;
