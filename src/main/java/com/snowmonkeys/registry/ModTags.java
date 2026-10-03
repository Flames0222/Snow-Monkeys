package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Item> SNOW_MONKEY_FOOD = TagKey.create(Registries.ITEM, SnowMonkeys.id("snow_monkey_food"));
    public static final TagKey<Block> SNOW_MONKEYS_SPAWNABLE_ON =
            TagKey.create(Registries.BLOCK, SnowMonkeys.id("snow_monkeys_spawnable_on"));

    private ModTags() {
    }
}
