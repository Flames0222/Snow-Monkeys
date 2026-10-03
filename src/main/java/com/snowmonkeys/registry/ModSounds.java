package com.snowmonkeys.registry;

import com.snowmonkeys.SnowMonkeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, SnowMonkeys.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SNOW_MONKEY_AMBIENT = register("entity.snow_monkey.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOW_MONKEY_HURT = register("entity.snow_monkey.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOW_MONKEY_DEATH = register("entity.snow_monkey.death");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(SnowMonkeys.id(name)));
    }

    private ModSounds() {
    }
}
