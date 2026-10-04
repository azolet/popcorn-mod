package it.argo.mc.mods.popcorn.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * One popped flake tumbling out of a furnace: it is thrown upwards, spins on
 * its way, falls back under gravity and fades out where it lands.
 */
public class PopcornFlakeParticle extends SingleQuadParticle {
	/** How many ticks the flake spends fading out at the end of its life. */
	private static final int FADE_TICKS = 8;

	/** Radians of spin per tick, positive or negative. */
	private final float rotationSpeed;

	protected PopcornFlakeParticle(ClientLevel level, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
		super(level, x, y, z, sprites.get(level.getRandom()));

		// The six-argument super constructor scatters the velocity it is given;
		// the mixin already picked one per flake, so set it straight.
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;

		this.gravity = 0.8F;
		this.friction = 0.96F;
		this.hasPhysics = true;
		this.lifetime = 24 + this.random.nextInt(16);
		this.quadSize = 0.09F + this.random.nextFloat() * 0.04F;
		this.roll = this.random.nextFloat() * Mth.TWO_PI;
		this.oRoll = this.roll;
		this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.5F;
		this.setSize(0.1F, 0.1F);
	}

	@Override
	public Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.oRoll = this.roll;

		super.tick();

		if (!this.isAlive()) {
			return;
		}

		// Once it has landed it settles instead of spinning on the floor.
		this.roll += this.onGround ? this.rotationSpeed * 0.05F : this.rotationSpeed;

		int remaining = this.lifetime - this.age;

		if (remaining < FADE_TICKS) {
			this.setAlpha(Math.max(0.0F, remaining / (float) FADE_TICKS));
		}
	}

	/**
	 * Hands the client a flake for every {@code argo_popcorn:popcorn_flake} the
	 * server sends.
	 */
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
				double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			return new PopcornFlakeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
		}
	}
}
