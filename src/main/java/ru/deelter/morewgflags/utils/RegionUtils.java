package ru.deelter.morewgflags.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegionUtils {

	public static @Nullable ApplicableRegionSet getRegionsInLocation(@NotNull Location location) {
		RegionManager regionManager = WorldGuard.getInstance()
				.getPlatform()
				.getRegionContainer()
				.get(BukkitAdapter.adapt(location.getWorld()));
		if (regionManager == null) return null;


		return regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
	}

	public static ProtectedRegion getRegionWithHigherPriority(@NotNull ApplicableRegionSet regionSet) {
		ProtectedRegion highestRegion = null;
		for (ProtectedRegion region : regionSet) {
			if (highestRegion == null) {
				highestRegion = region;
				continue;
			}
			if (region.getPriority() > highestRegion.getPriority()) {
				highestRegion = region;
			}
		}
		return highestRegion;
	}
}
