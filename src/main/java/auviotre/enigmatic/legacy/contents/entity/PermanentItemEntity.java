package auviotre.enigmatic.legacy.contents.entity;


import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.misc.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.misc.StorageCrystal;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class PermanentItemEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(PermanentItemEntity.class, EntityDataSerializers.ITEM_STACK);
    public float hoverStart = (float) (Math.random() * Math.PI * 2.0D);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    private Vec3 position;

    public PermanentItemEntity(EntityType<PermanentItemEntity> type, Level world) {
        super(type, world);
    }

    public PermanentItemEntity(Level world, double x, double y, double z) {
        this(EnigmaticEntities.PERMANENT_ITEM_ENTITY.get(), world);
        y = y <= world.getMinBuildHeight() ? world.getMinBuildHeight() + 8 : y;

        this.setPos(x, y, z);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.setInvulnerable(true);

        this.setNoGravity(true);
        this.position = new Vec3(x, y, z);
    }

    public PermanentItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private PermanentItemEntity(PermanentItemEntity entity) {
        super(entity.getType(), entity.level());
        this.setItem(entity.getItem().copy());
        this.copyPosition(entity);
        this.age = entity.age;
        this.hoverStart = entity.hoverStart;
    }

    @OnlyIn(Dist.CLIENT)
    public PermanentItemEntity copy() {
        return new PermanentItemEntity(this);
    }

    @OnlyIn(Dist.CLIENT)
    public float getItemHover(float partialTicks) {
        return (this.getAge() + partialTicks) / 20.0F + this.hoverStart;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) d0 = 4.0D;
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getAge() {
        return this.age;
    }

    public boolean dampensVibrations() {
        return this.getItem().is(ItemTags.DAMPENS_VIBRATIONS);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(PermanentItemEntity.ITEM, ItemStack.EMPTY);
    }

    public void tick() {
        if (this.getItem().isEmpty()) {
            this.discard();
        } else {
            if (!this.level().isClientSide && this.position != null) {
                if (!this.position().equals(this.position))
                    this.teleportTo(this.position.x, this.position.y, this.position.z);
            }

            super.tick();
            if (this.pickupDelay > 0 && this.pickupDelay != 32767) --this.pickupDelay;

            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            Vec3 vec3d = this.getDeltaMovement();

            if (!this.isNoGravity()) this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));

            if (this.level().isClientSide) {
                this.noPhysics = false;
                this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ(), ((Math.random() - 0.5) * 2.0), ((Math.random() - 0.5) * 2.0), ((Math.random() - 0.5) * 2.0));
            }

            ++this.age;
            if (!this.level().isClientSide) {
                double d0 = this.getDeltaMovement().subtract(vec3d).lengthSqr();
                if (d0 > 0.01D) {
                    this.hasImpulse = true;
                }
            }

            ItemStack item = this.getItem();
            if (item.isEmpty()) this.discard();
            // Portal Cooldown
            this.setPortalCooldown();
        }
    }

    public int getDimensionChangingDelay() {
        return Short.MAX_VALUE;
    }

    public Entity changeDimension(DimensionTransition transition) {
        return null;
    }

    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || !this.isAlive()) return false;

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            EnigmaticLegacy.LOGGER.warn("[WARN] Attacked permanent item entity with absolute DamageSource: " + source);
            this.kill();
            return true;
        }
        return false;
    }

    public void remove(Entity.RemovalReason reason) {
        if (reason == RemovalReason.DISCARDED || reason == RemovalReason.KILLED) {
            EnigmaticLegacy.LOGGER.warn("[WARN] Removing Permanent Item Entity: " + this);
            if (!this.level().isClientSide) SoulArchive.getInstance().removeItem(this);
        }
        super.remove(reason);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("Health", (short) this.health);
        compound.putShort("Age", (short) this.age);
        compound.putShort("PickupDelay", (short) this.pickupDelay);
        if (this.getThrowerId() != null) compound.putUUID("Thrower", this.getThrowerId());
        if (this.getOwnerId() != null) compound.putUUID("Owner", this.getOwnerId());
        if (!this.getItem().isEmpty()) compound.put("Item", this.getItem().save(this.registryAccess()));
        if (this.position != null) {
            compound.putDouble("BoundX", this.position.x);
            compound.putDouble("BoundY", this.position.y);
            compound.putDouble("BoundZ", this.position.z);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");
        if (compound.contains("PickupDelay")) this.pickupDelay = compound.getShort("PickupDelay");

        if (compound.contains("Owner")) this.owner = compound.getUUID("Owner");

        if (compound.contains("Thrower")) this.thrower = compound.getUUID("Thrower");

        if (compound.contains("BoundX") && compound.contains("BoundY") && compound.contains("BoundZ")) {
            double x = compound.getDouble("BoundX");
            double y = compound.getDouble("BoundY");
            double z = compound.getDouble("BoundZ");
            this.position = new Vec3(x, y, z);
        }

        if (compound.contains("Item", 10)) {
            CompoundTag compoundtag = compound.getCompound("Item");
            this.setItem(ItemStack.parse(this.registryAccess(), compoundtag).orElse(ItemStack.EMPTY));
        } else this.discard();
        if (this.getItem().isEmpty()) this.discard();
    }

    public void playerTouch(Player player) {
        if (this.level() instanceof ServerLevel level) {
            if (this.pickupDelay > 0) return;

            ItemStack stack = this.getItem();
            Item item = stack.getItem();
            int count = stack.getCount();

            ItemStack copy = stack.copy();
            boolean isPlayerOwner = player.getUUID().equals(this.getOwnerId());
            boolean allowPickUp = (item instanceof SoulCrystal || item instanceof StorageCrystal) && isPlayerOwner;

            if (allowPickUp) {
                if (item instanceof StorageCrystal) {
                    StorageCrystal.StorageInfo info = stack.get(EnigmaticComponents.STORAGE_INFO);
                    if (info != null) {
                        ItemStack crystal = info.soulCrystal();
                        StorageCrystal.retrieveDropsFromCrystal(stack, player, crystal, this.position());
                    }
                    SoulArchive.getInstance().removeItem(this);
                } else {
                    if (!SoulCrystal.retrieveSoulFromCrystal(player)) return;
                    else SoulArchive.getInstance().removeItem(this);
                }
                level.sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY(0.5), this.getZ(), 48, 0, 0, 0, 0.03);
                player.take(this, count);
                EnigmaticLegacy.LOGGER.info("Player " + player.getGameProfile().getName() + " picking up: " + this);
                this.discard();
                stack.setCount(0);
            } else if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUUID())) && (count <= 0 || player.getInventory().add(stack))) {
                copy.setCount(copy.getCount() - this.getItem().getCount());
                if (stack.isEmpty()) {
                    player.take(this, count);
                    EnigmaticLegacy.LOGGER.info("Player " + player.getGameProfile().getName() + " picking up: " + this);
                    this.discard();
                    stack.setCount(count);
                }
                player.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
            }
        }
    }

    public boolean containsSoul() {
        return this.getItem().is(EnigmaticItems.SOUL_CRYSTAL);
    }

    public Component getName() {
        Component itextcomponent = this.getCustomName();
        return itextcomponent != null ? itextcomponent : Component.translatable(this.getItem().getDescriptionId());
    }

    public boolean isAttackable() {
        return false;
    }

    public boolean isCurrentlyGlowing() {
        return true;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(PermanentItemEntity.ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(PermanentItemEntity.ITEM, stack);
    }

    public @Nullable UUID getOwnerId() {
        return this.owner;
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.owner = ownerId;
    }

    public @Nullable UUID getThrowerId() {
        return this.thrower;
    }

    public void setThrowerId(@Nullable UUID throwerId) {
        this.thrower = throwerId;
    }

    public void setDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void setNoPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void makeFakeItem() {
        this.setInfinitePickupDelay();
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }
}