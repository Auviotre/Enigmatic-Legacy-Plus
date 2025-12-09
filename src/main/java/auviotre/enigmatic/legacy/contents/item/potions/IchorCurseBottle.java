package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class IchorCurseBottle extends BaseDrinkableItem {
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(EnigmaticEffects.ICHOR_CURSE, 9600 * 4), 1.0F)
            .effect(() -> new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 1200), 0.8F)
            .effect(() -> new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 1200), 0.4F)
            .effect(() -> new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 1200), 0.2F)
            .alwaysEdible().build();

    public IchorCurseBottle() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE).food(FOOD_PROPERTIES).rarity(Rarity.RARE)
                .component(EnigmaticComponents.CURSED, true)
                .component(EnigmaticComponents.BLESSED, true)
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        MutableComponent component = Component.translatable(EnigmaticEffects.ICHOR_CURSE.get().getDescriptionId());
        list.add(Component.translatable("potion.withDuration", component, Component.literal(StringUtil.formatTickDuration(9600 * 4, context.tickRate()))).withStyle(ChatFormatting.RED));
        if (EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player)) {
            component = Component.translatable(MobEffects.ABSORPTION.value().getDescriptionId());
            component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency.4"));
            list.add(Component.translatable("potion.withDuration", component, Component.literal(StringUtil.formatTickDuration(2400, context.tickRate()))).withStyle(ChatFormatting.BLUE));
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        player.eat(level, stack);
        if (EnigmaticHandler.isTheBlessedOne(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4));
        }
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
