package mx.towers.pato14.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.map.MinecraftFont;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /*public static World createEmptyWorld(String name) {
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
    }*/

    public static void tpToWorld(World world, Player player) {
        TowersWorldInstance worldInstance = AmazingTowers.getInstance(world);
        String lobby;
        if (worldInstance instanceof GameInstance && !((GameInstance) worldInstance).canJoin(player))
            player.sendMessage("No puedes entrar a esta partida ahora mismo");
        else if (worldInstance != null && (lobby = worldInstance.getConfig(ConfigType.LOCATIONS).getString(mx.towers.pato14.utils.enums.Location.LOBBY.getPath())) != null)
            player.teleport(Locations.getLocationFromString(lobby));
        else
            player.teleport(world.getSpawnLocation());
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

    public static ItemStack setLore(ItemStack item, List<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack setLore(ItemStack item, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Collections.singletonList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static int ceilToMultipleOfNine(int n) {
        return n <= 9 ? 9 : ((n - 1) / 9 + 1) * 9;
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

    public static String camelCaseToMacroCase(String camelCaseText) {
        StringBuilder toret = new StringBuilder(camelCaseText);
        Pattern pat = Pattern.compile("[A-Z][^A-Z]*$");
        Matcher match = pat.matcher(camelCaseText);

        int lastCapitalIndex;
        do {
            if (match.find()) {
                lastCapitalIndex = match.start();
                if (lastCapitalIndex != -1) {
                    camelCaseText = toret.insert(lastCapitalIndex, "_").toString();
                    match = pat.matcher(camelCaseText);
                }
            }
        } while (match.find());
        return camelCaseText.toUpperCase();
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
                int teamNumber = (int) Math.floor(Math.random() * numberOfTeams);
                gameInstance.getGame().getFinish().Fatality(TeamColor.values()[teamNumber]);
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

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPath(String path) {
        String[] pathSplit = path.split(";");
        if (pathSplit.length < 2)
            return false;
        try {
            ConfigType.valueOf(camelCaseToMacroCase(pathSplit[0]));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static ItemStack addGlint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack removeGlint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static boolean hasGlint(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta.hasEnchant(Enchantment.ARROW_DAMAGE) && itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    public static boolean isLeatherArmor(Material material) {
        return material.equals(Material.LEATHER_HELMET) || material.equals(Material.LEATHER_CHESTPLATE)
                || material.equals(Material.LEATHER_LEGGINGS) || material.equals(Material.LEATHER_BOOTS);
    }

    public static boolean checkWorldFolder(String worldName) {
        File potentialWorld = new File(Bukkit.getServer().getWorldContainer(), worldName);
        return potentialWorld.exists();
    }

    public static double safeDivide(double n1, double n2) {
        return n2 == 0 ? n1 : n1 / n2;
    }

    public static void resetPlayer(Player player) {
        player.setHealth(20.0D);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setFoodLevel(20);
        player.setSaturation(5.f);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL)
            player.setAllowFlight(false);
        removePotion(player);
    }

    private static void removePotion(Player player) {
        if (player.getActivePotionEffects().isEmpty())
            return;
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    public static ItemStack colorArmor(ItemStack item, Color color) {
        if (Utils.isLeatherArmor(item.getType()) && color != null) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void updatePlayerTab(Player player) {
        TowersWorldInstance playerInstance = AmazingTowers.getInstance(player);
        for (Player player1 : AmazingTowers.getAllOnlinePlayers()) {
            if (player.getWorld().equals(player1.getWorld())) {
                player1.showPlayer(player);
                player.showPlayer(player1);
                if (!(playerInstance instanceof GameInstance))
                    continue;
                NametagEdit.getApi().reloadNametag(player);
            } else {
                player1.hidePlayer(player);
                player.hidePlayer(player1);
            }
        }
    }

    public static void clearNameTagPlayer(Player player) {
        NametagEdit.getApi().clearNametag(player);
    }

    public static List<List<TextComponent>> getLines(List<TextComponent> text) { //Thx to Swedz :)
        //Note that the only flaw with using MinecraftFont is that it can't account for some UTF-8 symbols, it will throw an IllegalArgumentException
        final MinecraftFont font = new MinecraftFont();
        final int maxLineWidth = font.getWidth("LLLLLLLLLLLLLLLLLLL");

        //Get all of our lines
        List<List<TextComponent>> lines = new ArrayList<>();
        try {
            List<TextComponent> line = new ArrayList<>();
            for (TextComponent textComponent : text) {
                String rawLine = ChatColor.stripColor(line.stream().map(TextComponent::getText).reduce("", String::concat));
                rawLine += ChatColor.stripColor(textComponent.getText());
                if (font.getWidth(rawLine) > maxLineWidth) {
                    lines.add(line);
                    line = new ArrayList<>();
                }
                line.add(textComponent);
                if (textComponent.getText().endsWith("\n")) {
                    lines.add(line);
                    line = new ArrayList<>();
                }
            }
        } catch (IllegalArgumentException ex) {
            lines.clear();
        }
        return lines;
    }

    public static void joinMainLobby(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        Utils.resetPlayer(player);
        AmazingTowers.getLobby().getHotbarItems().giveHotbarItems(player);
    }

    public static void joinGame(Player player) {
        Utils.resetPlayer(player);
        GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        Team team;
        if ((team = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName())) != null && team.respawnPlayers())
            joinTeam(player, team, gameInstance);
        else { //if ... (check for case that player should be on spectator mode)
            player.setGameMode(GameMode.ADVENTURE);
            gameInstance.getHotbarItems().giveHotbarItems(player);
            NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(TeamColor.SPECTATOR.getColor()));
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(mx.towers.pato14.utils.enums.Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    public static void joinTeam(Player player, Team team, GameInstance gameInstance) {
        NametagEdit.getApi().clearNametag(player);
        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
        player.setGameMode(GameMode.SURVIVAL);
        team.setNameTagPlayer(player);
        gameInstance.getGame().applyKitToPlayer(player);
        gameInstance.getGame().getStats().setHashStats(player.getName());

    }

    public static void bungeecordTeleport(Player player) {
        if (!AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled"))
            return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(AmazingTowers.getGlobalConfig().getString("options.bungeecord.server_name"));
        player.sendPluginMessage(AmazingTowers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static String listToCommaSeparatedString(List<?> list) {
        StringBuilder names = new StringBuilder();
        Iterator<?> itr = list.iterator();
        while (itr.hasNext()) {
            names.append(itr.next().toString());
            if (itr.hasNext())
                names.append(", ");
        }
        return names.toString();
    }
}