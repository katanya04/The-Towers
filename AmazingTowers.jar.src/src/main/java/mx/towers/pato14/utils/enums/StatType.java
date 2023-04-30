package mx.towers.pato14.utils.enums;

public enum StatType {
    KILLS(3, "Kills", "Kills"),
    DEATHS(4, "Muertes", "Deaths"),
    POINTS(5, "Puntos", "Anoted_Points"),
    GAMES_PLAYED(6, "Partidas jugadas", "Games_Played"),
    WINS(7, "Victorias", "Wins"),
    BLOCKS_BROKEN(8, "Bloques rotos",  "Blocks_Broken"),
    BLOCKS_PLACED(9, "Bloques colocados", "Blocks_Placed");
    private final int index;
    private final String text;
    private final String fieldName;
    StatType(int index, String text, String fieldName) {
        this.index = index;
        this.text = text;
        this.fieldName = fieldName;
    }
    public String getText() {
        return this.text;
    }
    public int getIndex() {
        return this.index;
    }
    public String getFieldName() {
        return this.fieldName;
    }
}