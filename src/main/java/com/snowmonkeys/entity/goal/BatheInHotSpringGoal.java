package com.snowmonkeys.entity.goal;

import com.snowmonkeys.entity.SnowMonkey;
import com.snowmonkeys.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;

/**
 * Walks to a shallow hot spring block (source water on a solid floor, open air above) and sits in it for a
 * while. The monkey heals and looks relaxed as it soaks.
 */
public class BatheInHotSpringGoal extends MoveToBlockGoal {
    private final SnowMonkey monkey;
    private int bathTicks;

    public BatheInHotSpringGoal(SnowMonkey monkey, double speedModifier, int searchRange) {
        super(monkey, speedModifier, searchRange, 6);
        this.monkey = monkey;
    }

    @Override
    public boolean canUse() {
        return this.monkey.wantsToBathe() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.monkey.getTarget() != null) {
            return false;
        }
        if (this.monkey.isBathing()) {
            return this.bathTicks > 0 && this.monkey.isInHotSpring();
        }
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.bathTicks = 600 + this.monkey.getRandom().nextInt(1200);
    }

    @Override
    public void stop() {
        super.stop();
        this.monkey.setBathing(false);
        this.monkey.resetBathCooldown();
    }

    @Override
    public void tick() {
        if (this.monkey.isBathing()) {
            this.bathTicks--;
            this.monkey.getNavigation().stop();
            return;
        }
        super.tick();
        if (this.isReachedTarget() && this.monkey.isInHotSpring()) {
            this.monkey.setBathing(true);
        }
    }

    @Override
    protected BlockPos getMoveToTarget() {
        return this.blockPos;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        if (!fluid.isSource() || fluid.getFluidType() != ModFluids.HOT_SPRING_TYPE.get()) {
            return false;
        }
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)
                && level.getBlockState(pos.above()).isAir();
    }
}
