package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.capability.IAntiqueBagHandler;
import auviotre.enigmatic.legacy.contents.gui.AntiqueBagContainerMenu;
import auviotre.enigmatic.legacy.contents.item.books.TheInfinitum;
import auviotre.enigmatic.legacy.contents.item.books.TheTwist;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticCapability;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AntiqueBag extends BaseItem {
    public static ModConfigSpec.ConfigValue<List<? extends String>> extraBookList;

    public AntiqueBag() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.antique_bag").push("else.antiqueBag");
        extraBookList = builder.defineList("extraBookList", List.of(), () -> "minecraft:book", Objects::nonNull);
        builder.pop(2);
    }

    public static boolean isBook(ItemStack stack) {
        return stack.is(ItemTags.BOOKSHELF_BOOKS) || extraBookList.get().contains(IItemHelper.getLocation(stack.getItem()).toString());
    }

    public static ItemStack getBook(ItemStack stack, LivingEntity entity) {
        Optional<IAntiqueBagHandler> optional = EnigmaticCapability.get(entity, EnigmaticCapability.ANTIQUE_BAG_INVENTORY);
        if (!hasBag(entity) || optional.isEmpty()) return ItemStack.EMPTY;
        IAntiqueBagHandler handler = optional.get();
        return handler.findBook(stack.getItem());
    }

    public static boolean hasBook(ItemStack stack, LivingEntity entity) {
        return hasBag(entity) && !getBook(stack, entity).isEmpty();
    }

    public static boolean hasBag(LivingEntity entity) {
        Item antiqueBag = EnigmaticItems.ANTIQUE_BAG.asItem();
        boolean enderCheck = entity instanceof Player player && player.getEnderChestInventory().countItem(antiqueBag) > 0;
        return !EnigmaticHandler.getItemRaw(entity, EnigmaticItems.ANTIQUE_BAG).isEmpty() || enderCheck;
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

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (event.getEntity().getType().is(Tags.EntityTypes.BOSSES)) {
                    if (AntiqueBag.hasBook(EnigmaticItems.THE_TWIST.toStack(), attacker) && EnigmaticHandler.canUse(attacker, EnigmaticItems.THE_TWIST.toStack())) {
                        event.setAmount(event.getAmount() * (1 + 0.001F * TheTwist.specialDamageBoost.get() / 3));
                    }
                    if (AntiqueBag.hasBook(EnigmaticItems.THE_INFINITUM.toStack(), attacker) && EnigmaticHandler.isTheWorthyOne(attacker)) {
                        event.setAmount(event.getAmount() * (1 + 0.001F * TheInfinitum.specialDamageBoost.get()));
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamaged(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide() && EnigmaticHandler.isTheWorthyOne(entity)) {
                if (AntiqueBag.hasBook(EnigmaticItems.THE_INFINITUM.toStack(), entity))
                    entity.getAttributes().addTransientAttributeModifiers(getModifier());
                else if (!entity.getWeaponItem().is(EnigmaticItems.THE_INFINITUM))
                    entity.getAttributes().removeAttributeModifiers(getModifier());
            }
        }

        private static Multimap<Holder<Attribute>, AttributeModifier> getModifier() {
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
            ResourceLocation location = IItemHelper.getLocation(EnigmaticItems.THE_INFINITUM.get());
            builder.put(EnigmaticAttributes.LIFESTEAL, new AttributeModifier(location, 0.01 * TheInfinitum.lifeSteal.getAsInt(), AttributeModifier.Operation.ADD_VALUE));
            return builder.build();
        }
    }
}
