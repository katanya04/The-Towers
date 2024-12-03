A Minecraft The Towers 1.8 plugin, based on the one by Pato14: [Amazing Towers](https://www.spigotmc.org/resources/amazingtowers-1-8-x-1-12-x-bungee-mode-thetowers-minigame-recoded.26858/).

## If you want more features
You can look out for 2 of our complement plugins specifically made for this Fork of Amazing Towers: 
- [RegionPlugin](https://github.com/nicoliee/RegionPlugin).
- [TowersBot](https://github.com/nicoliee/TowersBot).

## Explanation
What started as a few bug fixes on the original plugin, ended up becoming a completely different plugin. Main key features added:
- Multiple instances/games in one server.
- Customizable kits.
- Customizable number of teams (previously hardcoded to support 2 teams only).
- Storing and retrieving players statistics from a SQL database.
- In-game menus to modify the settings of each instance.
- A "bedwars" type of game mode (instead of scoring points to win, each team has an amount of health points, and when 0 hp is reached, players don't respawn after dying).
- More subcommands, a timer...

This is still a work in progress rathen than a release, bugs are expected and I plan on adding more features in the future.
The only supported version is 1.8, as there are quite a few methods that rely on NMS, althought I used reflection to try to get around this issue so they may work in more versions. Nonetheless, these modules of code aren't vital, and a normal match could be played without them working.
