package mx.towers.pato14.update;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

public class AutoUpdate {

    private final JavaPlugin plugin;

    public AutoUpdate(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        String currentVersion = plugin.getDescription().getVersion();
        String apiUrl = "https://api.github.com/repos/katanya04/The-Towers/releases/latest";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Abrir conexión a la API de GitHub
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                // Leer la respuesta de la API
                StringBuilder responseBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
                String response = responseBuilder.toString();

                // Obtener la última versión y la URL de descarga del JSON
                String latestVersion = parseVersionFromJson(response);
                if (latestVersion == null) {
                    plugin.getLogger().severe("No se pudo obtener la última versión del JSON.");
                    notifyAdmins("§cError al verificar actualizaciones. Consulta la consola para más información.");
                    return;
                }

                if (compareVersions(currentVersion, latestVersion) >= 0) {
                    // Si la versión actual es igual o mayor que la última
                    plugin.getLogger().info("AmazingTowers está actualizado.");
                    notifyAdmins("§aAmazingTowers está actualizado a la versión más reciente. §8[§b" + latestVersion + "§8]");
                } else {
                    plugin.getLogger().info("Nueva versión disponible: " + latestVersion);
                    notifyAdmins("§eNueva versión de AmazingTowers disponible: " + latestVersion);

                    String downloadUrl = parseDownloadUrlFromJson(response);
                    if (downloadUrl == null) {
                        plugin.getLogger().severe("No se pudo obtener la URL de descarga del JSON.");
                        notifyAdmins("§cError al verificar actualizaciones. Consulta la consola para más información.");
                        return;
                    }

                    // Descargar la nueva versión
                    notifyAdmins("§eDescargando nueva versión de AmazingTowers...");
                    downloadNewVersion(downloadUrl);
                    plugin.getLogger().info("Nueva versión descargada. Notificando a los administradores...");
                    notifyAdmins("§aNueva versión descargada. Reinicia el servidor para aplicar los cambios.");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error verificando actualizaciones: " + e.getMessage());
                notifyAdmins("§cOcurrió un error al verificar actualizaciones. Consulta la consola para más información.");
            }
        });
    }

    private void notifyAdmins(String message) {
        // Notificar a todos los jugadores con el permiso `towers.admin`
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("towers.admin"))
                .forEach(player -> player.sendMessage(message));
    }

    private String parseVersionFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("tag_name"); // Obtiene la versión del campo `tag_name`
        } catch (Exception e) {
            plugin.getLogger().severe("Error al analizar la versión del JSON: " + e.getMessage());
            return null;
        }
    }

    private String parseDownloadUrlFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray assets = jsonObject.getJSONArray("assets");
            if (assets.length() > 0) {
                JSONObject firstAsset = assets.getJSONObject(0);
                return firstAsset.getString("browser_download_url"); // URL del primer asset
            }
            return null;
        } catch (Exception e) {
            plugin.getLogger().severe("Error al analizar la URL de descarga del JSON: " + e.getMessage());
            return null;
        }
    }

    private void downloadNewVersion(String downloadUrl) {
        try {
            // Configurar la ruta para guardar el nuevo archivo .jar
            File pluginFile = new File("plugins/" + plugin.getName() + ".jar");

            // Abrir una conexión para descargar el archivo
            try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(pluginFile)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }

            plugin.getLogger().info("El nuevo archivo del plugin ha sido descargado con éxito.");
        } catch (IOException e) {
            plugin.getLogger().severe("Error al descargar la nueva versión: " + e.getMessage());
        }
    }

    /**
     * Compara dos versiones en formato "x.y.z".
     * Devuelve:
     * - Un valor negativo si `version1` < `version2`
     * - Cero si `version1` == `version2`
     * - Un valor positivo si `version1` > `version2`
     */
    private int compareVersions(String version1, String version2) {
        // Elimina el prefijo "v" u otros caracteres no numéricos
        version1 = version1.replaceAll("[^0-9.]", "");
        version2 = version2.replaceAll("[^0-9.]", "");

        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (v1 != v2) {
                return v1 - v2;
            }
        }
        return 0; // Las versiones son iguales
    }
}
