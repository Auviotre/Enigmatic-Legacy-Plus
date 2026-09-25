package auviotre.enigmatic.legacy.contents.entity.goal;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public class AvoidTheWorthyGoal extends AvoidEntityGoal<Player> {
    public AvoidTheWorthyGoal(Animal mob, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, Player.class, maxDist, walkSpeedModifier, sprintSpeedModifier, player -> {
            boolean cursePlayer = EnigmaticHandler.isTheWorthyOne(player) && !EnigmaticHandler.hasItem(player, EnigmaticItems.ODE_TO_LIVING);
            boolean notTarget = mob.getTarget() == null || mob.getTarget() != player;
            return mob.getLoveCause() == null && cursePlayer && notTarget && !(mob instanceof NeutralMob);
        });
    }

    public boolean canUse() {
        return EnigmaticHandler.isCurseBoosted(this.mob) && super.canUse();
    }
}
