package com.snowmonkeys.block;

import com.snowmonkeys.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class HotSpringWaterBlock extends LiquidBlock {
    public HotSpringWaterBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LivingEntity living) {
            living.setTicksFrozen(0);
            if (!level.isClientSide && living.tickCount % 20 == 0) {
                living.addEffect(new MobEffectInstance(ModEffects.WARMTH, 600, 0, true, true));
            }
        }
    }
}
