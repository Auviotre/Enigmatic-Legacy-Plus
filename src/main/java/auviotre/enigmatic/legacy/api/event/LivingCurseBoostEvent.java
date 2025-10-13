package auviotre.enigmatic.legacy.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

public class LivingCurseBoostEvent extends LivingEvent implements ICancellableEvent {
    /**
     * This Event is fired when a {@link LivingEntity} interacts with a player
     * which has worn the Ring of Seven Curses for enough time. (the Worthy One)
     **/

    private final @Nullable LivingEntity worthyCursed;

    public LivingCurseBoostEvent(LivingEntity entity, LivingEntity player) {
        super(entity);
        this.worthyCursed = player;
    }

    public LivingEntity getTheWorthyOne() {
        return this.worthyCursed;
    }
}
