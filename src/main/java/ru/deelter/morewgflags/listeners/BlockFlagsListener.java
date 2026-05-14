package ru.deelter.moreWGFlags;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.wgflags.data.DFlags;
import ru.deelter.wgflags.utils.RegionUtils;
import ru.xikki.libraries.paper.annotations.Listener;

@Listener
public class BlockFlagsListener {

	@EventHandler
	public void editTable(@NotNull SignChangeEvent event) {
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.SIGN_CHANGE)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBucketEmpty(@NotNull PlayerBucketEmptyEvent event) {
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.BUCKET_EMPTY)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBucketFill(@NotNull PlayerBucketFillEvent event) {
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.BUCKET_FILL)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFertilize(@NotNull BlockFertilizeEvent event) {
		if (event.getPlayer() == null) return;
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.BLOCK_FERTILIZE)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(@NotNull BlockBreakEvent event) {
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.SIMPLE_BLOCK_BREAK)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(@NotNull BlockPlaceEvent event) {
		if (hasAccess(event.getPlayer(), event.getBlock().getLocation(), DFlags.SIMPLE_BLOCK_PLACE)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTakeLectern(@NotNull PlayerTakeLecternBookEvent event) {
		if (hasAccess(event.getPlayer(), event.getLectern().getLocation(), DFlags.LECTERN_TAKE)) return;

		event.setCancelled(true);
	}

	private static boolean hasAccess(@Nullable Player player, Location location, StateFlag flag) {
		if (player == null) return true;
		if (player.hasPermission("dflags.bypass")) return true;

		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(location);
		if (regionSet == null) return true;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		StateFlag.State state = regionSet.queryState(localPlayer, flag);
		if (state == null) return true;

		return state == StateFlag.State.ALLOW;
	}
}
