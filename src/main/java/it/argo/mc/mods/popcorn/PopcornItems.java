package it.argo.mc.mods.popcorn;

import java.util.List;
import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

/**
 * Every item this mod adds, and the helper that registers them.
 */
public final class PopcornItems {
	/** A light snack: two hunger points, cookie-tier saturation. */
	public static final FoodProperties POPCORN_FOOD = new FoodProperties.Builder()
			.nutrition(2)
			.saturationModifier(0.3F)
			.build();

	/**
	 * A bucket costs eight popcorn, so it has to earn the trip to the crafting
	 * table: steak-tier nutrition, the best saturation in the mod, and edible
	 * even on a full hunger bar — you can always find room for popcorn.
	 */
	public static final FoodProperties POPCORN_BUCKET_FOOD = new FoodProperties.Builder()
			.nutrition(8)
			.saturationModifier(0.8F)
			.alwaysEdible()
			.build();

	/** Wheat seeds, cooked in a furnace or a smoker. */
	public static final Item POPCORN = register("popcorn", Item::new,
			new Item.Properties().food(POPCORN_FOOD));

	// Empty buckets, folded from paper and dyed. Fill one with popcorn to eat it.
	public static final Item RED_POPCORN_BUCKET = register("red_popcorn_bucket", Item::new, new Item.Properties());
	public static final Item GREEN_POPCORN_BUCKET = register("green_popcorn_bucket", Item::new, new Item.Properties());
	public static final Item BLACK_POPCORN_BUCKET = register("black_popcorn_bucket", Item::new, new Item.Properties());

	public static final List<Item> EMPTY_BUCKETS = List.of(
			RED_POPCORN_BUCKET, GREEN_POPCORN_BUCKET, BLACK_POPCORN_BUCKET);

	private PopcornItems() {
	}

	public static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PopcornMod.id(name));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/**
	 * Loads this class (registering every item in it) and hangs the items off
	 * their creative tabs. Called from {@link PopcornMod#onInitialize()}.
	 */
	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(entries -> entries.accept(POPCORN));

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
			for (Item bucket : EMPTY_BUCKETS) {
				entries.accept(bucket);
			}
		});
	}
}
