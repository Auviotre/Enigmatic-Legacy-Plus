package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.spellstones.other.SpellstoneSword;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public class EngineHook extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(EngineHook .class, EntityDataSerializers.INT);
    private @Nullable Entity hookedIn;
    private State currentState;

    public EngineHook(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.currentState = State.FLYING;
    }

    public EngineHook(Player player, Level level, ItemStack stack) {
        this(EnigmaticEntities.ENGINE_HOOK.get(), level);
        this.setOwner(player);
        float xRot = player.getXRot();
        float yRot = player.getYRot();
        float f2 = (float) Math.cos(Math.toRadians(-yRot) - Math.PI);
        float f3 = (float) Math.sin(Math.toRadians(-yRot) - Math.PI);
        double x = player.getX() - f3 * 0.3;
        double y = player.getEyeY() - 0.05;
        double z = player.getZ() - f2 * 0.3;
        this.moveTo(x, y, z, yRot, xRot);
        Vec3 move = player.getLookAngle().scale(2.75);
        this.setDeltaMovement(move);
        this.setYRot((float) Math.toDegrees(Mth.atan2(move.x, move.z)));
        this.setXRot((float) Math.toDegrees(Mth.atan2(move.y, move.horizontalDistance())));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HOOKED_ENTITY, 0);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(EnigmaticLegacy.location("engine_hooked"), -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return attributes;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level().getEntity(i - 1) : null;
        }
        super.onSyncedDataUpdated(key);
    }

    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        if (!(owner instanceof LivingEntity entity)) {
            this.discard();
            return;
        }
        if (this.level().isClientSide || !this.shouldStop(entity)) {
            if (this.currentState == State.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.currentState = State.HOOKED_IN_ENTITY;
                    return;
                }
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
                this.checkCollision();
            } else if (this.currentState == State.HOOKED_IN_BLOCK) {
                this.setDeltaMovement(Vec3.ZERO);
            } else if (this.currentState == State.HOOKED_IN_ENTITY) {
                if (this.hookedIn != null) {
                    if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                        this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                        if (this.hookedIn instanceof LivingEntity living) {
                            living.getAttributes().addTransientAttributeModifiers(getModifiers());
                        }
                    } else {
                        this.setHookedEntity(null);
                        this.currentState = State.FLYING;
                    }
                }
                return;
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (this.getDeltaMovement().length() > 0) this.updateRotation();

            this.setDeltaMovement(this.getDeltaMovement().scale(0.99));
            this.reapplyPosition();
        }
    }

    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) || target.isAlive() && target instanceof ItemEntity;
    }

    private void checkCollision() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() == HitResult.Type.MISS || !EventHooks.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.getOwner() instanceof LivingEntity owner) {
            if (!this.level().isClientSide) {
                this.setHookedEntity(result.getEntity());
                result.getEntity().hurt(damageSources().mobProjectile(this, owner), (float) (owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6F));
            }
            owner.playSound(SoundEvents.CHAIN_PLACE, 0.9F, 1.2F + 0.6F * this.getRandom().nextFloat());
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.currentState = State.HOOKED_IN_BLOCK;
        if (this.getOwner() instanceof LivingEntity owner)
            owner.playSound(SoundEvents.CHAIN_PLACE, 0.9F, 1.2F + 0.6F * this.getRandom().nextFloat());
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(result.distanceTo(this)));
    }

    private void setHookedEntity(@Nullable Entity hookedEntity) {
        this.hookedIn = hookedEntity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, hookedEntity == null ? 0 : hookedEntity.getId() + 1);
    }

    private boolean shouldStop(LivingEntity entity) {
        ItemStack stack = entity.getMainHandItem();
        boolean flag = stack.is(EnigmaticItems.SPELLSTONE_SWORD) && SpellstoneSword.isResonatingWith(stack, EnigmaticItems.LOST_ENGINE);
        if (!entity.isRemoved() && entity.isAlive() && flag && !(this.distanceToSqr(entity) > 4096.0F)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (this.tickCount > 10 && !player.isUsingItem()) {
            this.discard();
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void handleEntityEvent(byte id) {
        if (id == 31 && this.level().isClientSide() && this.currentState == State.HOOKED_IN_BLOCK) this.pull();
        else if (id == 32 && this.level().isClientSide() && this.currentState == State.HOOKED_IN_BLOCK) this.pullStart();
        else super.handleEntityEvent(id);
    }

    private Vec3 getLenModifier(Entity from, Entity to) {
        Vec3 vec3 = to.getEyePosition().subtract(from.getEyePosition());
        if (vec3.length() > 1) return vec3.normalize();
        return vec3;
    }

    public void pull() {
        if (!this.level().isClientSide()) this.level().broadcastEntityEvent(this, (byte) 31);
        Entity owner = this.getOwner();
        if (owner != null) {
            owner.addDeltaMovement(getLenModifier(owner, this).multiply(0.125, 0.16, 0.125));
            owner.addDeltaMovement(owner.getLookAngle().scale(0.072));
        }
    }

    public void pullStart() {
        if (!this.level().isClientSide()) this.level().broadcastEntityEvent(this, (byte) 32);
        Entity owner = this.getOwner();
        if (owner != null) {
            Vec3 vec = getLenModifier(owner, this).multiply(0.32, 0.36, 0.32);
            if (owner.onGround()) owner.setDeltaMovement(vec);
            else owner.setDeltaMovement(owner.getDeltaMovement().scale(0.8).add(vec));
        }
    }

    public void drag() {
        Entity owner = this.getOwner();
        if (owner != null && this.hookedIn != null) {
            this.hookedIn.addDeltaMovement(getLenModifier(this.hookedIn, owner).multiply(0.12, 0.16, 0.12));
        }
    }

    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public void remove(Entity.RemovalReason reason) {
        this.updateOwnerInfo(null);
        super.remove(reason);
    }

    public void onClientRemoval() {
        this.updateOwnerInfo(null);
    }

    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.updateOwnerInfo(this);
    }

    private void updateOwnerInfo(@Nullable EngineHook hook) {
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity owner) {
            owner.getData(EnigmaticAttachments.ENIGMATIC_DATA).setEngineHook(hook == null ? null : hook.getUUID());
        }
        if (hook == null && this.hookedIn instanceof LivingEntity living) {
            living.getAttributes().removeAttributeModifiers(getModifiers());
        }
    }

    public @Nullable Entity getHookedIn() {
        return this.hookedIn;
    }

    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    public enum State {
        FLYING,
        HOOKED_IN_BLOCK,
        HOOKED_IN_ENTITY;
    }
}
