package it.argo.mc.mods.popcorn;

import java.util.List;

import it.argo.mc.mods.popcorn.block.PopcornBucketBlock;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

/**
 * The filled popcorn buckets. Each one is both a block you can stand on a
 * table and the edible item you get back when you take one off the pile.
 */
public final class PopcornBlocks {
	public static final Block FILLED_RED_POPCORN_BUCKET =
			register("filled_red_popcorn_bucket", PopcornItems.POPCORN_BUCKET_FOOD);
	// 0.1.0 ships the red bucket only. The green and black ones are ready —
	// their models, textures and recipes are parked under disabled/ in the repo.
	// public static final Block FILLED_GREEN_POPCORN_BUCKET =
	// 		register("filled_green_popcorn_bucket", PopcornItems.POPCORN_BUCKET_FOOD);
	// public static final Block FILLED_BLACK_POPCORN_BUCKET =
	// 		register("filled_black_popcorn_bucket", PopcornItems.POPCORN_BUCKET_FOOD);

	public static final Block FILLED_RED_CARAMEL_POPCORN_BUCKET =
			register("filled_red_caramel_popcorn_bucket", PopcornItems.CARAMEL_POPCORN_BUCKET_FOOD);
	// public static final Block FILLED_GREEN_CARAMEL_POPCORN_BUCKET =
	// 		register("filled_green_caramel_popcorn_bucket", PopcornItems.CARAMEL_POPCORN_BUCKET_FOOD);
	// public static final Block FILLED_BLACK_CARAMEL_POPCORN_BUCKET =
	// 		register("filled_black_caramel_popcorn_bucket", PopcornItems.CARAMEL_POPCORN_BUCKET_FOOD);

	public static final List<Block> FILLED_BUCKETS = List.of(
			FILLED_RED_POPCORN_BUCKET,
			FILLED_RED_CARAMEL_POPCORN_BUCKET);
			// FILLED_GREEN_POPCORN_BUCKET, FILLED_BLACK_POPCORN_BUCKET,
			// FILLED_GREEN_CARAMEL_POPCORN_BUCKET, FILLED_BLACK_CARAMEL_POPCORN_BUCKET

	private PopcornBlocks() {
	}

	/**
	 * Whether this is one of the six filled buckets — the edible block items.
	 * Both the holding pose and the crumb particles key off this.
	 */
	public static boolean isFilledBucket(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem
				&& FILLED_BUCKETS.contains(blockItem.getBlock());
	}

	private static Block register(String name, FoodProperties food) {
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
				.food(food)));

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
