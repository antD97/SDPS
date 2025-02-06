import OverlayData, { overlayNames } from "../overlays/overlayData";

export default function updateOverlayNames(overlays: OverlayData[]): OverlayData[] {
  return overlays.map((overlay, i, allOverlays) => {
    // if there is only one overlay of this type
    if (allOverlays.filter((o) => o.type === overlay.type).length <= 1) {
      return { ...overlay, overlayName: overlayNames[overlay.type].shortName };
    }
    // if there are multiple overlays of this type
    else {
      const numPrecedingSameOverlays = allOverlays.filter((o, j) => j < i && o.type === overlay.type).length;
      return {
        ...overlay,
        overlayName: `${overlayNames[overlay.type].shortName} ${numPrecedingSameOverlays + 1}`
      }
    }
  });
}
