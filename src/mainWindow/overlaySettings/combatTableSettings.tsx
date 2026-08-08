import { useImmer } from 'use-immer';
import { H } from '../../components/header';
import { useMainWindowContext } from '../mainWindowContext';

export const CombatTableSettings = () => {
  const { selectedOverlayData, setSelectedOverlayData } = useMainWindowContext();
  const [styleInputFocused, setStyleInputFocused] = useImmer(false);

  if (!selectedOverlayData || selectedOverlayData.type !== 'combat table') { return null; }

  return (
    <div className="flex flex-col gap-4">

      <H level="1">Combat Table</H>

      combat table settings...

      <H level="2" className={!styleInputFocused ? 'opacity-50' : ''}>Styles</H>
      <textarea
        spellCheck={false}
        onFocus={() => { setStyleInputFocused(true); }}
        onBlur={() => { setStyleInputFocused(false); }}
        value={selectedOverlayData.styles}
        onChange={(e) => {
          const value = e.target.value;
          setSelectedOverlayData((draft) => {
            if (draft.type !== 'combat table') { return; }
            draft.styles = value;
          });
        }}
        className="min-h-48 border opacity-50 font-mono p-2 focus:opacity-100 focus:border-cyan-700 focus:outline-none"
      />
    </div>
  );
}
