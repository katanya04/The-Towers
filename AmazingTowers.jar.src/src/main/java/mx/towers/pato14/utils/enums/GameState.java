package mx.towers.pato14.utils.enums;

public enum GameState {
    LOBBY(false), PREGAME(false), GAME(true), GOLDEN_GOAL(true), FINISH(false);
    public final boolean matchIsBeingPlayed;
    GameState(boolean matchIsBeingPlayed) {
        this.matchIsBeingPlayed = matchIsBeingPlayed;
    }
}


