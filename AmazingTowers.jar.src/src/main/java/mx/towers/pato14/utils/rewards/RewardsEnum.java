package mx.towers.pato14.utils.rewards;

public enum RewardsEnum {
    KILL("kill"), WIN("win"), LOSER_TEAM("forplay"), POINT("point");
    private String string;

    RewardsEnum(String string) {
        this.string = string;
    }

    public String getName() {
        return this.string;
    }
}


