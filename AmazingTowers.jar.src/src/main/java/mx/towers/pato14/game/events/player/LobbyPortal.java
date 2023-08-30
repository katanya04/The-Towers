package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LobbyPortal implements Listener {
    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (!(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL || e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL))
            return;
        Utils.tpToWorld(AmazingTowers.getGameInstanceWithMorePlayers().getWorld(), e.getPlayer());
    }
}
