package mx.towers.pato14.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import mx.towers.pato14.update.AutoUpdate;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class AmazingTowersCommand implements CommandExecutor, TabCompleter {
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
                sender.sendMessage("§8[§bAmazingTowers§8] §7v" + currentVersion);
                return true;
            }
            // Si el comando es "/AmazingTowers update"
            else if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
                // Mostrar la versión actual del plugin
                String currentVersion = plugin.getDescription().getVersion();
                sender.sendMessage("§8[§bAmazingTowers§8] §7v" + currentVersion);

                // Iniciar la verificación de actualizaciones
                AutoUpdate updateChecker = new AutoUpdate(plugin); // Si necesita el plugin como parámetro
                updateChecker.checkForUpdates();

                return true;
            }
            // Si el comando es "/AmazingTowers reload"
            else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage("§8[§bAmazingTowers§8] §7Reloading...");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                plugin.getServer().getPluginManager().enablePlugin(plugin);
                sender.sendMessage("§8[§bAmazingTowers§8] §aReloaded.");
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Lista de opciones posibles
            List<String> options = Arrays.asList("reload", "update");

            // Filtrar las opciones que comienzan con el texto ingresado por el usuario
            String input = args[0].toLowerCase();
            List<String> filteredOptions = new ArrayList<>();
            for (String option : options) {
                if (option.toLowerCase().startsWith(input)) {
                    filteredOptions.add(option);
                }
            }
            return filteredOptions;
        }
        return new ArrayList<>();
    }
}
