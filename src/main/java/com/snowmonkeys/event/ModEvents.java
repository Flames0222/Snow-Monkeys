package com.snowmonkeys.event;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.entity.SnowMonkey;
import com.snowmonkeys.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = SnowMonkeys.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    private ModEvents() {
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNOW_MONKEY.get(), SnowMonkey.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.SNOW_MONKEY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SnowMonkey::checkSnowMonkeySpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
