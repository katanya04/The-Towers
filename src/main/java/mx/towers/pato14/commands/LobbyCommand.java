package mx.towers.pato14.commands;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class LobbyCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (AmazingTowers.getLobby() == null) {
            Utils.sendMessage("There's no lobby defined", MessageType.ERROR, sender);
            return true;
        }
        if (!(sender instanceof Player)) {
            Utils.sendMessage("This command can only be run by a player", MessageType.ERROR, sender);
            return true;
        }
        Player p = (Player) sender;
        Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), p);
        Utils.sendMessage("Teleported to the lobby successfully", MessageType.ERROR, sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
