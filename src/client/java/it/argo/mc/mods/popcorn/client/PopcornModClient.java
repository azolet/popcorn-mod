package it.argo.mc.mods.popcorn.client;

import it.argo.mc.mods.popcorn.PopcornParticles;
import it.argo.mc.mods.popcorn.client.particle.PopcornFlakeParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class PopcornModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance()
				.register(PopcornParticles.POPCORN_FLAKE, PopcornFlakeParticle.Provider::new);
	}
}
