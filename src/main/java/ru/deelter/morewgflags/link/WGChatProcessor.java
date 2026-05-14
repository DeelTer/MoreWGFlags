package ru.deelter.moreWGFlags;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class WGChatProcessor extends AbstractChatProcessor {

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public void process(@NotNull ChatData chatData) {
		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(chatData.getLocation());
		if (regionSet == null) return;

		var regionChatRadius = regionSet.queryValue(null, DFlags.CHAT_RADIUS);
		var regionChatFormat = regionSet.queryValue(null, DFlags.CHAT_FORMAT);
		var regionZoneName = regionSet.queryValue(null, DFlags.CHAT_ZONE_NAME);

		if (regionChatRadius != null) {
			chatData.setRadius(regionChatRadius);
		}
		if (regionZoneName != null && !regionZoneName.isBlank()) {
			Component zoneName = MiniMessage.miniMessage().deserialize(regionZoneName).hoverEvent(createDebugHover(chatData));
			Component prefix = chatData.getPrefix();
			if (prefix != null) {
				prefix = Component.join(JoinConfiguration.separator(Component.text(" ")), zoneName, prefix);
			}
			chatData.setPrefix(prefix);
		}
		if (regionChatFormat != null && !regionChatFormat.isBlank()) {
			chatData.setFormat(regionChatFormat);
		}
	}

	private @NotNull HoverEvent<Component> createDebugHover(@NotNull ChatData data) {
		Location location = data.getLocation();
		return HoverEvent.showText(
				Component.text(String.format("XYZ: %s, %s, %s", location.getBlockX(), location.getBlockY(), location.getBlockZ()))
						.append(Component.newline())
						.append(Component.translatable("com.settings.chat.radius").append(Component.text(": " + data.getRadius())))
						.style(Style.style()
								.color(TextColor.color(168, 155, 138))
								.build())
		);
	}

	@Override
	public boolean canProcess(@NotNull ChatData chatData) {
		return DFlags.CHAT_FLAGS_ENABLED;
	}
}
