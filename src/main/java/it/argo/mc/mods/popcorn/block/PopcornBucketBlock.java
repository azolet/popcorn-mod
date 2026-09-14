package it.argo.mc.mods.popcorn.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
	 * Where each bucket stands, indexed by (count - 1), as the x/z of its lower
	 * corner in sixteenths of a block. These must match the block models.
	 */
	public static final int[][][] LAYOUTS = {
			{{5, 5}},
			{{2, 6}, {8, 4}},
			{{2, 7}, {8, 8}, {5, 1}},
			{{1, 2}, {8, 1}, {2, 9}, {9, 8}},
	};

	private static final int BUCKET_WIDTH = 6;
	private static final int BUCKET_HEIGHT = 9;
	private static final VoxelShape[] SHAPES = buildShapes();

	public PopcornBucketBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BUCKETS, 1));
	}

	private static VoxelShape[] buildShapes() {
		VoxelShape[] shapes = new VoxelShape[LAYOUTS.length];

		for (int i = 0; i < LAYOUTS.length; i++) {
			VoxelShape shape = Shapes.empty();

			for (int[] spot : LAYOUTS[i]) {
				shape = Shapes.or(shape, Block.box(spot[0], 0, spot[1],
						spot[0] + BUCKET_WIDTH, BUCKET_HEIGHT, spot[1] + BUCKET_WIDTH));
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
		builder.add(BUCKETS);
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
			return existing.setValue(BUCKETS, Math.min(MAX_BUCKETS, existing.getValue(BUCKETS) + 1));
		}

		return super.getStateForPlacement(context);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hit) {
		if (level.isClientSide) {
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
