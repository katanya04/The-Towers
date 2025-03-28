package mx.towers.pato14.game.events;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.events.player.*;
import mx.towers.pato14.game.events.protect.*;
import mx.towers.pato14.utils.enums.ConfigType;
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
        pm.registerEvents(new TeamChatListener(), this.plugin);
        pm.registerEvents(new LobbyListener(), this.plugin);
        pm.registerEvents(new ChestsProtect(), this.plugin);
        pm.registerEvents(new PistonsillosListener(), this.plugin);
        pm.registerEvents(new LeatherProtectListener(), this.plugin);
        pm.registerEvents(new WeatherListener(), this.plugin);
        // pm.registerEvents(new TeamTrollListener(), this.plugin);
        pm.registerEvents(new FallingSandTrollListener(), this.plugin);
        pm.registerEvents(new TntListener(), this.plugin);
        pm.registerEvents(new ProtectedAreasListener(), this.plugin);
        pm.registerEvents(new OrePlacedListener(), this.plugin);
        pm.registerEvents(new WaterListener(), this.plugin);
        pm.registerEvents(new BowListener(), this.plugin);
        pm.registerEvents(new PotionsAndAppleListener(), this.plugin);
        pm.registerEvents(new IronArmorListener(), this.plugin);
        pm.registerEvents(new EnderpearlAndPotionThrowListener(), this.plugin);
        pm.registerEvents(new EnchantItem(), this.plugin);
        pm.registerEvents(new ParkourPrizeFly(), this.plugin);
        pm.registerEvents(new MobSpawn(), this.plugin);
        if (AmazingTowers.getGlobalConfig().getBoolean("options.lobby.activated")){
            if (AmazingTowers.getLobby().getConfig(ConfigType.CONFIG).getBoolean("options.portalsToGame"))
            pm.registerEvents(new LobbyPortal(), this.plugin);
        }
    }
}


