package ru.deelter.moreWGFlags;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import ru.deelter.wgflags.data.DFlags;
import ru.deelter.wgflags.utils.RegionUtils;
import ru.xikki.libraries.paper.annotations.Listener;

import java.util.Arrays;

@Listener
public class OtherFlagsListener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onKeepInventory(@NotNull PlayerDeathEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;

		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(player.getLocation());
		if (regionSet == null) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		StateFlag.State state = regionSet.queryState(localPlayer, DFlags.KEEP_INVENTORY);
		if (state == null) return;
		if (state == StateFlag.State.DENY) {
			event.setKeepInventory(false);
			event.setKeepLevel(false);

			if (event.getDroppedExp() <= 0) {
				event.setDroppedExp(player.getTotalExperience());
			}
			if (event.getDrops().isEmpty()) {
				event.getDrops().addAll(Arrays.stream(player.getInventory().getContents()).toList());
			}
			return;
		}

		event.setKeepInventory(true);
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		event.getDrops().clear();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(@NotNull EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player target)) return;
		Player damager = null;
		if (event.getDamager() instanceof Player player) {
			damager = player;
		}
		else if (event.getDamager() instanceof AbstractArrow arrow) {
			ProjectileSource projectileSource = arrow.getShooter();
			damager = projectileSource instanceof Player sourcePlayer ? sourcePlayer : null;
		}
		if (damager == null) return;

		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(target.getLocation());
		if (regionSet == null) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(target);
		var damage = regionSet.queryValue(localPlayer, DFlags.PVP_DAMAGE);
		if (damage == null) return;

		event.setDamage(damage);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodChange(@NotNull FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getGameMode().isInvulnerable()) return;
		if (player.getFoodLevel() < event.getFoodLevel()) return;

		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(player.getLocation());
		if (regionSet == null) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		Integer minFoodLevel = regionSet.queryValue(localPlayer, DFlags.MIN_FOOD_LEVEL);
		if (minFoodLevel == null) return;
		if (event.getFoodLevel() >= minFoodLevel) return;

		event.setCancelled(true);
	}
}
