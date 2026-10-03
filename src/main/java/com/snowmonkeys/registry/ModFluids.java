package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.fluid.HotSpringFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, SnowMonkeys.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, SnowMonkeys.MODID);

    public static final DeferredHolder<FluidType, FluidType> HOT_SPRING_TYPE = FLUID_TYPES.register("hot_spring_water",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.snowmonkeys.hot_spring_water")
                    .canSwim(true)
                    .canDrown(true)
                    .canExtinguish(true)
                    .canHydrate(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .fallDistanceModifier(0.0F)
                    .temperature(320)
                    .density(1000)
                    .viscosity(1000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static final DeferredHolder<Fluid, HotSpringFluid.Source> HOT_SPRING_WATER =
            FLUIDS.register("hot_spring_water", () -> new HotSpringFluid.Source(properties()));
    public static final DeferredHolder<Fluid, HotSpringFluid.Flowing> FLOWING_HOT_SPRING_WATER =
            FLUIDS.register("flowing_hot_spring_water", () -> new HotSpringFluid.Flowing(properties()));

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(HOT_SPRING_TYPE, HOT_SPRING_WATER, FLOWING_HOT_SPRING_WATER)
                .bucket(ModItems.HOT_SPRING_WATER_BUCKET)
                .block(ModBlocks.HOT_SPRING_WATER)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5)
                .explosionResistance(100.0F);
    }

    private ModFluids() {
    }
}
