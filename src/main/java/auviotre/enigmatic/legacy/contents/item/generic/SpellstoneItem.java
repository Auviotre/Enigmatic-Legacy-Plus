package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public abstract class SpellstoneItem extends BaseCurioItem implements ISpellstone {
    protected List<ResourceKey<DamageType>> immunityList = new ArrayList<>();
    protected HashMap<ResourceKey<DamageType>, Supplier<Float>> resistanceList = new HashMap<>();

    public SpellstoneItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void addKeyText(List<Component> list) {
        try {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase());
        } catch (NullPointerException ignored) {
        }
    }

    public boolean isResistantTo(DamageSource source) {
        return this.resistanceList.containsKey(source.typeHolder().getKey());
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public boolean isImmuneTo(DamageSource source) {
        return this.immunityList.contains(source.typeHolder().getKey());
    }

    public Supplier<Float> getResistanceModifier(DamageSource source) {
        return this.resistanceList.get(source.typeHolder().getKey());
    }


    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return super.canEquip(slotContext, stack) && ISpellstone.get(slotContext.entity()).isEmpty();
    }
}
