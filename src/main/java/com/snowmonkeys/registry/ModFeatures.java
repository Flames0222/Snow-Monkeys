package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.worldgen.HotSpringFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, SnowMonkeys.MODID);

    public static final DeferredHolder<Feature<?>, HotSpringFeature> HOT_SPRING =
            FEATURES.register("hot_spring", () -> new HotSpringFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {
    }
}
