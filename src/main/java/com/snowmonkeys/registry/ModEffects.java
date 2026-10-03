package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.effect.WarmthEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, SnowMonkeys.MODID);

    public static final DeferredHolder<MobEffect, WarmthEffect> WARMTH = MOB_EFFECTS.register("warmth",
            () -> new WarmthEffect(MobEffectCategory.BENEFICIAL, 0xF08A4B));

    private ModEffects() {
    }
}
