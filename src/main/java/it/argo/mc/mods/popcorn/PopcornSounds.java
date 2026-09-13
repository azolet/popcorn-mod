package it.argo.mc.mods.popcorn;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * Every sound this mod adds.
 */
public final class PopcornSounds {
	/**
	 * The pop of a kernel turning inside out. Played by
	 * {@code AbstractFurnaceBlockEntityMixin} while seeds are cooking.
	 */
	public static final SoundEvent POPCORN_POP = register("popcorn_pop");

	private PopcornSounds() {
	}

	private static SoundEvent register(String name) {
		Identifier id = PopcornMod.id(name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	/**
	 * Loads this class, registering its sounds. Must run during mod
	 * initialization, while the registries are still open.
	 */
	public static void initialize() {
	}
}
