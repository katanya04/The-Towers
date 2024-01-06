package mx.towers.pato14.utils.locations;

public class IntCoordinate {
    private int x, y, z;
    public IntCoordinate() {
        this(0,0,0);
    }
    public IntCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static IntCoordinate getFromString(String location) {
        String[] split = location.split(";");
        return new IntCoordinate(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }
}