package it.argo.mc.mods.popcorn.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import it.argo.mc.mods.popcorn.PopcornBlocks;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla's eating pose shoves whatever you are holding into your face and
 * shakes it, which for a popcorn bucket reads as biting the carton. A bucket
 * is held instead: raised to just under the chin, tipped so the opening — and
 * the popcorn in it — faces you, with a small lift on every mouthful.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	/** Where the bucket is held, in camera space, once it is fully raised. */
	private static final float HOLD_X = 0.32F;
	private static final float HOLD_Y = -0.42F;
	private static final float HOLD_Z = -0.88F;

	/** How the bucket is turned there: yaw inwards, tipped open, slight roll. */
	private static final float HOLD_YAW = 12.0F;
	private static final float HOLD_PITCH = 28.0F;
	private static final float HOLD_ROLL = -12.0F;

	/** How far a mouthful lifts, pulls in and tips the bucket. */
	private static final float MUNCH_RISE = 0.035F;
	private static final float MUNCH_PULL = 0.025F;
	private static final float MUNCH_PITCH = 7.0F;

	/**
	 * The resting position {@code applyItemArmTransform} puts a held item in.
	 * It runs straight after this method, so the pose built here undoes it
	 * first and then places the bucket in camera space directly.
	 */
	private static final float ARM_X = 0.56F;
	private static final float ARM_Y = -0.52F;
	private static final float ARM_Z = -0.72F;

	@Inject(method = "applyEatTransform", at = @At("HEAD"), cancellable = true)
	private void popcorn$holdTheBucket(PoseStack poseStack, float partialTick, HumanoidArm arm,
			ItemStack stack, Player player, CallbackInfo ci) {
		if (!PopcornBlocks.isFilledBucket(stack)) {
			return;
		}

		int sign = arm == HumanoidArm.RIGHT ? 1 : -1;

		float remaining = player.getUseItemRemainingTicks() - partialTick + 1.0F;
		float left = remaining / stack.getUseDuration(player);

		// Vanilla's ramp: flat for most of the use, a quick swing up at the start.
		float raised = 1.0F - (float) Math.pow(Math.min(left, 1.0F), 27.0);

		// One beat per mouthful, on the same four-tick cadence as the crunch.
		float munch = left < 0.92F ? Mth.abs(Mth.cos(remaining / 4.0F * Mth.PI)) : 0.0F;

		poseStack.translate(
				sign * Mth.lerp(raised, ARM_X, HOLD_X),
				Mth.lerp(raised, ARM_Y, HOLD_Y + MUNCH_RISE * munch),
				Mth.lerp(raised, ARM_Z, HOLD_Z + MUNCH_PULL * munch));

		poseStack.mulPose(Axis.YP.rotationDegrees(sign * raised * HOLD_YAW));
		poseStack.mulPose(Axis.XP.rotationDegrees(raised * (HOLD_PITCH + MUNCH_PITCH * munch)));
		poseStack.mulPose(Axis.ZP.rotationDegrees(sign * raised * HOLD_ROLL));

		poseStack.translate(-sign * ARM_X, -ARM_Y, -ARM_Z);

		ci.cancel();
	}
}
