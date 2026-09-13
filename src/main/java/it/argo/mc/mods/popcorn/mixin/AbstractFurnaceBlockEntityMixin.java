package it.argo.mc.mods.popcorn.mixin;

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
 * seeds, so you can hear the popcorn before you see it.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	/** Input slot of a furnace — {@code AbstractFurnaceBlockEntity.SLOT_INPUT}, which is not accessible from here. */
	private static final int POPCORN$SLOT_INPUT = 0;

	/** Average one pop every this many ticks, per furnace. */
	private static final int POPCORN$AVERAGE_TICKS_BETWEEN_POPS = 40;

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
	}
}
