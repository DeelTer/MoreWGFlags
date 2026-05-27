package ru.deelter.morewgflags.listeners;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NonNull;
import org.vivecraft.api.VRAPI;
import ru.deelter.morewgflags.utils.RegionUtils;
import ru.deelter.morewgflags.utils.WGFlags;

public class VRFlagListener implements Listener {

    private static final Component VR_ONLY_MESSAGE = Component.text("VR ONLY", NamedTextColor.RED);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(@NonNull PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(event.getTo());
        if (regionSet == null) return;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (regionSet.queryState(localPlayer, WGFlags.VR_ONLY) != StateFlag.State.ALLOW) return;

        if (VRAPI.instance().isVRPlayer(player)) return;

        event.setCancelled(true);
        player.sendActionBar(VR_ONLY_MESSAGE);

        Vector push = event.getFrom().toVector()
                .subtract(event.getTo().toVector())
                .normalize()
                .multiply(0.4);
        push.setY(0.1);
        player.setVelocity(push);
    }
}
