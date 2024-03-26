package mx.towers.pato14.utils.rewards;

public enum RewardsEnum {
    KILL("kill"), WIN("win"), LOSER_TEAM("match"), POINT("point");
    private final String string;
    RewardsEnum(String string) {
        this.string = string;
    }
    public String getName() {
        return this.string;
    }
}


