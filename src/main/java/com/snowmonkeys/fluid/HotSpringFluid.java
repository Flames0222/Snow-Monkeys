package com.snowmonkeys.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/** Hot spring water. Works like water, but steams wherever it is open to the air. */
public final class HotSpringFluid {
    private HotSpringFluid() {
    }

    static void steam(Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) != 0 || !level.getBlockState(pos.above()).isAir()) {
            return;
        }
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + 0.95;
        double z = pos.getZ() + random.nextDouble();
        level.addParticle(ParticleTypes.CLOUD, x, y, z, 0.0, 0.03 + random.nextDouble() * 0.03, 0.0);
    }

    public static class Source extends BaseFlowingFluid.Source {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            steam(level, pos, random);
        }
    }

    public static class Flowing extends BaseFlowingFluid.Flowing {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            steam(level, pos, random);
        }
    }
}
