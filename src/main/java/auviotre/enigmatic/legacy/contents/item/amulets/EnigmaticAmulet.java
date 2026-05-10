package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IAmulet;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
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
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

import java.util.List;

public class EnigmaticAmulet extends BaseCurioItem implements IAmulet {
    public static ModConfigSpec.DoubleValue attackDamage;
    public static ModConfigSpec.IntValue sprintingSpeed;
    public static ModConfigSpec.IntValue projectileDeflect;
    public static ModConfigSpec.IntValue gravity;
    public static ModConfigSpec.DoubleValue miningEfficiency;
    public static ModConfigSpec.IntValue lifesteal;
    public static ModConfigSpec.IntValue swimSpeed;
    protected final Color color;

    public EnigmaticAmulet(Color color) {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE));
        this.color = color;
    }

    public EnigmaticAmulet(Properties properties, Color color) {
        super(properties);
        this.color = color;
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.enigmatic_amulet").push("else.enigmaticAmulet");
        attackDamage = builder.defineInRange("amuletRed", 2.0, 0.0, 10.0);
        sprintingSpeed = builder.defineInRange("amuletAqua", 15, 0, 100);
        projectileDeflect = builder.defineInRange("amuletViolet", 15, 0, 100);
        gravity = builder.defineInRange("amuletMagenta", 20, 0, 100);
        miningEfficiency = builder.defineInRange("amuletGreen", 2.0, 0.0, 10.0);
        lifesteal = builder.defineInRange("amuletBlack", 10, 0, 100);
        swimSpeed = builder.defineInRange("amuletBlue", 25, 0, 100);
        builder.pop(2);
    }

    public static ItemStack randomColor(RandomSource random) {
        List<DeferredItem<?>> list = List.of(
                EnigmaticItems.ENIGMATIC_AMULET_RED,
                EnigmaticItems.ENIGMATIC_AMULET_AQUA,
                EnigmaticItems.ENIGMATIC_AMULET_VIOLET,
                EnigmaticItems.ENIGMATIC_AMULET_MAGENTA,
                EnigmaticItems.ENIGMATIC_AMULET_GREEN,
                EnigmaticItems.ENIGMATIC_AMULET_BLACK,
                EnigmaticItems.ENIGMATIC_AMULET_BLUE
        );
        return list.get(random.nextInt(list.size())).toStack();
    }

    public Color getColor() {
        return color;
    }

    public Component getName(ItemStack stack) {
        return Component.translatable("item.enigmaticlegacyplus.enigmatic_amulet");
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
        Color color = IAmulet.getAmulet(stack);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletModifier" + color, ChatFormatting.GOLD, IAmulet.getAttributeVar(color));
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        context.entity().getAttributes().removeAttributeModifiers(getAllModifiers(context.entity()));
        super.onUnequip(context, newStack, stack);
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().addTransientAttributeModifiers(getModifiers(stack, entity));
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack amulet, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        ResourceLocation location = IItemHelper.getLocation(this);
        Color color = IAmulet.getAmulet(amulet);
        if (color == Color.RED) {
            map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(location, attackDamage.get(), AttributeModifier.Operation.ADD_VALUE));
        } else if (color == Color.AQUA) {
            map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(location, entity.isSprinting() ? sprintingSpeed.get() * 0.01F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (color == Color.VIOLET) {
            map.put(EnigmaticAttributes.PROJECTILE_DEFLECT, new AttributeModifier(location, projectileDeflect.get() * 0.01F, AttributeModifier.Operation.ADD_VALUE));
        } else if (color == Color.MAGENTA) {
            map.put(Attributes.GRAVITY, new AttributeModifier(location, gravity.get() * -0.01F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        } else if (color == Color.GREEN) {
            map.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(location, miningEfficiency.get(), AttributeModifier.Operation.ADD_VALUE));
        } else if (color == Color.BLACK) {
            map.put(EnigmaticAttributes.LIFESTEAL, new AttributeModifier(location, lifesteal.get() * 0.01F, AttributeModifier.Operation.ADD_VALUE));
        } else if (color == Color.BLUE) {
            map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(location, swimSpeed.get() * 0.01F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        return map;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAllModifiers(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        ResourceLocation location = IItemHelper.getLocation(this);
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(location, attackDamage.get(), AttributeModifier.Operation.ADD_VALUE));
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(location, entity.isSprinting() ? sprintingSpeed.get() * 0.01F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(EnigmaticAttributes.PROJECTILE_DEFLECT, new AttributeModifier(location, projectileDeflect.get() * 0.01F, AttributeModifier.Operation.ADD_VALUE));
        map.put(Attributes.GRAVITY, new AttributeModifier(location, gravity.get() * -0.01F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        map.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(location, miningEfficiency.get(), AttributeModifier.Operation.ADD_VALUE));
        map.put(EnigmaticAttributes.LIFESTEAL, new AttributeModifier(location, lifesteal.get() * 0.01F, AttributeModifier.Operation.ADD_VALUE));
        map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(location, swimSpeed.get() * 0.01F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
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
