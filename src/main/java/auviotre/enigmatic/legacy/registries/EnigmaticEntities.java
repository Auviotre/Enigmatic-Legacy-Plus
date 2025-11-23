package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.IchorSprite;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.entity.PiglinWanderer;
import auviotre.enigmatic.legacy.contents.entity.projectile.CobwebBall;
import auviotre.enigmatic.legacy.contents.entity.projectile.DragonBreathArrow;
import auviotre.enigmatic.legacy.contents.entity.projectile.ThrownIchorSpear;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class EnigmaticEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<IchorSprite>> ICHOR_SPRITE = register("ichor_sprite", EntityType.Builder.of(IchorSprite::new, MobCategory.CREATURE).sized(0.35F, 0.6F).eyeHeight(0.36F).ridingOffset(0.04F).clientTrackingRange(8).updateInterval(2));
    public static final DeferredHolder<EntityType<?>, EntityType<PiglinWanderer>> PIGLIN_WANDERER = register("piglin_wanderer", EntityType.Builder.of(PiglinWanderer::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<PermanentItemEntity>> PERMANENT_ITEM_ENTITY = register("permanent_item_entity", EntityType.Builder.<PermanentItemEntity>of(PermanentItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2F).clientTrackingRange(64).updateInterval(2));
    public static final DeferredHolder<EntityType<?>, EntityType<CobwebBall>> COBWEB_BALL = register("cobweb_ball", EntityType.Builder.<CobwebBall>of(CobwebBall::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(4).updateInterval(10));
    public static final DeferredHolder<EntityType<?>, EntityType<DragonBreathArrow>> DRAGON_BREATH_ARROW = register("dragon_breath_arrow", EntityType.Builder.<DragonBreathArrow>of(DragonBreathArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownIchorSpear>> THROWN_ICHOR_SPEAR = register("ichor_spear", EntityType.Builder.<ThrownIchorSpear>of(ThrownIchorSpear::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(4).setUpdateInterval(20));

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(EnigmaticLegacy.MODID + ":" + name));
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void d(@NotNull EntityAttributeCreationEvent event) {
            event.put(ICHOR_SPRITE.get(), IchorSprite.createAttributes().build());
            event.put(PIGLIN_WANDERER.get(), PiglinWanderer.createAttributes().build());
        }
    }
}
