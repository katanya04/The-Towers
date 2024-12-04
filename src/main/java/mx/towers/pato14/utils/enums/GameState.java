package mx.towers.pato14.utils.enums;

public enum GameState {
    LOBBY(false), PREGAME(false), CAPTAINS_CHOOSE(false), GAME(true), EXTRA_TIME(true), FINISH(false);
    public final boolean matchIsBeingPlayed;
    GameState(boolean matchIsBeingPlayed) {
        this.matchIsBeingPlayed = matchIsBeingPlayed;
    }
}


