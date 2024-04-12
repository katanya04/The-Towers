package mx.towers.pato14.commands;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.MenuItem;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.items.ItemsEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Utils.sendMessage("This command can only be run by a player", MessageType.ERROR, sender);
            return true;
        }
        Player p = (Player) sender;
        GameInstance game = AmazingTowers.getGameInstance(p);
        if (game == null || game.getGame() == null || game.getGame().getGameState() == GameState.FINISH) {
            Utils.sendMessage("This command can only be executed in a match", MessageType.ERROR, p);
            return true;
        }
        if (args.length == 0)
            ((MenuItem<?>) ActionItem.getByName(ItemsEnum.KIT_SELECT.name)).getMenu().openMenu(p);
        else {
            Kit kit = Kits.getByName(args[0]);
            if (kit == null) {
                Utils.sendMessage("Kit not found", MessageType.ERROR, p);
                return true;
            }
            if (kit.playerHasKit(p, game)) {
                game.getGame().getPlayersSelectedKit().put(p, kit);
                Utils.sendMessage("Kit applied", MessageType.INFO, p);
            } else
                Utils.sendMessage("You don't have this kit", MessageType.ERROR, p);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return null;
        Player p = (Player) sender;
        GameInstance game = AmazingTowers.getGameInstance(p);
        if (game == null || game.getGame() == null || game.getGame().getGameState() == GameState.FINISH)
            return null;
        if (args.length == 1)
            return new ArrayList<>(game.getGame().getKits().getKitsNamesInInstance());
        return null;
    }
}