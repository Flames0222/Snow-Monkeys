package com.snowmonkeys.client;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.registry.ModEntities;
import com.snowmonkeys.registry.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = SnowMonkeys.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private static final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow");
    private static final ResourceLocation WATER_OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");
    /** Milky turquoise onsen water. */
    private static final int HOT_SPRING_TINT = 0xD06ED6C8;

    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.HOT_SPRING_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_HOT_SPRING_WATER.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SNOW_MONKEY.get(), SnowMonkeyRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SnowMonkeyModel.LAYER_LOCATION, SnowMonkeyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return WATER_FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return WATER_OVERLAY;
            }

            @Override
            public int getTintColor() {
                return HOT_SPRING_TINT;
            }
        }, ModFluids.HOT_SPRING_TYPE.get());
    }
}
