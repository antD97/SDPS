type CombatLine = {
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
