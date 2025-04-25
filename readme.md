# Tauri + React + Typescript

This template should help get you started developing with Tauri, React and Typescript in Vite.

## Recommended IDE Setup

- [VS Code](https://code.visualstudio.com/) + [Tauri](https://marketplace.visualstudio.com/items?itemName=tauri-apps.tauri-vscode) + [rust-analyzer](https://marketplace.visualstudio.com/items?itemName=rust-lang.rust-analyzer)

`npm install`  
`npm run tauri dev`

Update oss attribution: `npx oss-attribution-generator generate-attribution`  
must be moved to `src-tauri\resources\oss-attribution`

known issues:
- "hidden combat"; seems to not be an issue if the combat tab is selected?
- nonGodNames in combatLine.ts may need updating

todo:
- DIT_Death
- DIT_Assist
- DIT_KillingBlow minions/gods/towers?
- DIT_Experience?
- DIT_ManaRestore?
- DIT_Level?
- IET_AbilityPurchase?
