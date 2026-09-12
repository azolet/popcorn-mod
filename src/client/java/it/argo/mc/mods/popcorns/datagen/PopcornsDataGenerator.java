package it.argo.mc.mods.popcorns.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Entrypoint for the "Data Generation" run configuration (./gradlew runDatagen).
 * Add providers to the pack to generate recipes, loot tables, models, tags and
 * translations into src/main/generated.
 */
public class PopcornsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		// pack.addProvider(MyRecipeProvider::new);
	}
}
