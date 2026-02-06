package auviotre.enigmatic.legacy.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class SummonedEntityEvent extends PlayerEvent {
    private final Entity summonedEntity;

    public SummonedEntityEvent(Player player, Entity entity) {
        super(player);
        this.summonedEntity = entity;
    }

    public Entity getSummonedEntity() {
        return this.summonedEntity;
    }
}
