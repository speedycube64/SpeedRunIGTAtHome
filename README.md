# SpeedRunIGTAtHome
In-game timer mod for Minecraft versions where SpeedRunIGT is unavailable (i.e. 1.2.5). Created with CTM speedrunning in mind.

## How it works
Every tick, the mod increments a counter, which is then converted to hh:mm:ss.000 format and drawn on the screen.

When the game is saved, the tick counter and timer state (running or not running) is saved in `ticks.txt` within the world save folder.

The timer pauses when the game is paused, but not during dimension loading screens, because the game actually runs ticks during that time. The timer will permanently stop upon placement of an optional "stopping block" (i.e., finishing the monument in CTM speedruns).

## How to install
In MultiMC/Prism, go to `Edit -> Version -> Add to Minecraft .jar` and select the zip file.

Optionally, put a file called `stopping_block.txt` in your `.minecraft` folder with the block ID of your desired "stopping" block (e.g., 57 for a diamond block).