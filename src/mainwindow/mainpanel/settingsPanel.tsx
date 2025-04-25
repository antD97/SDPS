import { useMainWindowContext } from '../mainWindowContext';

export const SettingsPanel = () => {
  const { combatLogData } = useMainWindowContext();

  const filename = combatLogData?.filename ?? null;
  const ign = combatLogData?.ign ?? null;
  const potentialHiddenCombat = combatLogData?.potentialHiddenCombat ?? null;

  return (
    <div className="flex flex-col items-center justify-center overflow-auto">
      <h1>TODO Settings</h1>
      <div>filename: {filename}</div>
      <div>ign: {ign}</div>
      <div>potential hidden combat: {potentialHiddenCombat}</div>
    </div>
  );
}
