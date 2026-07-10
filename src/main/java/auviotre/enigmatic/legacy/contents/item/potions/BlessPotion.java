package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class BlessPotion extends BaseDrinkableItem {
    public BlessPotion() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON).craftRemainder(Items.GLASS_BOTTLE)
                .component(EnigmaticComponents.CURSED, true).component(EnigmaticComponents.BLESSED, true));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessPotion1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessPotion2");
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        int removeCount = 0;
        List<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance effect : activeEffects) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                if (player.removeEffect(effect.getEffect())) removeCount++;
            }
        }
        if (EnigmaticHandler.isTheBlessedOne(player))
            player.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 1800 + 480 * removeCount, 1));
        else player.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 1200 + 300 * removeCount));
    }
}
