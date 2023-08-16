package mx.towers.pato14.utils;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Iterator;

public class Utils {
    public static World createEmptyWorld(String name) {
        final WorldCreator wc = new WorldCreator(name);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generatorSettings("2;0;1;");
        final World world = Bukkit.createWorld(wc);
        world.setAutoSave(false);
        world.setSpawnLocation(0, 0, 0);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        return world;
    }

    public static void tpToWorld(World world, Player... players) {
        org.bukkit.Location destination;
        GameInstance worldGameInstance = AmazingTowers.getPlugin().getGameInstance(world);
        String lobby;
        if (worldGameInstance != null && (lobby = worldGameInstance.getConfig(ConfigType.LOCATIONS).getString(mx.towers.pato14.utils.enums.Location.LOBBY.getPath())) != null) {
            destination = Locations.getLocationFromString(lobby);
        } else
            destination = world.getSpawnLocation();
        for (Player player : players)
            player.teleport(destination);
    }

    public static void sendMessage(String msg, MessageType messageType, CommandSender sender) {
        sender.sendMessage((sender instanceof Entity ? messageType.getShortPrefix() : messageType.getPrefix())
                + AmazingTowers.getColor(msg));
    }

    public static ItemStack setName(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static int ceilToMultipleOfNine(int n) {
        if (n <= 0)
            return 9;
        else
            while (n % 9 != 0)
                n++;
        return n;
    }

    public static String firstCapitalized(String text) {
        return text.replaceFirst(String.valueOf(text.charAt(0)), String.valueOf(text.charAt(0)).toUpperCase());
    }

    public static String macroCaseToItemName(String macroCaseText) {
        StringBuilder itemName = new StringBuilder();
        for (String word : macroCaseText.toLowerCase().split("_")) {
            itemName.append(firstCapitalized(word)).append(" ");
        }
        return itemName.toString().trim();
    }

    public static String macroCaseToCamelCase(String macroCaseText) {
        StringBuilder camelCase = new StringBuilder();
        Iterator<String> itr = Arrays.stream(macroCaseText.toLowerCase().split("_")).iterator();
        if (itr.hasNext())
            camelCase.append(itr.next().toLowerCase());
        while (itr.hasNext()) {
            camelCase.append(firstCapitalized(itr.next().toLowerCase()));
        }
        return camelCase.toString().trim();
    }

    public static void checkForTeamWin(GameInstance gameInstance) {
        boolean makeATeamWin = true;
        Team temp = null;
        for (Team team : gameInstance.getGame().getTeams().getTeams()) {
            if (team.getSizeOnlinePlayers() > 0) {
                if (temp == null)
                    temp = team;
                else
                    makeATeamWin = false;
            }
        }
        if (makeATeamWin) {
            if (temp != null)
                gameInstance.getGame().getFinish().Fatality(temp.getTeamColor());
            else {
                int numberOfTeams = gameInstance.getGame().getTeams().getTeams().size();
                int numero = (int) Math.floor(Math.random() * numberOfTeams);
                gameInstance.getGame().getFinish().Fatality(TeamColor.values()[numero]);
            }
        }
    }

    public static int stringTimeToInt(String[] time) {
        int toret = 0;
        int temp;
        for (int i = time.length - 1; i >= 0; i--) {
            temp = Integer.parseInt(time[i]);
            for (int j = time.length - 1; j > i; j--)
                temp *= 60;
            toret += temp;
        }
        return toret;
    }

    public static String intTimeToString(int time) {
        StringBuilder toret = new StringBuilder();
        int temp;
        int j = -1;
        int i = 0;
        while (j-- != 0) {
            temp = time;
            while (temp >= 60) {
                temp /= 60;
                i++;
            }
            toret.append(temp < 10 && j >= 0 ? "0" + temp : temp).append(":");
            if (j < 0)
                j = i;
            while (i > 0) {
                temp *= 60;
                i--;
            }
            time -= temp;
        }
        return toret.deleteCharAt(toret.length() - 1).toString();
    }
}
