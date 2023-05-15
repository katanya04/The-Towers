package mx.towers.pato14.game.events;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.events.player.*;
import mx.towers.pato14.game.events.protect.*;
import org.bukkit.plugin.PluginManager;

public class EventsManager {
    private final AmazingTowers plugin;

    public EventsManager(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new JoinListener(), this.plugin);
        pm.registerEvents(new QuitListener(), this.plugin);
        pm.registerEvents(new DeathListener(), this.plugin);
        pm.registerEvents(new DamageListener(), this.plugin);
        pm.registerEvents(new TeamChatListener(this.plugin), this.plugin);
        pm.registerEvents(new LobbyListener(this.plugin), this.plugin);
        pm.registerEvents(new CofresillosListener(this.plugin), this.plugin);
        pm.registerEvents(new PistonsillosListener(this.plugin), this.plugin);
        pm.registerEvents(new LeatherProtectListener(this.plugin), this.plugin);
        pm.registerEvents(new TimeListener(this.plugin), this.plugin);
        pm.registerEvents(new AntiTrollTeamListener(this.plugin), this.plugin);
        pm.registerEvents(new AntiFallingSandTrollListener(this.plugin), this.plugin);
        pm.registerEvents(new TntListener(this.plugin), this.plugin);
        pm.registerEvents(new ProtectedAreasListener(this.plugin), this.plugin);
        pm.registerEvents(new OrePlacedListener(this.plugin), this.plugin);
        pm.registerEvents(new WaterListener(this.plugin), this.plugin);
        pm.registerEvents(new BowListener(this.plugin), this.plugin);
        pm.registerEvents(new PotionsAndAppleListener(this.plugin), this.plugin);
        pm.registerEvents(new IronArmorListener(this.plugin), this.plugin);
        pm.registerEvents(new EnderpearlAndPotionThrowListener(this.plugin), this.plugin);
        pm.registerEvents(new BlockStats(this.plugin), this.plugin);
        pm.registerEvents(new EnchantItem(this.plugin), this.plugin);
    }
}


