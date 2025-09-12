package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MonsterCharm extends BaseCurioItem {
    public MonsterCharm() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.monsterCharm");
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        list.add(Component.translatable("attribute.modifier.plus.1", 25, Component.translatable("tooltip.enigmaticlegacy.monsterCharmAttribute")).withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("attribute.modifier.plus.0", 1, Component.translatable("attribute.name.looting_level")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public int getLootingLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getLootingLevel(context, lootContext, stack) + 1;
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent.@NotNull Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker && EnigmaticHandler.hasCurio(attacker, this)) {
            if (event.getEntity().getType().is(EntityTypeTags.UNDEAD)) {
                event.setNewDamage(event.getNewDamage() * 1.25F);
            }
        }
    }

    @SubscribeEvent()
    public void onExperienceDrop(@NotNull LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (EnigmaticHandler.hasCurio(player, this) && event.getEntity() instanceof Monster) {
            event.setDroppedExperience(event.getDroppedExperience() + event.getOriginalExperience());
        }
    }
}
