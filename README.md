# SpeedRunIGTAtHome
In-game timer mod for Minecraft versions where SpeedRunIGT is unavailable (i.e. 1.2.5)

## How it works
Every tick, the mod increments a counter, which is then converted to hh:mm:ss.000 format and drawn on the screen.

When the game is saved, the tick counter is saved in `ticks.txt` within the world save folder.

The timer pauses when the game is paused, but not during dimension loading screens, because the game actually runs ticks during that time.

## How to install
In MultiMC/Prism, go to `Edit -> Version -> Add to Minecraft .jar` and select the zip file.