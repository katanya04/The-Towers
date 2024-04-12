package mx.towers.pato14.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.mysql.IConnexion;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.map.MinecraftFont;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

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
        World oldWorld = player.getWorld();
        if (worldInstance instanceof GameInstance && !((GameInstance) worldInstance).canJoin(player))
            player.sendMessage("You can't enter this match at the moment");
        else {
            if (worldInstance != null)
                tpToLobby(worldInstance, player);
            else
                player.teleport(world.getSpawnLocation());
            onChangeWorlds(player, oldWorld, world);
            Utils.updatePlayerTab(player, world, true);
        }
    }

    public static void tpToLobby(@NotNull TowersWorldInstance worldInstance, Player player) {
        String lobby = worldInstance.getConfig(ConfigType.LOCATIONS).getString(mx.towers.pato14.utils.enums.Location.LOBBY.getPath());
        if (lobby != null)
            player.teleport(Locations.getLocationFromString(lobby));
        else
            player.teleport(worldInstance.getWorld().getSpawnLocation());
    }

    private static void onChangeWorlds(Player player, World oldWorld, World newWorld) {
        TowersWorldInstance oldInstance = AmazingTowers.getInstance(oldWorld);
        if (oldInstance != null)
            oldInstance.leaveInstance(player);
        TowersWorldInstance newInstance = AmazingTowers.getInstance(newWorld);
        if (newInstance == null)
            return;
        newInstance.joinInstance(player);
        if (newInstance instanceof GameInstance && ((GameInstance) newInstance).getGame() != null)
            newInstance.broadcastMessage(getJoinMessage((GameInstance) newInstance, player.getName()), true);
    }

    private static String getJoinMessage(GameInstance gameInstance, String playerName) {
        ITeam team = gameInstance.getGame().getTeams().getTeamByPlayer(playerName);
        if (team != null) {
            return gameInstance.getConfig(ConfigType.MESSAGES).getString("joinTeam")
                    .replace("{Player}", playerName)
                    .replace("{Color}", team.getTeamColor().getColor())
                    .replace("{Team}", team.getTeamColor().getName(gameInstance));
        } else {
            return gameInstance.getConfig(ConfigType.MESSAGES).getString("joinMessage")
                    .replace("{Player}", playerName).replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()));
        }
    }

    public static void sendMessage(String msg, MessageType messageType, CommandSender sender) {
        sender.sendMessage((sender instanceof Entity ? messageType.getShortPrefix() : messageType.getPrefix())
                + getColor(msg));
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
        return setLore(item, Collections.singletonList(lore));
    }

    public static ItemStack addLore(ItemStack item, List<String> lore) {
        List<String> currentLore = item.getItemMeta().getLore();
        if (currentLore == null)
            currentLore = new ArrayList<>();
        currentLore.addAll(lore);
        return setLore(item, lore);
    }

    public static ItemStack addLore(ItemStack item, String lore) {
        return addLore(item, Collections.singletonList(lore));
    }

    public static ItemStack removeLore(ItemStack item, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> currentLore = itemMeta.getLore();
        if (lore == null)
            return item;
        currentLore.remove(lore);
        itemMeta.setLore(currentLore);
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
        ITeam temp = null;
        for (ITeam team : gameInstance.getGame().getTeams().getTeams()) {
            if (team.getNumAlivePlayers() > 0) {
                if (temp == null)
                    temp = team;
                else
                    makeATeamWin = false;
            }
        }
        if (makeATeamWin) {
            if (temp != null)
                gameInstance.getGame().getFinish().fatality(temp.getTeamColor());
            else {
                int numberOfTeams = gameInstance.getGame().getTeams().getTeams().size();
                int teamNumber = (int) Math.floor(Math.random() * numberOfTeams);
                gameInstance.getGame().getFinish().fatality(TeamColor.values()[teamNumber]);
            }
        }
    }

    public static boolean isStringTime(String[] time) {
        for (String t : time)
            if (!Utils.isInteger(t))
                return false;
        return true;
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

    public static boolean isBoolean(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
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

    public static void updatePlayerTab(Player player, World currentWorld, boolean runTaskLater) {
        if (runTaskLater) {
            (new BukkitRunnable() {
                @Override
                public void run() {
                    updatePlayerTab(player, currentWorld, false);
                }
            }).runTaskLater(AmazingTowers.getPlugin(), 1L);
        } else {
            for (Player player1 : AmazingTowers.getAllOnlinePlayers()) {
                if (Objects.equals(player, player1))
                    continue;
                if (currentWorld.equals(player1.getWorld())) {
                    player1.showPlayer(player);
                    player.showPlayer(player1);
                } else {
                    player1.hidePlayer(player);
                    player.hidePlayer(player1);
                }
            }
        }
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
        AmazingTowers.getLobby().getHotbar().apply(player);
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

    public static boolean isAValidTable(String tableName) {
        return tableName != null && (AmazingTowers.connexion.getTables().contains(tableName) || IConnexion.ALL_TABLES.equals(tableName));
    }

    public static String getColor(String st) {
        return ChatColor.translateAlternateColorCodes('&', st);
    }

    public static List<String> getColor(List<String> st) {
        return st.stream().map(Utils::getColor).collect(Collectors.toList());
    }

    public static void sendConsoleMessage(String msg, MessageType messageType) {
        AmazingTowers.getPlugin().getServer().getConsoleSender().sendMessage(messageType.getPrefix() + msg);
    }

    public static boolean replaceWithBackup(String backupPath, String targetPath) throws IOException {
        File source = new File(backupPath);
        if (!source.exists())
            return false;
        File target = new File(targetPath);
        if (target.exists()) {                                 //Borra el mundo que estaba de la anterior partida
            Bukkit.unloadWorld(target.getName(), false);
            deleteRecursive(target);
        }                                                    //Lo sobreescribe con el de backup
        FileUtils.copyDirectory(source, target);
        Bukkit.createWorld(new WorldCreator(target.getName()));
        return true;
    }

    public static void deleteRecursive(File file) {
        if (file.exists())
            return;
        for (File value : file.listFiles()) {
            if (value.isDirectory())
                deleteRecursive(value);
            else
                value.delete();
        }
    }

    public static <T> T getValueOrDefault(T value, Supplier<? extends T> supplier) {
        return value == null ? supplier.get() : value;
    }

    public static <T> T getValueOrDefault(T value, T def) {
        return value == null ? def : value;
    }

    public static class Pair<T, U> {
        private T key;
        private U value;
        public Pair(T key, U value) {
            this.key = key;
            this.value = value;
        }
        public T getKey() {
            return key;
        }
        public U getValue() {
            return value;
        }
        public void setKey(T key) {
            this.key = key;
        }
        public void setValue(U value) {
            this.value = value;
        }
    }

    public static ItemStack setUnbreakable(ItemStack item) {
        if (item == null || item.getType().getMaxDurability() == 0)
            return item;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack[] setUnbreakable(ItemStack[] items) {
        Arrays.stream(items).forEach(Utils::setUnbreakable);
        return items;
    }

    public static <T> T getOrDefault(T value, T def) {
        return value != null ? value : def;
    }

    public static int parseIntOrDefault(String value, int def) {
        int toret;
        try {
            toret = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            toret = def;
        }
        return toret;
    }
    public static boolean parseBoolOrDefault(String value, boolean def) {
        return "true".equalsIgnoreCase(value) || !"false".equalsIgnoreCase(value) && def;
    }

    public static boolean getConfBoolDefaultsIfNull(Config config, String path) {
        String obj = config.getString(path);
        if (!("true".equals(obj) || "false".equals(obj)))
            obj = Config.getFromDefault(path, config.getFileName()).toString();
        return Boolean.parseBoolean(obj);
    }

    public static int getConfIntDefaultsIfNull(Config config, String path) {
        String obj = config.getString(path);
        if (!isInteger(obj))
            obj = Config.getFromDefault(path, config.getFileName()).toString();
        return Integer.parseInt(obj);
    }

    public static int getRandomInt(int min, int max) {
        double rand = Math.random();
        double longitude = Math.abs(max - min);
        return (int) Math.round(rand * longitude + min);
    }

    public static ItemStack[] getItemsFromConf(ConfigurationSection conf, String path) {
        return getItemsFromConf(conf, path, 0);
    }

    public static ItemStack[] getItemsFromConf(ConfigurationSection conf, String path, int expectedArraySize) {
        return getItemsFromObj(conf.get(path), expectedArraySize);
    }

    public static ItemStack[] getItemsFromObj(Object obj, int expectedArraySize) {
        if (obj instanceof ItemStack[]) {
            ItemStack[] toret = (ItemStack[]) obj;
            if (expectedArraySize > 0 && toret.length != expectedArraySize)
                throw new RuntimeException("Unexpected ItemStack array size (Expected " + expectedArraySize + ", got " + toret.length + ")");
            return toret;
        } else if (obj instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) obj;
            ItemStack[] toret;
            try {
                toret = collection.stream().map(o -> (ItemStack) o).toArray(ItemStack[]::new);
            } catch (Exception ex) {
                throw new RuntimeException("Error while parsing an ItemStack array");
            }
            if (expectedArraySize > 0 && toret.length != expectedArraySize)
                throw new RuntimeException("Unexpected ItemStack array size at (Expected " + expectedArraySize + ", got " + toret.length + ")");
            return toret;
        } else
            throw new RuntimeException("Error while parsing an ItemStack array");
    }

    public static void compressGzip(Path source, Path target) throws IOException {
        try (GZIPOutputStream gos = new GZIPOutputStream(Files.newOutputStream(target.toFile().toPath()));
             FileInputStream fis = new FileInputStream(source.toFile())) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }
        }
    }

    public static String fileTimeToDate(FileTime fileTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(fileTime.toMillis());
    }

    public static void removeItemFromHand(Player p) {
        int amount;
        if ((amount = p.getItemInHand().getAmount()) == 1)
            p.setItemInHand(new ItemStack(Material.AIR));
        else
            p.getItemInHand().setAmount(amount - 1);
    }
}