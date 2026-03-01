package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.entity.projectile.UltimateWitherSkull;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreationHeart extends SpellstoneItem {
    public static ModConfigSpec.IntValue cooldown;
    public CreationHeart() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).fireResistant(), 0x0);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.creation_heart").push("spellstone.creationHeart");
        cooldown = builder.defineInRange("cooldown", 3, 1, 40);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeartSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart8");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return cooldown.get();
    }

    public void addTuneTooltip(List<Component> list) {

    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.isFreezing()) entity.setTicksFrozen(0);

        List<MobEffectInstance> effects = new ArrayList<>(entity.getActiveEffects());

        for (MobEffectInstance effect : effects) {
            MobEffect value = effect.getEffect().value();
            if (Objects.equals(BuiltInRegistries.MOB_EFFECT.getKey(value), ResourceLocation.fromNamespaceAndPath("mana-and-artifice", "chrono-exhaustion"))) {
                continue;
            }

            if (!value.isBeneficial()) entity.removeEffect(effect.getEffect());
        }
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }

    public List<Component> getAttributesTooltip(@NotNull List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public void triggerActiveAbility(ServerLevel level, @NotNull ServerPlayer player, ItemStack stack) {
        this.launchWitherSkull(level, player, player.getRandom().nextDouble() <= 0.25);
        super.triggerActiveAbility(level, player, stack);
    }

    private void launchWitherSkull(ServerLevel level, Player player, boolean invulnerable) {
        level.levelEvent(null, 1024, BlockPos.containing(player.position()), 0);
        RandomSource random = player.getRandom();
        double playerRot = Math.toRadians(player.getYRot() + 90);
        Vec3 look = new Vec3(Math.cos(playerRot), 0, Math.sin(playerRot));
        Vec3 rand = new Vec3(random.nextDouble() - 0.5, random.nextDouble() * 0.2 + 0.1, random.nextDouble() - 0.5).normalize();
        double dot = rand.dot(look.normalize());
        rand = rand.subtract(look.scale(dot)).normalize().scale(1.2);
        look = look.normalize().scale(-2);
        Vec3 pl = look.add(player.getEyePosition()).add(0, 1.6, 0);
        Vec3 end = pl.add(rand);

        LivingEntity target = EnigmaticHandler.getObservedEntity(player, level, 3, 64);
        UltimateWitherSkull skull = new UltimateWitherSkull(level, player, target);
        skull.setDeltaMovement(player.getLookAngle().scale(0.1));
        skull.xRotO = player.xRotO;
        skull.yRotO = player.yRotO;
        if (invulnerable) skull.setDangerous(true);
        skull.setPos(end.x, end.y, end.z);
        level.addFreshEntity(skull);

        level.sendParticles(ParticleTypes.LARGE_SMOKE, skull.getX(), skull.getY(), skull.getZ(), 8, 0.05, 0.05, 0.05, 0.1);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.CREATION_HEART)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.CREATION_HEART_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (ISpellstone.get(entity).is(EnigmaticItems.CREATION_HEART) || EnigmaticHandler.hasItem(entity, EnigmaticItems.CREATION_HEART)) {
                if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }
    }
}