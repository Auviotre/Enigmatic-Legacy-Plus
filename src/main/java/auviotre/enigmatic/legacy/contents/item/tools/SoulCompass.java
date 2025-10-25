package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.base.Objects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class SoulCompass extends BaseCursedItem {
    @OnlyIn(Dist.CLIENT)
    private CompassWobble wobble;
    @OnlyIn(Dist.CLIENT)
    private CompassWobble wobbleRandom;
    @Nullable
    @OnlyIn(Dist.CLIENT)
    private BlockPos nearestCrystal;

    public SoulCompass() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void setNearestCrystal(BlockPos nearestCrystal) {
        if (!Objects.equal(this.nearestCrystal, nearestCrystal)) {
            if (nearestCrystal != null && this.nearestCrystal != null) {
                this.wobble.rotation = 1.0;
                this.wobble.deltaRotation = 0.3;
            } else {
                this.wobble.rotation = this.wobbleRandom.rotation;
                this.wobble.deltaRotation = this.wobbleRandom.deltaRotation;
            }
            this.nearestCrystal = nearestCrystal;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void registerVariants() {
        this.wobble = new CompassWobble(0.1);
        this.wobbleRandom = new CompassWobble(0.6);
        ItemProperties.register(this, ResourceLocation.withDefaultNamespace("angle"), new ClampedItemPropertyFunction() {
            private final CompassWobble wobble = SoulCompass.this.wobble;
            private final CompassWobble wobbleRandom = SoulCompass.this.wobbleRandom;

            public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed) {
                Entity entity = living != null ? living : stack.getEntityRepresentation();
                if (entity == null) return 0.0F;
                if (level == null && entity.level() instanceof ClientLevel) {
                    level = (ClientLevel) entity.level();
                }
                assert level != null;
                BlockPos target = this.getTargetPosition();
                long gameTime = level.getGameTime();

                if (target != null && !(entity.position().distanceToSqr(target.getX() + 0.5D, entity.position().y(), target.getZ() + 0.5D) < 1.0E-5F)) {
                    boolean isLocalPlayer = living instanceof Player player && player.isLocalPlayer();
                    double bodyRotation;

                    if (isLocalPlayer) bodyRotation = living.getYRot();
                    else return this.randomAngle(gameTime, seed);
                    ItemStack item = EnigmaticHandler.getItem(living, EnigmaticItems.SOUL_COMPASS);
                    if (!EnigmaticHandler.canUse(living, stack) || !ItemStack.isSameItemSameComponents(item, stack) || level.getBiome(living.blockPosition()).is(Biomes.SOUL_SAND_VALLEY))
                        return this.randomAngle(gameTime, seed);

                    bodyRotation = Mth.positiveModulo(bodyRotation / 360.0D, 1.0D);
                    double angle = SoulCompass.this.getAngleTo(Vec3.atCenterOf(target), entity) / ((float) Math.PI * 2F);
                    double otherAngle;

                    if (this.wobble.shouldUpdate(gameTime)) {
                        this.wobble.update(gameTime, 0.5D - (bodyRotation - 0.25D));
                    }

                    otherAngle = angle + this.wobble.rotation;

                    return Mth.positiveModulo((float) otherAngle, 1.0F);
                } else return this.randomAngle(gameTime, seed);
            }

            private float randomAngle(long gameTime, int seed) {
                if (this.wobbleRandom.shouldUpdate(gameTime)) {
                    this.wobbleRandom.update(gameTime, Math.random());
                }
                double randomAngle = this.wobbleRandom.rotation + this.hash(seed) / 2.14748365E9F;
                return Mth.positiveModulo((float) randomAngle, 1.0F);
            }

            private int hash(int value) {
                return value * 1327217883;
            }

            @Nullable
            private BlockPos getTargetPosition() {
                return SoulCompass.this.nearestCrystal;
            }
        });
    }

    private double getAngleTo(Vec3 pos, Entity entity) {
        return Math.atan2(pos.z() - entity.getZ(), pos.x() - entity.getX());
    }

    @OnlyIn(Dist.CLIENT)
    private static class CompassWobble {
        double needleMobility;
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        CompassWobble(double needleMobility) {
            this.needleMobility = needleMobility;
        }

        boolean shouldUpdate(long pGameTime) {
            return this.lastUpdateTick != pGameTime;
        }

        void update(long gameTime, double wobbleAmount) {
            this.lastUpdateTick = gameTime;
            double var = wobbleAmount - this.rotation;
            var = Mth.positiveModulo(var + 0.5D, 1.0D) - 0.5D;
            this.deltaRotation += var * this.needleMobility;
            this.deltaRotation *= 0.8D;

            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<Player, Integer> LAST_SOUL_COMPASS_UPDATE = new WeakHashMap<>();

        @SubscribeEvent
        private static void onPlayerTravel(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                LAST_SOUL_COMPASS_UPDATE.remove(player);
            }
        }

        @SubscribeEvent
        private static void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                if (EnigmaticHandler.hasItem(player, EnigmaticItems.SOUL_COMPASS)) {
                    Integer lastUpdate = LAST_SOUL_COMPASS_UPDATE.get(player);

                    if (lastUpdate == null || player.tickCount - lastUpdate > 10) {
                        var optional = SoulArchive.updateSoulCompass(player);
                        optional.ifPresent(tuple -> {
                            BlockPos pos = tuple.getB();
                            if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 256) {
                                player.level().getChunkAt(pos);
                                UUID id = tuple.getA();
                                AABB box = new AABB(pos).inflate(3);
                                List<PermanentItemEntity> list = player.level().getEntitiesOfClass(PermanentItemEntity.class, box, entity -> entity.getUUID().equals(id));

                                if (list.isEmpty()) SoulArchive.getInstance().removeItem(id);
                            }
                        });
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onPlayerRespawn(PlayerEvent.@NotNull PlayerRespawnEvent event) {
            if (!event.getEntity().level().isClientSide) {
                LAST_SOUL_COMPASS_UPDATE.remove(event.getEntity());
            }
        }
    }
}
