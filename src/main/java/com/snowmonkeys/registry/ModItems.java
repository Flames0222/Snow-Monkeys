package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SnowMonkeys.MODID);

    public static final DeferredItem<DeferredSpawnEggItem> SNOW_MONKEY_SPAWN_EGG = ITEMS.register("snow_monkey_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SNOW_MONKEY, 0x8E8172, 0xD9706B, new Item.Properties()));

    public static final DeferredItem<BucketItem> HOT_SPRING_WATER_BUCKET = ITEMS.register("hot_spring_water_bucket",
            () -> new BucketItem(ModFluids.HOT_SPRING_WATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    /** Onsen tamago: an egg slow-cooked by leaving it in a hot spring. */
    public static final DeferredItem<Item> ONSEN_EGG = ITEMS.register("onsen_egg",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(5)
                    .saturationModifier(0.7F)
                    .effect(new MobEffectInstance(ModEffects.WARMTH, 1200, 0), 1.0F)
                    .build())));

    public static final DeferredItem<BlockItem> ONSEN_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.ONSEN_STONE);
    public static final DeferredItem<BlockItem> HOT_SPRING_VENT = ITEMS.registerSimpleBlockItem(ModBlocks.HOT_SPRING_VENT);

    private ModItems() {
    }
}
