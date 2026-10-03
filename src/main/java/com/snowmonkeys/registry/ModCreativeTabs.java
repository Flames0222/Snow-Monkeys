package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SnowMonkeys.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.snowmonkeys"))
                    .icon(() -> ModItems.SNOW_MONKEY_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SNOW_MONKEY_SPAWN_EGG.get());
                        output.accept(ModItems.HOT_SPRING_WATER_BUCKET.get());
                        output.accept(ModItems.ONSEN_EGG.get());
                        output.accept(ModItems.ONSEN_STONE.get());
                        output.accept(ModItems.HOT_SPRING_VENT.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
