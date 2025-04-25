
export type CombatLine = {
  type: 'start'
} | {
  type: 'end';
} | {
  type: 'damage-dealt'; // set PHC
  time: number;
  target: string;
  from: string;
  damage: number;
  mitigated: number;
  text: string;
} | {
  type: 'damage-received'; // set PHC
  time: number;
  source: string;
  from: string;
  damage: number;
  mitigated: number;
  text: string;
} | {
  type: 'heal-dealt'; // set PHC
  time: number;
  target: string;
  from: string;
  amount: number;
  text: string;
} | {
  type: 'heal-received'; // set PHC
  time: number;
  source: string;
  from: string;
  amount: number;
  text: string;
} | {
  type: 'kill-player';
  time: number;
  target: string;
} | {
  type: 'kill-npc';
  time: number;
  target: string;
} | {
  type: 'death';
  time: number;
} | {
  type: 'assist';
  time: number;
} | {
  type: 'experience'; // hides currency
  time: number;
} | {
  type: 'currency'; // probably can hide experience
  time: number;
} | {
  type: 'level'; // can be hidden :/ by ^
  time: number;
} | {
  type: 'ability-purchase'; // clear PHC
  time: number;
} | {
  type: 'cast-start'; // clear PHC
  time: number;
} | {
  type: 'recall-end'; // gets hidden...
  time: number;
};

const damageTypes = [
  'DIT_Damage',
  'DIT_CritDamage',
  'DIT_Backstab',
  'DIT_HolyCrit'
];

const nonGodNames = [
  'Gold Fury',
  'Fire Giant',
  'Chaos Swordsman',
  'Chaos Brute',
  'Order Swordsman',
  'Order Archer',
  'Harpy',
  'Elder Harpy',
  'Manticore',
  'Alpha Manticore',
  'Spirit Satyr',
  'Elder Satyr',
  'Centaur',
  'Chief Centaur',
  'Chimera',
  'Alpha Chimera',
  'Chaos Bastion',
  'Order Bastion', // TODO check
  'Chaos Tower',
  'Order Tower', // TODO check
  'Chaos Phoenix',
  'Order Phoenix', // TODO check
  'Chaos Titan',
  'Order Titan', // TODO check
];

export function parseCombatLines(
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
        if (type && damageTypes.includes(type)) {
          potentialHiddenCombat = true;

          // track ign
          if (ign === null && source && !nonGodNames.includes(source)) { ign = source; }

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
