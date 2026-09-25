package auviotre.enigmatic.legacy.api.entity;

import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

public interface AbyssalHeartBearer {
    void addAttacker(UUID uuid);

    Set<UUID> getAllAttackers();

    void dropAbyssalHeart(Player player);
}