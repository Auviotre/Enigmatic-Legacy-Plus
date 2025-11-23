package auviotre.enigmatic.legacy.compat.thirst;

import auviotre.enigmatic.legacy.contents.item.food.ForbiddenFruit;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

public class ThirstCompatHandler {
    public static void setHellBladeRecover(Player player) {
        PlayerThirst data = player.getData(ModAttachment.PLAYER_THIRST);
        data.setQuenched(data.getThirst());
    }

    @SubscribeEvent
    public void onRegisterThirst(@NotNull RegisterThirstValueEvent event) {
        event.addFood(EnigmaticItems.ASTRAL_FRUIT.get(), 3, 8);
        event.addFood(EnigmaticItems.ENCHANTED_ASTRAL_FRUIT.get(), 3, 8);
        event.addDrink(EnigmaticItems.RECALL_POTION.get(), 6, 8);
        event.addDrink(EnigmaticItems.WORMHOLE_POTION.get(), 6, 8);
        event.addDrink(EnigmaticItems.ICHOR_BOTTLE.get(), 6, 10);
        event.addDrink(EnigmaticItems.REDEMPTION_POTION.get(), 6, 10);
        event.addDrink(EnigmaticItems.UNHOLY_GRAIL.get(), 1, 2);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
        lockThirstData(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTicked(PlayerTickEvent.@NotNull Post event) {
        lockThirstData(event.getEntity());
    }

    private void lockThirstData(@NotNull Player player) {
        PlayerThirst data = player.getData(ModAttachment.PLAYER_THIRST);
        if (ForbiddenFruit.isForbiddenCursed(player)) {
            data.setThirst(20);
            data.setQuenched(0);
        }
    }
}
