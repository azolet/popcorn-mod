package it.argo.mc.mods.popcorn.mixin;

import it.argo.mc.mods.popcorn.PopcornParticles;
import it.argo.mc.mods.popcorn.PopcornSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes a lit furnace or smoker pop every so often while it is cooking wheat
 * seeds, throwing a few flakes out of the top, so you can hear and see the
 * popcorn before you collect it.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	/** Input slot of a furnace — {@code AbstractFurnaceBlockEntity.SLOT_INPUT}, which is not accessible from here. */
	private static final int POPCORN$SLOT_INPUT = 0;

	/** Average one pop every this many ticks, per furnace. */
	private static final int POPCORN$AVERAGE_TICKS_BETWEEN_POPS = 16;

	/** How many flakes a single pop throws out, at most. */
	private static final int POPCORN$MAX_FLAKES_PER_POP = 3;

	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void popcorn$playPoppingSound(ServerLevel level, BlockPos pos, BlockState state,
			AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
		if (!state.is(Blocks.FURNACE) && !state.is(Blocks.SMOKER)) {
			return;
		}

		if (!state.hasProperty(AbstractFurnaceBlock.LIT) || !state.getValue(AbstractFurnaceBlock.LIT)) {
			return;
		}

		if (!blockEntity.getItem(POPCORN$SLOT_INPUT).is(Items.WHEAT_SEEDS)) {
			return;
		}

		RandomSource random = level.getRandom();

		if (random.nextInt(POPCORN$AVERAGE_TICKS_BETWEEN_POPS) != 0) {
			return;
		}

		level.playSound(null, pos, PopcornSounds.POPCORN_POP, SoundSource.BLOCKS,
				0.55F, 0.9F + random.nextFloat() * 0.3F);

		popcorn$throwFlakes(level, pos, random);
	}

	/**
	 * Throws one to {@value #POPCORN$MAX_FLAKES_PER_POP} flakes up out of the
	 * block's top face, each with its own velocity. A count of zero tells
	 * {@code sendParticles} to use the three offsets as the velocity rather
	 * than as a spread, which is what lets every flake fly its own way.
	 */
	private static void popcorn$throwFlakes(ServerLevel level, BlockPos pos, RandomSource random) {
		int flakes = 1 + random.nextInt(POPCORN$MAX_FLAKES_PER_POP);

		for (int i = 0; i < flakes; i++) {
			double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
			double y = pos.getY() + 1.02;
			double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;

			double xd = (random.nextDouble() - 0.5) * 0.14;
			double yd = 0.18 + random.nextDouble() * 0.18;
			double zd = (random.nextDouble() - 0.5) * 0.14;

			level.sendParticles(PopcornParticles.POPCORN_FLAKE, x, y, z, 0, xd, yd, zd, 1.0);
		}
	}
}
