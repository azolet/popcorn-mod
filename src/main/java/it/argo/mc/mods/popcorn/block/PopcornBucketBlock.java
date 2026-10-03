package it.argo.mc.mods.popcorn.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * One to four filled popcorn buckets sitting on top of a block.
 *
 * <p>Placing another bucket of the same colour on the pile adds one; using the
 * pile with an empty hand takes one back, the way a cake loses a slice.
 */
public class PopcornBucketBlock extends Block {
	public static final MapCodec<PopcornBucketBlock> CODEC = simpleCodec(PopcornBucketBlock::new);

	public static final int MAX_BUCKETS = 4;
	public static final IntegerProperty BUCKETS = IntegerProperty.create("buckets", 1, MAX_BUCKETS);

	/**
	 * Which way a single bucket faces, in sixteenths of a turn, as player heads
	 * do. The models cover it with four baked angles times four blockstate
	 * rotations; piles of two or more ignore it and keep their arrangement.
	 */
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

	/**
	 * Where each bucket stands, indexed by (count - 1), as the x/z of its lower
	 * corner in sixteenths of a block. These must match the block models.
	 */
	public static final double[][][] LAYOUTS = {
			{{4.5, 4.5}},
			{{0, 4}, {9, 4}},
			{{0, 0}, {9, 0}, {4, 9}},
			{{0, 0}, {9, 0}, {0, 9}, {9, 9}},
	};

	/**
	 * Each bucket is one head-sized cube. Seven wide rather than eight so that
	 * four of them fit side by side with a gap: two coplanar faces would z-fight.
	 */
	private static final int BUCKET_WIDTH = 7;
	private static final int BUCKET_HEIGHT = 8;

	private static final VoxelShape[] SHAPES = buildShapes();

	public PopcornBucketBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BUCKETS, 1).setValue(ROTATION, 0));
	}

	private static VoxelShape[] buildShapes() {
		VoxelShape[] shapes = new VoxelShape[LAYOUTS.length];

		for (int i = 0; i < LAYOUTS.length; i++) {
			VoxelShape shape = Shapes.empty();

			for (double[] spot : LAYOUTS[i]) {
				double x = spot[0];
				double z = spot[1];

				shape = Shapes.or(shape, Block.box(x, 0, z,
						x + BUCKET_WIDTH, BUCKET_HEIGHT, z + BUCKET_WIDTH));
			}

			shapes[i] = shape;
		}

		return shapes;
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BUCKETS, ROTATION);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(BUCKETS) - 1];
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportCenter(level, pos.below(), Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos,
			Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(state, level, tickAccess, pos, direction, neighbourPos, neighbourState, random);
	}

	/** Lets another bucket of the same colour be placed onto this pile. */
	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		if (!context.isSecondaryUseActive()
				&& context.getItemInHand().is(asItem())
				&& state.getValue(BUCKETS) < MAX_BUCKETS) {
			return true;
		}

		return super.canBeReplaced(state, context);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState existing = context.getLevel().getBlockState(context.getClickedPos());

		if (existing.is(this)) {
			// adding to a pile keeps the rotation the first bucket was placed with
			return existing.setValue(BUCKETS, Math.min(MAX_BUCKETS, existing.getValue(BUCKETS) + 1));
		}

		// face the player, the way a head placed on the floor does
		int facing = Mth.floor((double) (context.getRotation() * 16.0F / 360.0F) + 0.5D) & 15;

		return super.getStateForPlacement(context).setValue(ROTATION, facing);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		ItemStack taken = new ItemStack(this);

		if (!player.addItem(taken)) {
			player.drop(taken, false);
		}

		int remaining = state.getValue(BUCKETS) - 1;

		if (remaining > 0) {
			level.setBlock(pos, state.setValue(BUCKETS, remaining), Block.UPDATE_ALL);
		} else {
			level.removeBlock(pos, false);
		}

		level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.7F, 1.3F);

		return InteractionResult.SUCCESS;
	}
}
