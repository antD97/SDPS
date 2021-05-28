 ---------- SDPS v2.3 ----------

SDPS is a handy tool for the game Smite (https://smitegame.com/) that lets you track your in-game
damage in real time. By jumping into a jungle practice match, SDPS makes it easy to compare the
damage of different builds. SDPS uses the in-game combat log, so it will work seamlessly with future
updates containing balance changes, new gods, and new items.

Release: https://github.com/antD97/SmiteDPS/releases/tag/v2.3
For a more detailed readme: https://github.com/antD97/SDPS/tree/v2.3

 --- Usage ---

1. Extract the zip file and run `SDPS.exe` to start the tool. Windows will likely display a message
   stating "Windows protected your PC", because the file is not recognized. Click "More info" and
   then "Run anyway" to launch the tool.

2. In the Smite chat, enter `/combatlog toggle piped` to let SDPS see your combat log. This command
   has to be typed in once per game launch.

3. Your first tick of damage will start the timer used to calculate DPS, and your DPS will
   continuously be reported every time you do more damage. DPS is calculated from your first tick of
   damage to the most recent.

4. If you click "Reset", your next tick of damage will reset the DPS timer. Alternatively, if you
   click "Clear Table", the table will both be cleared and the timer reset.

5. Whenever you want to record DPS or total damage, be sure to click "Reset" and then do another
   tick of damage to reveal any damage ticks that may have been hidden. Read the last bullet in the
   detailed readme's notes section for an explanation.

SDPS will automatically fill in the in-game name field when the first tick of damage appears in the
combat log. If your in-game name is set incorrectly, the reset button next to the displayed name
will let you set your name again.

 --- Copyright and License ---

Copyright © 2021 antD97  
Licensed under the MIT License https://antD.mit-license.org/
