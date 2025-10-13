package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MinerRing extends BaseCurioItem {
    private static final int MAX_POINT = 128;
    private final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.BLASTING);

    public MinerRing() {
        super(defaultSingleProperties().component(EnigmaticComponents.MINER_POINT, 0));
    }

    public static int getPoint(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (!stack.has(EnigmaticComponents.MINER_POINT)) return 0;
        int durability = stack.getOrDefault(EnigmaticComponents.MINER_POINT, 0);
        durability = Math.clamp(durability, 0, MAX_POINT);
        stack.set(EnigmaticComponents.MINER_POINT, durability);
        return durability;
    }

    public static void setPoint(ItemStack stack, int damage) {
        stack.set(EnigmaticComponents.MINER_POINT, Mth.clamp(damage, 0, MAX_POINT));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.minerRing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.minerRing2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.minerRing3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.minerRing4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.minerRing5");
        } else TooltipHandler.holdShift(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.PRIMARY && slot.mayPickup(player) && slot.hasItem()) {
            if (other.getBurnTime(RecipeType.BLASTING) > 200 && getPoint(stack) < MAX_POINT) {
                setPoint(stack, getPoint(stack) + other.getBurnTime(RecipeType.BLASTING) / 200);
                ItemStack remain = other.getCraftingRemainingItem();
                if (remain.isEmpty()) other.consume(1, player);
                else access.set(remain);
                player.playSound(SoundEvents.GENERIC_BURN, 1.0F, 0.9F + 0.5F * player.getRandom().nextFloat());
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel) || !entity.isCrouching()) return;
        if (entity.tickCount % 40 == 0 && getPoint(stack) > 0) {
            List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(4));
            for (ItemEntity itemEntity : entities) {
                if (getPoint(stack) <= 0) return;
                ItemStack item = itemEntity.getItem();
                RecipeHolder<BlastingRecipe> holder = quickCheck.getRecipeFor(new SingleRecipeInput(item), level).orElse(null);
                if (holder != null && itemEntity.onGround()) {
                    int count = item.getCount();
                    ItemStack output = holder.value().assemble(new SingleRecipeInput(item), level.registryAccess());
                    if (!output.isEmpty() && getPoint(stack) >= count * 2) {
                        item.shrink(count);
                        serverLevel.sendParticles(ParticleTypes.FLAME, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 4, 0.2, 0.2, 0.2, 0);
                        for (int i = 0; i < count; i++) {
                            ItemEntity out = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), output.copy());
                            out.setDefaultPickUpDelay();
                            level.addFreshEntity(out);
                        }
                        setPoint(stack, getPoint(stack) - count * 2);
                    }
                }
            }
        }
        if (getPoint(stack) < 64 && entity.tickCount % 100 == 0) setPoint(stack, getPoint(stack) + 1);
    }

    public boolean isBarVisible(ItemStack stack) {
        return getPoint(stack) > 0;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round((float) getPoint(stack) * 13.0F / MAX_POINT);
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) getPoint(stack) / MAX_POINT);
        return Mth.hsvToRgb(f / 6.0F, 1.0F, 1.0F);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        private static final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> CHECK = RecipeManager.createCheck(RecipeType.BLASTING);

        @SubscribeEvent
        private static void onBlockDrop(@NotNull BlockDropsEvent event) {
            ServerLevel level = event.getLevel();
            if (event.getBreaker() instanceof LivingEntity breaker && EnigmaticHandler.hasCurio(breaker, EnigmaticItems.MINER_RING)) {
                ItemStack curio = EnigmaticHandler.getCurio(breaker, EnigmaticItems.MINER_RING);
                if (getPoint(curio) <= 0) return;
                List<ItemEntity> drops = event.getDrops().stream().toList();
                for (ItemEntity drop : drops) {
                    ItemStack stack = drop.getItem();
                    if (getPoint(curio) <= 0) return;
                    CHECK.getRecipeFor(new SingleRecipeInput(stack), level).ifPresent(holder -> {
                        ItemStack output = holder.value().assemble(new SingleRecipeInput(stack), level.registryAccess());
                        int count = output.getCount() * stack.getCount();
                        if (!output.isEmpty() && getPoint(curio) >= count * 2) {
                            setPoint(curio, getPoint(curio) - count * 2);
                            event.setDroppedExperience(Math.max(0, event.getDroppedExperience() - count));
                            event.getDrops().remove(drop);
                            level.sendParticles(ParticleTypes.FLAME, drop.getX(), drop.getY(), drop.getZ(), 4, 0.2, 0.2, 0.2, 0);
                            boolean setup = true;
                            for (int i = 0; i < stack.getCount(); i++) {
                                ItemEntity item = new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), output.copy());
                                if (setup) setup = false;
                                else item.setNoPickUpDelay();
                                event.getDrops().add(item);
                            }
                        }
                    });
                }
            }
        }
    }
}
