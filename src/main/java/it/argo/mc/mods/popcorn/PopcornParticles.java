package it.argo.mc.mods.popcorn;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Every particle this mod adds.
 */
public final class PopcornParticles {
	/**
	 * A single popped flake, thrown out of a furnace or smoker that is cooking
	 * wheat seeds. Spawned by {@code AbstractFurnaceBlockEntityMixin} alongside
	 * the pop, and drawn by {@code PopcornFlakeParticle} on the client.
	 */
	public static final SimpleParticleType POPCORN_FLAKE = register("popcorn_flake");

	private PopcornParticles() {
	}

	private static SimpleParticleType register(String name) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, PopcornMod.id(name), FabricParticleTypes.simple());
	}

	/**
	 * Loads this class, registering its particle types. Must run during mod
	 * initialization, while the registries are still open.
	 */
	public static void initialize() {
	}
}
