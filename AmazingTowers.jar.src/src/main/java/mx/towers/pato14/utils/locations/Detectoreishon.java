package mx.towers.pato14.utils.locations;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.Locationshion;

public class Detectoreishon {
    private static final Map<Locationshion, String> locExist = new TreeMap<>();
    protected static final AmazingTowers plugin = AmazingTowers.getPlugin();

    private static Config getLocations() {
        return plugin.getLocations();
    }

    public static boolean getLocationString(String st) {
        if (st == null) {
            return false;
        }
        String[] i = st.split(";");
        if (i.length >= 6) {
            String.valueOf(i[0]);
            Double.valueOf(i[1]);
            Double.valueOf(i[2]);
            Double.valueOf(i[3]);
            Float.valueOf(i[4]);
            Float.valueOf(i[5]);
            return true;
        }
        String.valueOf(i[0]);
        Double.valueOf(i[1]);
        Double.valueOf(i[2]);
        Double.valueOf(i[3]);
        return true;
    }

    public static String getLocatioshionsTruee(boolean console) {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<Locationshion, String> map : locExist.entrySet()) {
            if (map.getKey().getLocationObligatory()) {
                if (!getLocationString(getLocations().getString(map.getKey().getLocationString())))
                    names.add("§c" + map.getKey().toString().replaceAll("_", "") + "§f");
                continue;
            }
            names.add("§e" + map.getKey().toString().replaceAll("_", "") + "§f");
        }
        if (console) {
            return names.isEmpty() ? "§a[AmazingTowers] §aLocations§f: §f[NONE]" : ("§a[AmazingTowers] §aLocations§f: §f" + names.toString().toUpperCase());
        }
        return names.isEmpty() ? "§aLocations§f: §f[NONE]" : ("§aLocations§f: §f" + names.toString().toLowerCase());
    }

    public static boolean getLocationsObligatory() {
        ArrayList<String> noExiste = new ArrayList<>();
        for (Map.Entry<Locationshion, String> map : locExist.entrySet()) {
            if (map.getKey().getLocationObligatory() && map.getValue().equals("No existe")) {
                noExiste.add(map.getKey().toString());
            }
        }
        return noExiste.isEmpty();
    }
//Comprueba si la location tiene coordenadas (si existe, vamos)
    public static void detectoreishonLocations() {
        Locationshion[] arrayOfLocationshion = Locationshion.values();
        //for (i = numero de locations; b = 0; b < i)
        for (int i = arrayOfLocationshion.length, b = 0; b < i; ) {
            //loc = "LOBBY" por ejemplo
            Locationshion loc = arrayOfLocationshion[b];
            //Si esta en la lista de localizaciones ya asignadas (?)
            if (getLocations().contains(loc.getLocationString())) {
                //Si tiene coordenadas asignadas
                if (getLocationString(getLocations().getString(loc.getLocationString()))) {
                    locExist.put(loc, "Existe");
                } else {
                    locExist.put(loc, "No existe");
                }
            } else {
                locExist.put(loc, "No existe");
            }
            b++;
        }
    }
}


