package ru.deelter.morewgflags;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.morewgflags.listeners.BlockFlagsListener;
import ru.deelter.morewgflags.listeners.OtherFlagsListener;
import ru.deelter.morewgflags.listeners.VRFlagListener;
import ru.deelter.morewgflags.utils.WGFlags;

public final class MoreWGFlags extends JavaPlugin {

	@Getter
	private static MoreWGFlags instance;

	@Override
	public void onLoad() {
		instance = this;

		PluginManager pluginManager = Bukkit.getPluginManager();
		if (pluginManager.getPlugin("WorldGuard") == null) {
			getLogger().warning("Can't hook WorldGuard");
			return;
		}
		WGFlags.init();


	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new BlockFlagsListener(), this);
		manager.registerEvents(new OtherFlagsListener(), this);
		if (WGFlags.VR_FLAG_ENABLED) {
			manager.registerEvents(new VRFlagListener(), this);
		}
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
