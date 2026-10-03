package com.snowmonkeys.block;

import com.snowmonkeys.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Geothermal vent. On random ticks it heats nearby still water above it into hot spring water and melts
 * snow and ice. With one of these, players can build their own onsen.
 */
public class HotSpringVentBlock extends Block {
    private static final int HORIZONTAL_RANGE = 3;
    private static final int VERTICAL_RANGE = 3;

    public HotSpringVentBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 4; i++) {
            BlockPos target = pos.offset(
                    random.nextInt(HORIZONTAL_RANGE * 2 + 1) - HORIZONTAL_RANGE,
                    1 + random.nextInt(VERTICAL_RANGE),
                    random.nextInt(HORIZONTAL_RANGE * 2 + 1) - HORIZONTAL_RANGE);
            BlockState targetState = level.getBlockState(target);
            if (targetState.is(Blocks.WATER) && targetState.getFluidState().isSource()) {
                level.setBlockAndUpdate(target, ModBlocks.HOT_SPRING_WATER.get().defaultBlockState());
            } else if (targetState.is(BlockTags.ICE) && !targetState.is(Blocks.BLUE_ICE)) {
                level.setBlockAndUpdate(target, Blocks.WATER.defaultBlockState());
            } else if (targetState.is(Blocks.SNOW) || targetState.is(Blocks.POWDER_SNOW)) {
                level.removeBlock(target, false);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
        BlockPos above = pos.above();
        if (level.getBlockState(above).isAir()) {
            if (random.nextInt(3) == 0) {
                level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, pos.getY() + 1.1, z, 0.0, 0.05, 0.0);
            }
            return;
        }
        if (level.getFluidState(above).isEmpty()) {
            return;
        }
        // Submerged: send a plume of steam up from the water surface above the vent.
        for (int dy = 2; dy <= 10; dy++) {
            BlockPos check = pos.above(dy);
            if (level.getFluidState(check).isEmpty()) {
                if (level.getBlockState(check).isAir() && random.nextInt(2) == 0) {
                    level.addParticle(ParticleTypes.CLOUD, x, check.getY() + 0.05, z, 0.0, 0.08, 0.0);
                }
                return;
            }
        }
    }
}
