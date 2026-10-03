package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.block.HotSpringVentBlock;
import com.snowmonkeys.block.HotSpringWaterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SnowMonkeys.MODID);

    public static final DeferredBlock<HotSpringWaterBlock> HOT_SPRING_WATER = BLOCKS.register("hot_spring_water",
            () -> new HotSpringWaterBlock(ModFluids.HOT_SPRING_WATER.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_CYAN).noLootTable()));

    /** Pale mineral rock (travertine/sinter) that builds up around hot springs. */
    public static final DeferredBlock<Block> ONSEN_STONE = BLOCKS.registerSimpleBlock("onsen_stone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.CALCITE));

    /** Geothermal vent: slowly heats normal water above it into hot spring water. */
    public static final DeferredBlock<HotSpringVentBlock> HOT_SPRING_VENT = BLOCKS.register("hot_spring_vent",
            () -> new HotSpringVentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 6.0F)
                    .randomTicks()
                    .lightLevel(state -> 6)
                    .sound(SoundType.BASALT)));

    private ModBlocks() {
    }
}
