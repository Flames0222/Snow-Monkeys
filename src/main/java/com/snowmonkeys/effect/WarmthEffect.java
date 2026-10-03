package com.snowmonkeys.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/** Warmth from a hot spring soak: stops freezing and slowly heals. */
public class WarmthEffect extends MobEffect {
    public WarmthEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getTicksFrozen() > 0) {
            entity.setTicksFrozen(0);
        }
        int healInterval = Math.max(20, 80 >> amplifier);
        if (entity.tickCount % healInterval == 0 && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
