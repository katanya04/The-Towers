package mx.towers.pato14.game.events;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.events.player.*;
import mx.towers.pato14.game.events.protect.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EventsManager {
    private AmazingTowers plugin;

    public EventsManager(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents((Listener) new JoinListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new QuitListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new DeathListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new DamageListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new TeamChatListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new LobbyListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new SpawnsTeams(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new CofresillosListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new BorderListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new PistonsillosListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new PoolsListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new LeatherProtectListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new TimeListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new AntiTrollTeamListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new AntiFallingSandTrollListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new TntListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new ProtectedAreasListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new OrePlacedListener(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new WaterListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new BowListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new PotionsAndAppleListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new IronArmorListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new EnderpearlAndPotionThrowListener(), (Plugin) this.plugin);
        pm.registerEvents((Listener) new BlockStats(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new EnchantItem(), (Plugin) this.plugin);
    }
}


