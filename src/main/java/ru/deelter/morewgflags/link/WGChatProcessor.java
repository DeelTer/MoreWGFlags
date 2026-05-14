package ru.deelter.morewgflags.link;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import ru.deelter.chat.processors.AbstractChatProcessor;
import ru.deelter.chat.utils.ChatData;
import ru.deelter.morewgflags.utils.RegionUtils;
import ru.deelter.morewgflags.utils.WGFlags;

public class WGChatProcessor extends AbstractChatProcessor {

	public WGChatProcessor(int priority) {
		super(priority);
	}

	@Override
	public void process(@NotNull ChatData chatData) {
		ApplicableRegionSet regionSet = RegionUtils.getRegionsInLocation(chatData.getLocation());
		if (regionSet == null) return;

		var regionChatRadius = regionSet.queryValue(null, WGFlags.CHAT_RADIUS);
		var regionChatFormat = regionSet.queryValue(null, WGFlags.CHAT_FORMAT);
		var regionZoneName = regionSet.queryValue(null, WGFlags.CHAT_ZONE_NAME);

		if (regionChatRadius != null) {
			chatData.setRadius(regionChatRadius);
		}
		if (regionZoneName != null && !regionZoneName.isBlank()) {
			Component zoneName = MiniMessage.miniMessage().deserialize(regionZoneName);
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

	@Override
	public boolean canProcess(@NotNull ChatData chatData) {
		return WGFlags.CHAT_FLAGS_ENABLED;
	}
}
