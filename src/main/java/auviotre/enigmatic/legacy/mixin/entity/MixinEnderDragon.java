package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.api.entity.AbyssalHeartBearer;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public abstract class MixinEnderDragon extends Mob implements Enemy, AbyssalHeartBearer {
    @Shadow
    public int dragonDeathTime;
    private Player abyssalHeartOwner;

    protected MixinEnderDragon() {
        super(null, null);
        throw new IllegalStateException("Can't touch this");
    }


    @Inject(method = "tickDeath", at = @At("RETURN"), require = 1)
    private void onTickDeath(CallbackInfo info) {
        if (this.dragonDeathTime == 200 && this.level() instanceof ServerLevel) {
            if (this.abyssalHeartOwner != null) {
                CompoundTag data = EnigmaticHandler.getPersistedData(this.abyssalHeartOwner);
                int heartsGained = data.getInt("AbyssalHeartsGained");

                Vec3 position = this.position();
                PermanentItemEntity heart = new PermanentItemEntity(this.level(), position.x, position.y, position.z, EnigmaticItems.ABYSSAL_HEART.toStack());
                heart.setOwnerId(this.abyssalHeartOwner.getUUID());
                this.level().addFreshEntity(heart);

                data.putInt("AbyssalHeartsGained", heartsGained + 1);
            }
        }
    }

    public void dropAbyssalHeart(Player player) {
        this.abyssalHeartOwner = player;
    }
}
