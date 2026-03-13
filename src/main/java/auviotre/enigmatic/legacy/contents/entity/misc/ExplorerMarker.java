package auviotre.enigmatic.legacy.contents.entity.misc;

import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class ExplorerMarker extends Entity implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(ExplorerMarker .class, EntityDataSerializers.OPTIONAL_UUID);
    private int lifetime = 0;

    public ExplorerMarker(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ExplorerMarker(Level level, BlockPos pos, LivingEntity owner) {
        super(EnigmaticEntities.EXPLORER_MARKER.get(), level);
        this.setGlowingTag(true);
        this.setOwnerUUID(owner.getUUID());
        this.setPos(pos.getBottomCenter());
        this.lifetime = 180;
    }

    public void tick() {
        if (!this.level().isClientSide()) {
            this.lifetime--;
            if (lifetime <= 0) this.discard();
            BlockEntity blockEntity = this.level().getBlockEntity(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ()));
            if (!(blockEntity instanceof RandomizableContainerBlockEntity entity) || entity.getLootTable() == null) this.discard();
        }
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNER_UUID, Optional.empty());
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            UUID uuid = compoundTag.getUUID("Owner");
            this.setOwnerUUID(uuid);
        }
    }

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
    }

    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }
}
