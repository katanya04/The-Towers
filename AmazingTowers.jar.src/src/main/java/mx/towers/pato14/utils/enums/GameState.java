package mx.towers.pato14.utils.enums;

public enum GameState {
    LOBBY, PREGAME, GAME, FINISH;
    private static GameState state;

    public static GameState getState() {
        return state;
    }

    public static boolean isState(GameState st) {
        return (state == st);
    }

    public static void setState(GameState st) {
        state = st;
    }
}


