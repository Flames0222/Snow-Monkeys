package com.snowmonkeys.event;

import com.snowmonkeys.SnowMonkeys;
import com.snowmonkeys.registry.ModFluids;
import com.snowmonkeys.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SnowMonkeys.MODID)
public final class GameEvents {
    private static final String COOK_TIME_KEY = SnowMonkeys.MODID + ":onsen_cook_time";
    /** Real onsen tamago take a long, gentle soak; 30 seconds is the game version. */
    private static final int COOK_TICKS = 600;

    private GameEvents() {
    }

    /** Eggs dropped into a hot spring slowly turn into onsen tamago. */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item) || !(item.level() instanceof ServerLevel level)) {
            return;
        }
        if (!item.getItem().is(Items.EGG)) {
            return;
        }
        CompoundTag data = item.getPersistentData();
        if (!item.isInFluidType(ModFluids.HOT_SPRING_TYPE.get())) {
            data.remove(COOK_TIME_KEY);
            return;
        }
        int cookTime = data.getInt(COOK_TIME_KEY) + 1;
        if (cookTime < COOK_TICKS) {
            data.putInt(COOK_TIME_KEY, cookTime);
            return;
        }
        data.remove(COOK_TIME_KEY);
        item.setItem(new ItemStack(ModItems.ONSEN_EGG.get(), item.getItem().getCount()));
        level.sendParticles(ParticleTypes.CLOUD, item.getX(), item.getY() + 0.3, item.getZ(), 6, 0.15, 0.1, 0.15, 0.02);
        level.playSound(null, item.getX(), item.getY(), item.getZ(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,
                SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
