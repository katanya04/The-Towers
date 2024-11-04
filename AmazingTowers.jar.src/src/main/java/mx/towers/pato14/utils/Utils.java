package mx.towers.pato14.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.locations.Locations;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
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
        Team team = gameInstance.getGame().getTeams().getTeamByPlayer(playerName);
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

    public static String firstCapitalized(String text) {
        return text == null ? null :
                text.replaceFirst(String.valueOf(text.charAt(0)), String.valueOf(text.charAt(0)).toUpperCase());
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

    public static String itemCaseToMacroCase(String itemName) {
        return itemName.toUpperCase().replace(" ", "_");
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
        Stack<Integer> digits = new Stack<>();
        do {
            digits.push(time % 60);
            time /= 60;
        } while (time > 0);
        StringBuilder sb = new StringBuilder();
        sb.append(digits.pop());
        while (!digits.empty())
            sb.append(":").append(String.format("%02d", digits.pop()));
        return sb.toString();
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

    public static String getColor(String st) {
        return ChatColor.translateAlternateColorCodes('&', st);
    }

    public static List<String> getColor(List<String> st) {
        return st.stream().map(Utils::getColor).collect(Collectors.toList());
    }

    public static void sendConsoleMessage(String msg, MessageType messageType) {
        AmazingTowers.getPlugin().getServer().getConsoleSender().sendMessage(messageType.getPrefix() + msg);
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

    public static List<?> getConfSafeList(Config config, String path) {
        Object obj = config.get(path);
        if (obj == null)
            return new ArrayList<>();
        return obj instanceof List ? (List<?>) obj : Collections.singletonList(obj);
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
        } else if (obj instanceof ItemStack)
            return new ItemStack[]{(ItemStack) obj};
        else
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

    public static <E> E getRandomSetElement(Set<E> set) {
        return set.stream().skip(new Random().nextInt(set.size())).findFirst().orElse(null);
    }

    public static boolean isValidURL(String url) {
        try {
            URL url1 = new URL(url);
            url1.toURI();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void reportException(String text, Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackLine : ex.getStackTrace())
            sb.append(stackLine).append("\n");
        Utils.sendConsoleMessage(text + "\n Exception: " + ex.getClass().getCanonicalName() + "\n" + sb, MessageType.ERROR);
    }

    public static boolean isPotionEffect(String effect) {
        return Arrays.stream(PotionEffectType.class.getFields()).filter(o -> o.getDeclaringClass().equals(PotionEffectType.class))
                .map(Field::getName).collect(Collectors.toSet())
                .contains(Utils.itemCaseToMacroCase(effect));
    }

    public static PotionEffectType getPotionEffect(String effect) {
        Field potion = Arrays.stream(PotionEffectType.class.getFields()).filter(o -> o.getDeclaringClass().equals(PotionEffectType.class))
                .filter(o -> o.getName().equals(Utils.itemCaseToMacroCase(effect)))
                .findAny().orElse(null);
        try {
            return potion == null ? null : (PotionEffectType) potion.get("");
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}