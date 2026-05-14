package ru.deelter.morewgflags.utils;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import ru.deelter.chat.bukkit.BetterChat;
import ru.deelter.chat.config.ChatConfig;
import ru.deelter.morewgflags.link.WGChatProcessor;

import java.util.List;

public class WGFlags {

	public static final boolean CHAT_FLAGS_ENABLED;
	public static final StringFlag CHAT_ZONE_NAME;
	public static final StringFlag CHAT_FORMAT;
	public static final DoubleFlag CHAT_RADIUS;
	public static final StateFlag KEEP_INVENTORY;
	public static final DoubleFlag PVP_DAMAGE;
	public static final IntegerFlag MIN_FOOD_LEVEL;
	public static final StateFlag SIGN_CHANGE;
	public static final StateFlag BUCKET_FILL;
	public static final StateFlag BUCKET_EMPTY;
	public static final StateFlag BLOCK_FERTILIZE;
	public static final StateFlag LECTERN_TAKE;
	public static final StateFlag SIMPLE_BLOCK_BREAK;
	public static final StateFlag SIMPLE_BLOCK_PLACE;

	static {
		CHAT_ZONE_NAME = new StringFlag("chat-zone-name");
		CHAT_RADIUS = new DoubleFlag("chat-radius", RegionGroup.ALL);
		CHAT_FORMAT = new StringFlag("chat-format", ChatConfig.formatDefault);
		KEEP_INVENTORY = new StateFlag("keep-inventory", false);
		PVP_DAMAGE = new DoubleFlag("pvp-damage", RegionGroup.ALL);
		SIGN_CHANGE = new StateFlag("sign-change", false);
		BUCKET_FILL = new StateFlag("bucket-fill", false);
		BUCKET_EMPTY = new StateFlag("bucket-empty", false);
		BLOCK_FERTILIZE = new StateFlag("block-fertilize", false);
		LECTERN_TAKE = new StateFlag("lectern-take", false);
		SIMPLE_BLOCK_BREAK = new StateFlag("block-break-simple", false);
		SIMPLE_BLOCK_PLACE = new StateFlag("block-place-simple", false);
		MIN_FOOD_LEVEL = new IntegerFlag("min-food-level", RegionGroup.ALL);


		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		registry.registerAll(List.of(
				KEEP_INVENTORY,
				PVP_DAMAGE,
				SIGN_CHANGE,
				BUCKET_FILL,
				BUCKET_EMPTY,
				BLOCK_FERTILIZE,
				LECTERN_TAKE,
				SIMPLE_BLOCK_PLACE,
				SIMPLE_BLOCK_BREAK,
				MIN_FOOD_LEVEL
		));

		PluginManager pluginManager = Bukkit.getPluginManager();
		CHAT_FLAGS_ENABLED = pluginManager.getPlugin("BetterChat") != null;

		if (CHAT_FLAGS_ENABLED) {
			registry.registerAll(List.of(
					CHAT_ZONE_NAME,
					CHAT_RADIUS,
					CHAT_FORMAT
			));
			BetterChat.getInstance().getManager().register(new WGChatProcessor(10));
		}
	}

	public static void init() {
	}


}
