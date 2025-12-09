package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.capability.IAntiqueBagHandler;
import auviotre.enigmatic.legacy.contents.gui.AntiqueBagContainerMenu;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AntiqueBag extends BaseItem {
    public static ModConfigSpec.ConfigValue<List<? extends String>> extraBookList;

    public AntiqueBag() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.antique_bag").push("else.antiqueBag");
        extraBookList = builder.defineList("extraBookList", List.of(), () -> "minecraft:book", Objects::nonNull);
        builder.pop(2);
    }

    public static boolean isBook(ItemStack stack) {
        return extraBookList.get().contains(getLocation(stack.getItem()).toString()) || stack.is(ItemTags.BOOKSHELF_BOOKS);
    }

    public static boolean hasBook(ItemStack stack, LivingEntity entity) {
        Optional<IAntiqueBagHandler> optional = EnigmaticCapability.get(entity, EnigmaticCapability.ANTIQUE_BAG_INVENTORY);
        if (optional.isEmpty()) return false;
        IAntiqueBagHandler handler = optional.get();
        return !handler.findBook(stack.getItem()).isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.antiqueBag1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.antiqueBag2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.antiqueBag3");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.antiqueBag4");
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        if (!world.isClientSide) player.openMenu(new AntiqueBagContainerMenu.Provider());
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
