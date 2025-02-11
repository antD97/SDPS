export type CombatLine = {
  type: 'end'
} | {
  type: 'damage-dealt',
  time: number,
  damage: number,
  mitigated: number,
  reason: string
} | {
  type: 'damage-received',
  time: number,
  damage: number,
  mitigated: number,
  reason: string
} | {
  type: 'heal-dealt',
  time: number,
  amount: number,
  reason: string
} | {
  type: 'heal-received',
  time: number,
  amount: number,
  reason: string
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
  'Chaos Tower',
  'Chaos Titan',
  'Chaos Phoenix'
]

export function parseCombatLines(
  ign: string | null,
  lines: string[]
): {
  ign: string | null;
  combatLines: CombatLine[];
  potentialHiddenCombat: boolean;
} {

  let potentialHiddenCombat: boolean = false;

  const combatLines: (CombatLine | { type: 'ignore' })[] = lines.map((line) => {
    line = line.trim();

    // end line
    if (line == 'end' || line == ',{"eventType":"end"}') {
      return { type: 'end' };
    }

    const lineParts = line.includes('{')
      // log type that uses commas
      ? line.split(',').map((part) => part.trim().split(':')[1].replace(/^"/, '').replace(/'&/, ''))
      // log type that uses vertical bars
      : line.split('|').map((part) => part.includes('=') ? part.split('=')[1] : part);

    const type = lineParts[1];

    // damage
    if (damageTypes.includes(type)) {
      potentialHiddenCombat = true;

      const source = lineParts[9];
      const target = lineParts[11];

      // track ign
      if (ign === null && !nonGodNames.includes(source)) {
        ign = source;
      }

      const time = parseFloat(lineParts[8]);
      const damage = parseInt(lineParts[12]);
      const mitigated = parseInt(lineParts[13]);
      const reason = lineParts[7];

      if (source === ign) {
        return { type: 'damage-dealt', time, damage, mitigated, reason };
      } else if (target === ign) {
        return { type: 'damage-received', time, damage, mitigated, reason };
      }
    }

    // healing
    else if (type === 'DIT_Healing') {
      potentialHiddenCombat = true;

      const source = lineParts[9];
      const target = lineParts[11];
      const time = parseFloat(lineParts[8]);
      const amount = parseInt(lineParts[12]);
      const reason = lineParts[7] === '' ? 'Lifesteal' : lineParts[7];

      if (source === ign) {
        return { type: 'heal-dealt', time, amount, reason };
      } else if (target === ign) {
        return { type: 'heal-received', time, amount, reason };
      }
    }

    // cc lines that create potential hidden combat scenarios?
    else if (['DIT_Status', 'DIT_NONE', 'DIT_CrowdControl'].includes(type)) {
      potentialHiddenCombat = true;
      return { type: 'ignore' };
    }

    // all other lines...
    potentialHiddenCombat = false;
    return { type: 'ignore' };
  });

  return {
    ign,
    combatLines: combatLines.filter((combatLine) => combatLine.type !== 'ignore') as CombatLine[],
    potentialHiddenCombat
  };
}
