package it.argo.mc.mods.popcorn.mixin;

import it.argo.mc.mods.popcorn.PopcornBlocks;
import it.argo.mc.mods.popcorn.PopcornParticles;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * While you eat, vanilla throws crumbs of the item you are holding. For a
 * bucket that means flecks of painted carton; these are popcorn flakes
 * instead, the same ones a furnace throws when it pops a seed.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	/** Vanilla asks for five crumbs a mouthful; whole flakes want fewer. */
	private static final int FLAKES_PER_MOUTHFUL = 2;

	@Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
	private void popcorn$flakesNotCarton(ItemStack stack, int count, CallbackInfo ci) {
		if (!PopcornBlocks.isFilledBucket(stack)) {
			return;
		}

		LivingEntity self = (LivingEntity) (Object) this;

		for (int i = 0; i < FLAKES_PER_MOUTHFUL; i++) {
			// Same geometry as the vanilla crumbs: a little push away from the
			// mouth, turned to wherever the entity happens to be looking.
			Vec3 velocity = new Vec3((self.getRandom().nextFloat() - 0.5D) * 0.1D,
					self.getRandom().nextFloat() * 0.1D + 0.1D, 0.0D)
					.xRot(-self.getXRot() * Mth.DEG_TO_RAD)
					.yRot(-self.getYRot() * Mth.DEG_TO_RAD);

			Vec3 position = new Vec3((self.getRandom().nextFloat() - 0.5D) * 0.3D,
					-self.getRandom().nextFloat() * 0.6D - 0.3D, 0.6D)
					.xRot(-self.getXRot() * Mth.DEG_TO_RAD)
					.yRot(-self.getYRot() * Mth.DEG_TO_RAD)
					.add(self.getX(), self.getEyeY(), self.getZ());

			self.level().addParticle(PopcornParticles.POPCORN_FLAKE,
					position.x, position.y, position.z,
					velocity.x, velocity.y + 0.05D, velocity.z);
		}

		ci.cancel();
	}
}
