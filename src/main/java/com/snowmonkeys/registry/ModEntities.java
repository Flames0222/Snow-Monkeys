package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.entity.SnowMonkey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SnowMonkeys.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SnowMonkey>> SNOW_MONKEY = ENTITY_TYPES.register("snow_monkey",
            () -> EntityType.Builder.of(SnowMonkey::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F)
                    .eyeHeight(0.75F)
                    .clientTrackingRange(10)
                    .build("snow_monkey"));

    private ModEntities() {
    }
}
