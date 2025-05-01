import { EventCallback } from '@tauri-apps/api/event';
import { Updater } from 'use-immer';
import { CombatLogData } from '../mainWindow/mainWindowTypes';
import { damageTypes, npcNames } from './combatMonitorConsts';

export function combatMonitor(
  setCombatLogData: Updater<CombatLogData | null>
): EventCallback<{ Combat: [string, [number, string][]]; }> {
  return (event) => {
    const { Combat: [filename, lines] } = event.payload;
    const isNewFile = lines[0][0] === 0;

    setCombatLogData((draft) => {

      const parsedCombatLines = parseCombatLines(
        draft?.ign ?? null,
        lines.map((lineData) => lineData[1])
      );

      if (parsedCombatLines.logType === 'piped') {
        throw new Error('Piped log type not supported. Use `/combatlog toggle` not `/combatlog toggle piped`.');
      }

      const { ign, combatLines, potentialHiddenCombat } = parsedCombatLines;

      const debugLines = lines.map(([_, line]) => line);

      if (isNewFile || draft === null) {
        return { ign, filename, debugLines, combatLines, potentialHiddenCombat };
      } else {
        draft.debugLines.push(...debugLines);
        draft.combatLines.push(...combatLines);
      }
    });
  }
}

function parseCombatLines(
  ign: string | null,
  lines: string[]
): {
  logType: 'not piped';
  ign: string | null;
  combatLines: CombatLine[];
  potentialHiddenCombat: boolean;
} | {
  logType: 'piped'
} {

  // bad log type
  if (lines.some((line) => !line.includes('{'))) { return { logType: 'piped' }; }

  let potentialHiddenCombat: boolean = false;

  const combatLines: (CombatLine | { type: 'ignore' })[] = lines

    // remove leading comma and brackets
    .map((line) => line.trim().replace(/^,/, '').replace(/^{/, '').replace(/}$/, ''))

    // extract key-value pairs as record
    .map((line) => Object.fromEntries(line.split(',').map((lineDataEntry) => {
      const [key, value] = lineDataEntry.split(':').
        map((part) => part.trim().replace(/^"/, '').replace(/"$/, ''));
      return [key, value];
    })) as Partial<Record<string, string>>)

    // create combat line
    .map(({ eventType, type, itemname: from, time, sourceowner: source, target, value1, value2, text }) => {

      // start
      if (eventType === 'start') { return { type: 'start' }; }

      // end
      else if (eventType === 'end') { return { type: 'end' }; }

      // combatmsg
      else if (eventType === 'combatmsg') {

        // damage
        if (type && (damageTypes as readonly string[]).includes(type)) {
          potentialHiddenCombat = true;

          // track ign
          if (ign === null && source && !(npcNames as readonly string[]).includes(source)) { ign = source; }

          if (source === ign) {
            return {
              type: 'damage-dealt',
              time: parseFloat(time!),
              target: target!,
              from: from!,
              damage: parseInt(value1!),
              mitigated: parseInt(value2!),
              text: text!
            };
          } else if (target === ign) {
            return {
              type: 'damage-received',
              time: parseFloat(time!),
              source: source!,
              from: from!,
              damage: parseInt(value1!),
              mitigated: parseInt(value2!),
              text: text!
            };
          }
        }

        // healing
        else if (type === 'DIT_Healing') {
          potentialHiddenCombat = true;

          if (source === ign) {
            return {
              type: 'heal-dealt',
              time: parseFloat(time!),
              target: target!,
              from: from!,
              amount: parseInt(value1!),
              text: text!
            };
          } else if (target === ign) {
            return {
              type: 'heal-received',
              time: parseFloat(time!),
              source: source!,
              from: from!,
              amount: parseInt(value1!),
              text: text!
            };
          }
        }

        // kill player
        else if (type === 'DIT_KillingBlow') {

        }

        // cc lines that create potential hidden combat scenarios?
        else if (type && ['DIT_Status', 'DIT_NONE', 'DIT_CrowdControl'].includes(type)) {
          potentialHiddenCombat = true;
          return { type: 'ignore' };
        }
      }

      // all other lines...
      potentialHiddenCombat = false;
      return { type: 'ignore' };
    });

  return {
    logType: 'not piped',
    ign,
    combatLines: combatLines.filter((combatLine) => combatLine.type !== 'ignore') as CombatLine[],
    potentialHiddenCombat
  };
}
