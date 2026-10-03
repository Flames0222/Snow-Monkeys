package com.snowmonkeys;

import com.mojang.logging.LogUtils;
import com.snowmonkeys.registry.ModBlocks;
import com.snowmonkeys.registry.ModCreativeTabs;
import com.snowmonkeys.registry.ModEffects;
import com.snowmonkeys.registry.ModEntities;
import com.snowmonkeys.registry.ModFeatures;
import com.snowmonkeys.registry.ModFluids;
import com.snowmonkeys.registry.ModItems;
import com.snowmonkeys.registry.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SnowMonkeys.MODID)
public class SnowMonkeys {
    public static final String MODID = "snowmonkeys";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SnowMonkeys(IEventBus modEventBus, ModContainer modContainer) {
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
