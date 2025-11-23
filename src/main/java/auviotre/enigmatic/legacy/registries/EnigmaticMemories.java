package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class EnigmaticMemories {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPE = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, EnigmaticLegacy.MODID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<LivingEntity>> ICHOR_SPRITE_OWNER = MEMORY_TYPE.register("ichor_sprite_owner", () -> new MemoryModuleType<>(Optional.empty()));
}
