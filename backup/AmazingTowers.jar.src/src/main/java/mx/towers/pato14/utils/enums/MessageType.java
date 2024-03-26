package mx.towers.pato14.utils.enums;

public enum MessageType {
    NO_PREFIX("", ""),
    INFO("§f[§aAmazingTowers/Info§f]§r ", "§a(AM) "),
    ERROR("§f[§cAmazingTowers/Error§f]§4 ", "§c(AM)§4 "),
    WARNING("§f[§eAmazingTowers/Warning§f]§6 ", "§e(AM)§6 ");
    private final String prefix;
    private final String shortPrefix;
    MessageType (String prefix, String shortPrefix) {
        this.prefix = prefix;
        this.shortPrefix = shortPrefix;
    }
    public String getPrefix() {
        return prefix;
    }

    public String getShortPrefix() {
        return shortPrefix;
    }
}