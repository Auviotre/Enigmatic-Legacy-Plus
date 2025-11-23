package auviotre.enigmatic.legacy.mixin.compat;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen")
public abstract class MixinApothScreen extends EnchantmentScreen {
    @Shadow
    @Final
    protected ApothEnchantmentMenu menu;

    public MixinApothScreen(EnchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apothic_enchanting/table/ApothEnchantmentMenu;getGoldCount()I"), method = "renderBg")
    private int onRenderBg(ApothEnchantmentMenu instance) {
        return getLapisCount();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apothic_enchanting/table/ApothEnchantmentMenu;getGoldCount()I"), method = "render")
    private int onRender(ApothEnchantmentMenu instance) {
        return getLapisCount();
    }

    @Unique
    private int getLapisCount() {
        Player player = null;
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof Inventory inventory) {
                player = inventory.player;
                break;
            }
        }
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.ENCHANTER_PEARL)) return 64;
        return this.menu.getGoldCount();
    }
}
