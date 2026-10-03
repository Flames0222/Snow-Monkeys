package com.snowmonkeys.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.entity.SnowMonkey;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SnowMonkeyRenderer extends MobRenderer<SnowMonkey, SnowMonkeyModel<SnowMonkey>> {
    private static final ResourceLocation TEXTURE = SnowMonkeys.id("textures/entity/snow_monkey.png");
    /** Same monkey with its eyes closed, for soaking. */
    private static final ResourceLocation RELAXED_TEXTURE = SnowMonkeys.id("textures/entity/snow_monkey_relaxed.png");

    public SnowMonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new SnowMonkeyModel<>(context.bakeLayer(SnowMonkeyModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowMonkey entity) {
        return entity.isBathing() ? RELAXED_TEXTURE : TEXTURE;
    }

    @Override
    protected void scale(SnowMonkey entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        if (entity.isBathing()) {
            // The pose stack is flipped here, so +Y moves the monkey down and sinks it to its shoulders.
            poseStack.translate(0.0F, 0.35F, 0.0F);
        }
    }
}
