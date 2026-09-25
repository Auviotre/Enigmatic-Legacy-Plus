package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.api.entity.AbyssalHeartBearer;
import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.contents.entity.misc.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.materials.AbyssalHeart;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.client.AcceptorSyncPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(EnderDragon.class)
public abstract class MixinEnderDragon extends Mob implements Enemy, AbyssalHeartBearer {
    @Shadow
    public int dragonDeathTime;
    @Unique
    private Set<UUID> attackers;

    protected MixinEnderDragon() {
        super(null, null);
        throw new IllegalStateException("Can't touch this");
    }

    @Inject(method = "tickDeath", at = @At("RETURN"), require = 1)
    private void onTickDeath(CallbackInfo info) {
        if (this.dragonDeathTime == 200 && this.level() instanceof ServerLevel level) {
            if (this.attackers != null && !this.attackers.isEmpty()) {
                Vec3 position = this.position();
                PermanentItemEntity heart = new PermanentItemEntity(this.level(), position.x, position.y, position.z, EnigmaticItems.ABYSSAL_HEART.toStack());
                heart.acceptors = new HashSet<>();
                List<ServerPlayer> players = level.getPlayers(player -> true);
                Set<UUID> attackers = getAllAttackers();
                for (UUID attacker : attackers) {
                    Player player = level.getPlayerByUUID(attacker);
                    if (player instanceof ServerPlayer sp && players.contains(sp)) {
                        CompoundTag data = EnigmaticHandler.getPersistedData(sp);
                        int heartsGained = data.getInt("AbyssalHeartsGained");
                        if (heartsGained < AbyssalHeart.getMaxCount(player)) {
                            heart.acceptors.add(player.getUUID());
                            data.putInt("AbyssalHeartsGained", heartsGained + 1);
                        }
                    }
                }
                this.level().addFreshEntity(heart);
                PacketDistributor.sendToAllPlayers(new AcceptorSyncPacket(heart.getId(), heart.acceptors));
            }

            List<ServerPlayer> players = this.level().getEntitiesOfClass(ServerPlayer.class, EnigmaticHandler.getBoundingBoxAroundEntity(this, 256));
            players.forEach(player -> Quote.WITH_DRAGONS.play(player, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(140)));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addSaveData(CompoundTag compoundTag, CallbackInfo info) {
        CompoundTag attacker = new CompoundTag();
        int i = 0;
        if (this.attackers != null) {
            for (UUID uuid : this.attackers) {
                if (uuid != null) attacker.putUUID(String.valueOf(i++), uuid);
            }
        }
        compoundTag.put("Attackers", attacker);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readData(CompoundTag compoundTag, CallbackInfo info) {
        Set<UUID> set = new HashSet<>();
        CompoundTag attacker = compoundTag.getCompound("Attackers");
        for (String key : attacker.getAllKeys()) {
            if (attacker.hasUUID(key)) {
                UUID tagUUID = attacker.getUUID(key);
                set.add(tagUUID);
            }
        }
        this.attackers = set;
    }

    public void addAttacker(UUID uuid) {
        if (this.attackers == null) this.attackers = new HashSet<>();
        this.attackers.add(uuid);
    }

    public Set<UUID> getAllAttackers() {
        return attackers;
    }

    public void dropAbyssalHeart(Player player) {
    }
}
