package com.snowmonkeys.client;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.entity.SnowMonkey;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Four-legged macaque. The body pivots at the hips, so when bathing it tips upright into a sitting pose;
 * the head and arms stay attached to it.
 */
public class SnowMonkeyModel<T extends SnowMonkey> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SnowMonkeys.id("snow_monkey"), "main");

    private static final float SIT_ANGLE = 0.95F;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart tail;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public SnowMonkeyModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.tail = this.body.getChild("tail");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -5.0F, -8.0F, 7.0F, 6.0F, 10.0F),
                PartPose.offset(0.0F, 17.0F, 4.0F));

        body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 5.0F)
                        .texOffs(22, 16).addBox(-1.5F, 0.0F, -6.0F, 3.0F, 2.0F, 1.0F)
                        .texOffs(22, 19).addBox(-4.0F, -1.5F, -3.0F, 1.0F, 2.0F, 1.0F)
                        .texOffs(26, 19).addBox(3.0F, -1.5F, -3.0F, 1.0F, 2.0F, 1.0F),
                PartPose.offset(0.0F, -4.0F, -8.0F));

        body.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-2.0F, 1.0F, -6.0F));
        body.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(8, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(2.0F, 1.0F, -6.0F));
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(32, 16).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.6F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(16, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(24, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(2.0F, 18.0F, 4.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        if (entity.isBathing()) {
            float breathe = Mth.sin(ageInTicks * 0.05F) * 0.04F;
            // Sit upright, tilt the face toward the sky, let the arms hang, stretch the legs forward.
            this.body.xRot = -SIT_ANGLE + breathe;
            this.head.xRot += SIT_ANGLE - breathe - 0.25F;
            this.rightArm.xRot = SIT_ANGLE - 0.35F;
            this.leftArm.xRot = SIT_ANGLE - 0.35F;
            this.rightArm.zRot = 0.2F;
            this.leftArm.zRot = -0.2F;
            this.rightLeg.xRot = -1.45F;
            this.leftLeg.xRot = -1.45F;
            this.rightLeg.yRot = 0.25F;
            this.leftLeg.yRot = -0.25F;
            this.tail.xRot = 1.2F;
            return;
        }

        float swing = limbSwing * 0.6662F;
        float amount = 1.2F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(swing) * amount;
        this.leftLeg.xRot = Mth.cos(swing + Mth.PI) * amount;
        this.rightArm.xRot = Mth.cos(swing + Mth.PI) * amount;
        this.leftArm.xRot = Mth.cos(swing) * amount;
        this.tail.zRot = Mth.sin(ageInTicks * 0.1F) * 0.15F;
        this.tail.xRot += Mth.cos(swing) * 0.2F * limbSwingAmount;
    }
}
