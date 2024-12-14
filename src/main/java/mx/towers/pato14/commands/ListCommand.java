package mx.towers.pato14.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("list")) {
            // Map to store players by world
            Map<String, List<String>> playersByWorld = new HashMap<>();

            // Group players by world
            for (Player player : Bukkit.getOnlinePlayers()) {
                String world = player.getWorld().getName();
                playersByWorld.computeIfAbsent(world, k -> new ArrayList<>()).add(player.getName());
            }

            // Build the output message
            StringBuilder message = new StringBuilder(ChatColor.YELLOW + "Players by world:\n");
            for (Map.Entry<String, List<String>> entry : playersByWorld.entrySet()) {
                String world = entry.getKey();
                List<String> players = entry.getValue();

                // Add world information and player count
                message.append(ChatColor.AQUA).append(world).append(": ").append(ChatColor.WHITE).append(players.size()).append("\n");

                // Format the player list
                message.append(formatPlayerList(players)).append("\n");
            }

            // Send the message to the command executor
            sender.sendMessage(message.toString());
            return true;
        }
        return false;
    }

    // Method to format the player list
    private String formatPlayerList(List<String> players) {
        StringBuilder list = new StringBuilder();

        for (int i = 0; i < players.size(); i++) {
            String player = players.get(i);

            // Add the player's name in aqua
            list.append(ChatColor.AQUA).append(player);

            // If it's not the last player, add a comma and space in dark gray
            if (i < players.size() - 2) {
                list.append(ChatColor.DARK_GRAY).append(", ");
            }
            // If it's the second-to-last player, add "and" in dark gray before the last player
            else if (i == players.size() - 2) {
                list.append(ChatColor.DARK_GRAY).append(" and ");
            }
        }

        return list.toString();
    }
}
