# Smite DPS

A handy tool to calculate DPS in the game [Smite](https://smitegame.com/). It uses the in-game
combat log so this tool should always work, even with future updates containing new items and gods.

Releases can be found [here](https://github.com/antD97/SmiteDPS/releases).

## Screenshots

![Screenshot 1](pics/screenshot1.png) ![Screenshot 2](pics/screenshot2.png)

## Usage

1. Join a jungle practice and type `/combatlog toggle piped` to enable combat logging to file. This
   only has to be done once every game launch.
2. Run `SmiteDPS.bat` to start the program.
3. Enter your Smite in-game name when prompted or enter your name in the `in-game_name.txt` file to
   keep it saved.
4. The program should be able to find your combat log file, but if it can't, it'll ask you to find
   it. It should be located in `Documents\My Games\Smite\BattleGame\Logs`. From there you'll want to
   select the `CombatLog` file with the largest number.
5. After that, it will begin tracking your damage done in that match. When you do your first tick of
   damage, it will start the timer used to calculate DPS. From then on, any time you do damage, it
   will print out your DPS from that initial hit to your last hit.
6. If you want to reset the DPS timer, hit enter on the console window.

## Notes

- In the "Odin bots" section of jungle practice, there are three Odin bots to the immediate left.
  These bots are handy for DPS calculations. The first one matches your character's level, the
  second one is level one, and the third one is level twenty. There are also buttons to the right
  that let you control the amount of protections they have.
- If there are multiple ticks of damage that occur at the same time (e.g. Qin's Sais), the log file
  annoyingly won't be updated until the next tick of damage or the file is closed. The late timing
  won't mess up the DPS calculation, but it can make the output visually confusing. If the DPS timer
  is reset when there is output that has yet to be printed, when it does print it'll include a
  message to indicate that it was from the previous DPS timer.  
  ![Screenshot 3](pics/screenshot3.png)  
  This screenshot should make it clearer. Qin's Sais and Shifters of Seasons occur at the same time
  as the previous basic attack and doesn't update when it should.

## Building

Run `./gradlew jar` on Windows or `gradle jar` on Linux. The output is located in `build/libs`.

## Copyright and License
Copyright © 2021 antD97  
Licensed under the [MIT License](LICENSE).
