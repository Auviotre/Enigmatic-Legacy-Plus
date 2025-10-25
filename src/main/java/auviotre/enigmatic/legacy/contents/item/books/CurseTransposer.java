package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurseTransposer extends EnchantmentTransposer {
    public CurseTransposer() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).component(EnigmaticComponents.CURSED, true));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseTransposer1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseTransposer2");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantmentTransposer3");
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    protected boolean canDisenchant(Player player, ItemStack transposer, @NotNull ItemStack target) {
        if (transposer.is(this) && !EnigmaticHandler.canUse(player, transposer)) return false;
        return super.canDisenchant(player, transposer, target);
    }

    public boolean canTranspose(@NotNull Holder<Enchantment> enchantment) {
        return enchantment.is(EnchantmentTags.CURSE);
    }
}
