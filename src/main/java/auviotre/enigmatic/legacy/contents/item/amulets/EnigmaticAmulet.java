package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

import java.util.List;

public class EnigmaticAmulet extends BaseCurioItem {
    public EnigmaticAmulet() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.RARE));
    }

    public EnigmaticAmulet(Properties properties) {
        super(properties);
    }

    public static AmuletColor getColor(ItemStack amulet) {
        if (!amulet.has(EnigmaticComponents.AMULET_COLOR)) return AmuletColor.RED;
        Float colorVar = amulet.getOrDefault(EnigmaticComponents.AMULET_COLOR, 0.0F);
        return evaluateColor(colorVar);
    }

    public static ItemStack setColor(@NotNull ItemStack amulet, AmuletColor color) {
        if (amulet.is(EnigmaticItems.ENIGMATIC_AMULET)) {
            amulet.set(EnigmaticComponents.AMULET_COLOR, color.getColorVar());
        }
        return amulet;
    }

    private static AmuletColor evaluateColor(float colorVar) {
        float var = (int) (colorVar * 10F) * 0.1F;
        for (AmuletColor color : AmuletColor.values())
            if (var == color.colorVar) return color;
        return AmuletColor.RED;
    }

    public static boolean hasColor(LivingEntity entity, AmuletColor color) {
        if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.ASCENSION_AMULET))
            return true;

        ItemStack stack = EnigmaticHandler.getCurio(entity, EnigmaticItems.ENIGMATIC_AMULET);
        return !stack.isEmpty() && getColor(stack) == color;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift4");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmulet1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmulet2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmulet3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmulet4");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }

        String name = stack.get(EnigmaticComponents.AMULET_NAME);
        if (name != null) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletInscription", ChatFormatting.RED, name);
        }

        TooltipHandler.line(list);
        this.addAttributes(list, stack);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        TooltipHandler.line(list, "curios.modifiers.charm", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletModifier" + getColor(stack));
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        context.entity().getAttributes().removeAttributeModifiers(getAllModifiers(context.entity()));
        super.onUnequip(context, newStack, stack);
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().addTransientAttributeModifiers(getModifiers(stack, entity));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack amulet, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        AmuletColor color = getColor(amulet);
        if (color == AmuletColor.RED) {
            map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        } else if (color == AmuletColor.AQUA) {
            map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), entity.isSprinting() ? 0.15F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (color == AmuletColor.MAGENTA) {
            map.put(Attributes.GRAVITY, new AttributeModifier(getLocation(this), -0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (color == AmuletColor.BLUE) {
            map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(getLocation(this), 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (color == AmuletColor.VIOLET) {
            map.put(EnigmaticAttributes.PROJECTILE_DEFLECT, new AttributeModifier(getLocation(this), 0.15F, AttributeModifier.Operation.ADD_VALUE));
        }
        return map;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAllModifiers(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), entity.isSprinting() ? 0.15F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.GRAVITY, new AttributeModifier(getLocation(this), -0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(getLocation(this), 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(EnigmaticAttributes.PROJECTILE_DEFLECT, new AttributeModifier(getLocation(this), 0.15F, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }

    public enum AmuletColor {
        RED(0.1F),
        AQUA(0.2F),
        VIOLET(0.3F),
        MAGENTA(0.4F),
        GREEN(0.5F),
        BLACK(0.6F),
        BLUE(0.7F);

        private final float colorVar;

        AmuletColor(float colorVar) {
            this.colorVar = colorVar;
        }

        public static AmuletColor getSeededColor(RandomSource rand) {
            return values()[rand.nextInt(values().length)];
        }

        public float getColorVar() {
            return this.colorVar;
        }

    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void getBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
            LivingEntity entity = event.getEntity();
            if (hasColor(entity, AmuletColor.GREEN)) {
                event.setNewSpeed(event.getOriginalSpeed() * 0.25F + event.getNewSpeed());
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide()) {
                if (hasColor(attacker, AmuletColor.BLACK)) attacker.heal(event.getNewDamage() * 0.1F);
            }
        }

        @SubscribeEvent
        private static void onEquip(@NotNull CurioCanEquipEvent event) {
            if (event.getEntity() instanceof Player player && player.isCreative()) return;
            if (event.getStack().is(EnigmaticTags.Items.AMULETS)) {
                HolderLookup<Item> holder = event.getEntity().level().holderLookup(Registries.ITEM);
                holder.get(EnigmaticTags.Items.AMULETS).ifPresent(holders -> {
                    for (Holder<Item> itemHolder : holders) {
                        if (EnigmaticHandler.hasCurio(event.getEntity(), itemHolder.value())) {
                            event.setEquipResult(TriState.FALSE);
                            return;
                        }
                    }
                });
            }
        }
    }
}
