package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.CobwebBall;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<PermanentItemEntity>> PERMANENT_ITEM_ENTITY = register("permanent_item_entity", EntityType.Builder.<PermanentItemEntity>of(PermanentItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(64).setShouldReceiveVelocityUpdates(true).updateInterval(2));
    public static final DeferredHolder<EntityType<?>, EntityType<CobwebBall>> COBWEB_BALL = register("cobweb_ball", EntityType.Builder.<CobwebBall>of(CobwebBall::new, MobCategory.MISC).sized(0.4F, 0.4F).setTrackingRange(4).setShouldReceiveVelocityUpdates(true).updateInterval(20));

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(EnigmaticLegacy.MODID + ":" + name));
    }
}
