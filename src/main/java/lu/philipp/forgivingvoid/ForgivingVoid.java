package lu.philipp.forgivingvoid;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ForgivingVoid extends JavaPlugin implements Listener {
	double teleportHeight = 300;
	boolean onlyPlayer = true;

	@Override
	public void onEnable() {
		loadConfig();
		getServer().getPluginManager().registerEvents(this, (Plugin) this);
	}

	private void loadConfig() {
		File file = new File("plugins/ForgivingVoid", "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		if (!file.exists()) {
			Reader reader = null;
			try {
				reader = new InputStreamReader(getResource("config.yml"), "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (reader != null) {
				config = YamlConfiguration.loadConfiguration(reader);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.teleportHeight = config.getDouble("teleport-height");
		this.onlyPlayer = config.getBoolean("only-player");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (onlyPlayer && !(entity instanceof Player)) {
			return;
		}
		if (event.getCause() != DamageCause.VOID) {
			return;
		}
		if (event.getDamage() > 1000) {
			// kill command
			return;
		}

		event.setCancelled(true);

		Vector velocity = entity.getVelocity().clone();
		Location location = entity.getLocation();
		location.setY(teleportHeight);
		entity.teleport(location);
		entity.setVelocity(velocity);

	}
}