package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class HunterScroll extends BaseCurioItem {
    public HunterScroll() {
        super(IItemHelper.singleProperties()
                .component(EnigmaticComponents.HUNTER_COUNT, 0)
                .component(EnigmaticComponents.HUNTER_LIST, List.of())
        );
    }

    public static float getCountModifier(ItemStack stack) {
        float count = stack.getOrDefault(EnigmaticComponents.HUNTER_COUNT, 0);
        return count / 100.0F;
    }

    public static float getSpecModifier(ItemStack stack) {
        double size = stack.getOrDefault(EnigmaticComponents.HUNTER_LIST, List.of()).size();
        size = Math.pow(size, 0.7);
        return Mth.floor(0.5 + size);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterScroll2");
        } else TooltipHandler.holdShift(list);
    }

    public List<Component> getAttributesTooltip(List<Component> list, TooltipContext context, ItemStack stack) {
        List<Component> tooltip = super.getAttributesTooltip(list, context, stack);
        MutableComponent component = Component.translatable("tooltip.enigmaticlegacy.hunterScrollAttribute");
        float count = getCountModifier(stack);
        int size = Mth.floor(getSpecModifier(stack));
        if (count >= 0.1)
            list.add(Component.translatable("attribute.modifier.plus.0", String.format("%.01f", count), component).withStyle(ChatFormatting.BLUE));
        if (size > 0)
            list.add(Component.translatable("attribute.modifier.plus.1", size, component).withStyle(ChatFormatting.BLUE));
        return tooltip;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(IItemHelper.getLocation(this), 0.5, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOW)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) return;
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.HUNTER_SCROLL)) {
                ItemStack curio = EnigmaticHandler.getCurio(victim, EnigmaticItems.HUNTER_SCROLL);
                event.setAmount(event.getAmount() - getCountModifier(curio));
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) return;
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.HUNTER_SCROLL)) {
                ItemStack curio = EnigmaticHandler.getCurio(victim, EnigmaticItems.HUNTER_SCROLL);
                event.setNewDamage(event.getNewDamage() * (1 - 0.01F * getSpecModifier(curio)));
            }
        }

        @SubscribeEvent
        private static void onDeath(@NotNull LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();
            if (!(victim instanceof Monster)) return;
            String s = victim.getType().getDescriptionId();
            if (event.getSource().getEntity() instanceof LivingEntity attacker && EnigmaticHandler.hasCurio(attacker, EnigmaticItems.HUNTER_SCROLL)) {
                ItemStack curio = EnigmaticHandler.getCurio(attacker, EnigmaticItems.HUNTER_SCROLL);
                Integer count = curio.getOrDefault(EnigmaticComponents.HUNTER_COUNT, 0);
                curio.set(EnigmaticComponents.HUNTER_COUNT, Math.min(200, count + 1));
                List<String> list = new ArrayList<>(curio.getOrDefault(EnigmaticComponents.HUNTER_LIST, List.of()));
                if (!list.contains(s)) list.add(s);
                curio.set(EnigmaticComponents.HUNTER_LIST, list);
            }
        }
    }
}
