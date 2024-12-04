package mx.towers.pato14.commands;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReadyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Utils.sendMessage("This command can only be run by a player", MessageType.ERROR, sender);
            return true;
        }
        Player p = (Player) sender;
        GameInstance game = AmazingTowers.getGameInstance(p);
        if (game == null || game.getGame() == null || game.getGame().getGameState() != GameState.PREGAME) {
            Utils.sendMessage("This command can only be executed during the match countdown", MessageType.ERROR, p);
            return true;
        }
        if (!game.getGame().getCaptainsPhase().isCaptain(p.getName())) {
            Utils.sendMessage("You are not a captain", MessageType.ERROR, p);
            return true;
        }
        TeamColor team = game.getGame().getTeams().getTeamColorByPlayer(p.getName());
        if (game.getGame().getCaptainsPhase().isReady(team)) {
            Utils.sendMessage("You already marked your team as ready", MessageType.ERROR, p);
            return true;
        }
        game.getGame().getCaptainsPhase().setReady(team);
        game.broadcastMessage(game.getConfig(ConfigType.MESSAGES).getString("teamReady")
                .replace("{Color}", team.getColor())
                .replace("{Team}", team.getName(game)),
                true);
        return true;
    }
}
