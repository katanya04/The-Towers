package mx.towers.pato14.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import mx.towers.pato14.update.AutoUpdate;
import org.bukkit.command.CommandExecutor;

public class AmazingTowersCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public AmazingTowersCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Método que se llama cuando se ejecuta el comando
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el usuario tiene permisos
        if (!sender.hasPermission("towers.admin") && !sender.isOp()) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        // Si el comando es "/AmazingTowers"
        if (command.getName().equalsIgnoreCase("AmazingTowers")) {
            // Si no hay argumentos, muestra la versión actual
            if (args.length == 0) {
                String currentVersion = plugin.getDescription().getVersion();
                sender.sendMessage("§aLa versión actual de AmazingTowers es: §b" + currentVersion);
                return true;
            }
            // Si el comando es "/AmazingTowers update"
            else if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
                // Mostrar la versión actual del plugin
                String currentVersion = plugin.getDescription().getVersion();
                sender.sendMessage("§aLa versión actual de AmazingTowers es: §b" + currentVersion);

                // Iniciar la verificación de actualizaciones
                AutoUpdate updateChecker = new AutoUpdate(plugin); // Si necesita el plugin como parámetro
                updateChecker.checkForUpdates();

                return true;
            }
            // Si el comando es "/AmazingTowers reload"
            else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage("§aRecargando el plugin...");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                plugin.getServer().getPluginManager().enablePlugin(plugin);
                sender.sendMessage("§aEl plugin se ha recargado correctamente.");
                return true;
            }
        }
        return false;
    }
}
