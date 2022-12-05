# SDPS

[![GitHub release](https://img.shields.io/github/downloads/antD97/SDPS/v2.5.0/total)](https://github.com/antD97/SDPS/releases/tag/v2.5.0)

SDPS is a handy tool for the game [Smite](https://smitegame.com/) that lets you track your in-game
damage and healing in real time. By jumping into a jungle practice match, SDPS makes it easy to 
compare different item builds. SDPS uses the in-game combat log, so it will work seamlessly with 
future updates containing balance changes, new gods, and new items.

As of v2.5.0, SDPS can also be used to create a text file that can be used as an
[OBS text source](https://obsproject.com/eu/kb/text-sources). See the
[OBS Source page](doc/obs-source.md) for a demonstration and documentation.

You can find the latest download [here](https://github.com/antD97/SDPS/releases/tag/v2.5.0).

Feel free to post any questions, bug reports, and feature requests to the
[issues page](https://github.com/antD97/SDPS/issues).

## Usage

1. Extract the zip file and run `SDPS.exe` to start the tool. Windows will likely display a message
   stating "Windows protected your PC", because the file is not recognized. Click "More info" and
   then "Run anyway" to launch the tool.
2. In the Smite chat, enter `/combatlog toggle piped` to let SDPS read your combat log. This command
   has to be typed in once per game launch.
3. The first row of healing or damage will start the timer used to calculate DPS, and your DPS will
   continuously be reported every time a row is added.
4. The "Reset" button resets the total columns values and resets the DPS timer. The "Clear Table"
   button removes all rows from the table and resets tracking.
   - The "Reset" and "Clear Table" buttons do not work if there is potentially hidden combat. The 
     last row in the table will display with asterisks and a darker color to indicate when this 
     is the case. The easiest way to clear potentially hidden combat is to start backing and
     then cancelling it immediately. This will update the combat log and the "Reset" and "Clear 
     Table" buttons will work again. See the
     [Hidden Combat page](doc/hidden-combat.md) for a detailed explanation.
6. If you click "Reset", your next tick of damage will reset the DPS timer. Alternatively, if you
   click "Clear Table", the table will both be cleared and the timer reset.
7. Whenever you want to record DPS or total damage, be sure to click "Reset" and then do another
   tick of damage to reveal any damage ticks that may have been hidden. Read the last bullet in the
   notes section for an explanation.

SDPS will try to automatically find your in-game name when the first instance of damage appears
in the combat log. If your in-game name is set incorrectly, the reset button next to the
displayed name will let you set your name again.

## Notes

- If you hover over each of the buttons and checkboxes in the window, you can reveal its shortcut.
  By only displaying columns that are relevant to you and clicking the button in the top left
  corner to hide the sidebar, you can resize the window and use the shortcuts so that the tool is
  as out of your way as possible.

- In the "Odin bots" section of jungle practice, there are three Odin bots to the immediate left.
  These bots are handy for DPS calculations. The first one matches your character's level, the
  second one is level one, and the third one is level twenty. There are also buttons to the right
  that let you change their protections.

- If you are in a match, and the DPS log says "End", try typing the command
  `/combatlog toggle piped` into Smite chat again. You may have accidentally entered it twice which
  prevents SDPS from monitoring your combat log.

- What do the darker rows with asterisks mean? Why am I not able to hit reset while that row is 
  there? Rows with asterisks shows that there is potentially more combat to show after that 
  row that has not appeared yet in the combat log. The easiest way to clear potentially hidden
  combat is to start backing and then cancelling it immediately. To learn why this is necessary 
  see the [Hidden Combat page](doc/hidden-combat.md).

## Building

Using JDK 15, run `./gradlew createBundledExe`. The output is located in `build/bundledExe`.

## Screenshots

[![Screenshot 1](doc/img/screenshot1.png)](doc/img/screenshot1.png)  
[![Screenshot 2](doc/img/screenshot2.png)](doc/img/screenshot2.png)

## Copyright and License

Copyright © 2021-2022 antD97  
Licensed under the [MIT License](LICENSE)
