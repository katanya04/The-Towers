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

public class ListAllCommand implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (command.getName().equalsIgnoreCase("listall")) {
                // Mapa para almacenar jugadores por mundo
                Map<String, List<String>> jugadoresPorMundo = new HashMap<>();

                // Agrupar jugadores por mundo
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String mundo = player.getWorld().getName();
                    jugadoresPorMundo.computeIfAbsent(mundo, k -> new ArrayList<>()).add(player.getName());
                }

                // Construir el mensaje de salida
                StringBuilder mensaje = new StringBuilder(ChatColor.YELLOW + "Jugadores por mundo:\n");
                for (Map.Entry<String, List<String>> entry : jugadoresPorMundo.entrySet()) {
                    String mundo = entry.getKey();
                    List<String> jugadores = entry.getValue();

                    // Agregar información del mundo y cantidad de jugadores
                    mensaje.append(ChatColor.GRAY).append(mundo).append(": ").append(ChatColor.WHITE).append(jugadores.size()).append("\n");

                    // Formatear la lista de jugadores
                    mensaje.append(formatearListaJugadores(jugadores)).append("\n");
                }

                // Enviar el mensaje al ejecutor del comando
                sender.sendMessage(mensaje.toString());
                return true;
            }
            return false;
        }

        // Método para formatear la lista de jugadores
        private String formatearListaJugadores(List<String> jugadores) {
            StringBuilder lista = new StringBuilder();
            
            for (int i = 0; i < jugadores.size(); i++) {
                String jugador = jugadores.get(i);
                
                // Añadir el nombre del jugador con color gris
                lista.append(ChatColor.GRAY).append(jugador);
                
                // Si no es el último jugador, añadir coma y espacio en gris oscuro
                if (i < jugadores.size() - 2) {
                    lista.append(ChatColor.DARK_GRAY).append(", ");
                }
                // Si es el penúltimo jugador, añadir "y" en gris oscuro antes del último jugador
                else if (i == jugadores.size() - 2) {
                    lista.append(ChatColor.DARK_GRAY).append(" y ");
                }
            }
            
            return lista.toString();
        }
    
}
