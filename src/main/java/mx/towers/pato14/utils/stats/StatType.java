package mx.towers.pato14.utils.stats;

public enum StatType {
    KILLS(3, "Kills", "kills", "§3"),
    DEATHS(4, "Muertes", "deaths", "§4"),
    POINTS(5, "Puntos", "points", "§6"),
    GAMES(6, "Partidas jugadas", "games", "§a"),
    WINS(7, "Victorias", "wins", "§b"),
    BLOCKS_BROKEN(8, "Bloques rotos",  "Blocks_Broken", "§d"),
    BLOCKS_PLACED(9, "Bloques colocados", "Blocks_Placed", "§2");
    private final int index;
    private final String text;
    private final String columnName;
    private final String color;
    StatType(int index, String text, String columnName, String color) {
        this.index = index;
        this.text = text;
        this.columnName = columnName;
        this.color = color;
    }
    public String getText() {
        return this.text;
    }
    public int getIndex() {
        return this.index;
    }
    public String getColumnName() {
        return this.columnName;
    }
    public String getColor() {
        return color;
    }
    public static StatType fromColumnName(String columnName) {
        for (StatType statType : StatType.values())
            if (statType.columnName.equalsIgnoreCase(columnName))
                return statType;
        return null;
    }
}