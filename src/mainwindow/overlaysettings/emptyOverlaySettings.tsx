import { BaseOverlayData } from "../../overlays/overlayData";

export interface EmptyOverlayData extends BaseOverlayData {
  type: 'empty'
}

export default function EmptyOverlaySettings() {
  return (
    <div className="grow flex items-center justify-center">
      Select an overlay above.
    </div>
  );
}
