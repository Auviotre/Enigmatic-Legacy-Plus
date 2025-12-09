package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.contents.capability.AntiqueBagCapability;
import auviotre.enigmatic.legacy.contents.capability.IAntiqueBagHandler;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;

import java.util.Optional;

public class EnigmaticCapability {
    public static final EntityCapability<IAntiqueBagHandler, Void> ANTIQUE_BAG_INVENTORY = EntityCapability.createVoid(AntiqueBagCapability.ID, IAntiqueBagHandler.class);

    public static <T> Optional<T> get(LivingEntity entity, EntityCapability<T, Void> capability) {
        return entity == null ? Optional.empty() : Optional.ofNullable(entity.getCapability(capability));
    }
}