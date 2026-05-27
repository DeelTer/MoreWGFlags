package ru.deelter.morewgflags.listeners;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NonNull;
import org.vivecraft.api.VRAPI;
import ru.deelter.morewgflags.MoreWGFlags;
import ru.deelter.morewgflags.utils.RegionUtils;
import ru.deelter.morewgflags.utils.WGFlags;

public class VRFlagListener implements Listener {

    private static final Component VR_ONLY_MESSAGE = Component.text("VR ONLY", NamedTextColor.RED);
    private static final Component PC_ONLY_MESSAGE = Component.text("PC ONLY", NamedTextColor.RED);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(@NonNull PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        StateFlag.State state = getVrFlagState(player, event.getTo());
        if (state == null) return;

        boolean isVR = VRAPI.instance().isVRPlayer(player);
        if (isAllowed(state, isVR)) return;

        event.setCancelled(true);
        player.sendActionBar(isVR ? PC_ONLY_MESSAGE : VR_ONLY_MESSAGE);

        Vector push = event.getFrom().toVector()
                .subtract(event.getTo().toVector())
                .normalize()
                .multiply(1.2);
        push.setY(0.2);

        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreWGFlags.getInstance(), () -> player.setVelocity(push));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(@NonNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        StateFlag.State state = getVrFlagState(player, event.getTo());
        if (state == null) return;

        boolean isVR = VRAPI.instance().isVRPlayer(player);
        if (isAllowed(state, isVR)) return;

        event.setCancelled(true);
        player.sendActionBar(isVR ? PC_ONLY_MESSAGE : VR_ONLY_MESSAGE);
    }

    private StateFlag.State getVrFlagState(Player player, org.bukkit.Location location) {
        ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(location);
        if (regionSet == null) return null;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return regionSet.queryState(localPlayer, WGFlags.VR_ONLY);
    }

    private boolean isAllowed(StateFlag.State state, boolean isVR) {
        return (state == StateFlag.State.ALLOW && isVR) || (state == StateFlag.State.DENY && !isVR);
    }
}
