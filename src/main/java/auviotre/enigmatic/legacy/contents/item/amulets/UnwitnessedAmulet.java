package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class UnwitnessedAmulet extends BaseCurioItem {
    public UnwitnessedAmulet() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.RARE));
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet3");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet4");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet5");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmulet6");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.unwitnessedAmuletUse");
    }

    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand hand) {
        ItemStack stack = EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), EnigmaticAmulet.AmuletColor.getSeededColor(player.getRandom()));
        stack.set(EnigmaticComponents.AMULET_NAME, player.getGameProfile().getName());
        player.playSound(EnigmaticSounds.CHARGED_ON.get(), 1.0F, 1.0F);
        return InteractionResultHolder.success(stack);
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return false;
    }
}
