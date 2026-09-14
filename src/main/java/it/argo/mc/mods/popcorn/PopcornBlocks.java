package it.argo.mc.mods.popcorn;

import java.util.List;

import it.argo.mc.mods.popcorn.block.PopcornBucketBlock;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

/**
 * The filled popcorn buckets. Each one is both a block you can stand on a
 * table and the edible item you get back when you take one off the pile.
 */
public final class PopcornBlocks {
	public static final Block FILLED_RED_POPCORN_BUCKET = register("filled_red_popcorn_bucket");
	public static final Block FILLED_GREEN_POPCORN_BUCKET = register("filled_green_popcorn_bucket");
	public static final Block FILLED_BLACK_POPCORN_BUCKET = register("filled_black_popcorn_bucket");

	public static final List<Block> FILLED_BUCKETS = List.of(
			FILLED_RED_POPCORN_BUCKET, FILLED_GREEN_POPCORN_BUCKET, FILLED_BLACK_POPCORN_BUCKET);

	private PopcornBlocks() {
	}

	private static Block register(String name) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, PopcornMod.id(name));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, PopcornMod.id(name));

		BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
				.sound(SoundType.WOOL)
				.strength(0.2F)
				.noOcclusion()
				.pushReaction(PushReaction.DESTROY)
				.setId(blockKey);

		Block block = new PopcornBucketBlock(properties);

		// The block item is the snack: right-click the ground to set it down,
		// right-click the air to eat it.
		Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties()
				.setId(itemKey)
				.useBlockDescriptionPrefix()
				.stacksTo(16)
				.food(PopcornItems.POPCORN_BUCKET_FOOD)));

		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
			for (Block bucket : FILLED_BUCKETS) {
				entries.accept(bucket);
			}
		});
	}
}
