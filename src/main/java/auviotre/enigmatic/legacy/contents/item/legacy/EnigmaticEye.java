package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.event.EndPortalActivatedEvent;
import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Random;

public class EnigmaticEye extends BaseCurioItem {
    public static ModConfigSpec.BooleanValue quoteSubtitles;
    public static ModConfigSpec.IntValue deathQuoteChance;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            quoteSubtitles = builder.define("quoteSubtitles", true);
            deathQuoteChance = builder.defineInRange("deathQuoteChance", 60, 0, 100);
        }
    }

    public EnigmaticEye() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerVariants() {
        ItemProperties.register(this, ResourceLocation.withDefaultNamespace("enigmatic_eye_activated"), (stack, world, entity, number) -> {
            if (!this.isDormant(stack)) return 1F;
            int animTicks = stack.getOrDefault(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);
            if (animTicks > -1) return animTicks > 2 ? 0.4F : 0.8F;
            return 0F;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (!this.isDormant(stack)) TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            if (this.isDormant(stack)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEye1");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEye2");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEye3");
            } else {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened1");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened2");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened3");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened4");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened5");
//                TooltipHandler.line(list);
//                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened6");
            }
        } else TooltipHandler.holdShift(list);
    }

    private boolean isDormant(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.DORMANT.get(), true);
    }

    private void setDormant(ItemStack stack, boolean value) {
        stack.set(EnigmaticComponents.DORMANT.get(), value);
    }

    public void activateWithAnimation(ItemStack eye) {
        if (this.isDormant(eye)) {
            eye.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), 4);
        }
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !this.isDormant(stack);
    }

    public Component getName(@NotNull ItemStack stack) {
        if (this.isDormant(stack)) return Component.translatable("item.enigmaticlegacyplus.enigmatic_eye_dormant");
        else return Component.translatable("item.enigmaticlegacyplus.enigmatic_eye_active");
    }

    public void inventoryTick(ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        int animTicks = stack.getOrDefault(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);

        if (animTicks > 0) {
            stack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), animTicks - 1);
        } else if (animTicks == 0) {
            stack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);
            this.setDormant(stack, false);
        }

        if (entity instanceof ServerPlayer player && !this.isDormant(stack)) {
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (!data.getUnlockedNarrator()) {
                data.setUnlockedNarrator(true);
                Quote.getRandom(Quote.NARRATOR_INTROS).play(player, Quote.PlayOptions.defaultPlay().delay(60));
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isDormant(stack) && !stack.has(EnigmaticComponents.ACTIVATION_ANIMATION)) {
            this.activateWithAnimation(stack);
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(), EnigmaticSounds.CHARGED_ON.get(), SoundSource.PLAYERS, 1.0F, 0.95F + player.getRandom().nextFloat() * 0.1F);
                if (!data.getUnlockedNarrator()) {
                    data.setUnlockedNarrator(true);
                    if (player instanceof ServerPlayer serverPlayer) {
                        Quote.getRandom(Quote.NARRATOR_INTROS).play(serverPlayer, Quote.PlayOptions.defaultPlay().delay(80));
                    }
                }
            }
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        if (this.isDormant(stack)) return attributes;
        if (!(context.entity() instanceof Player)) return attributes;
        CuriosApi.addSlotModifier(attributes, "charm", getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        return attributes;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onPlayerTravel(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
            Player player = event.getEntity();
            ResourceKey<Level> playerDimension = player.level().dimension();

            if (!(player instanceof ServerPlayer serverPlayer)) return;

            if (playerDimension == Level.NETHER) {
                Quote.SULFUR_AIR.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(240));
            } else if (playerDimension == Level.END) {
                Quote.TORTURED_ROCKS.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(240));
            }
        }

        @SubscribeEvent
        private static void onEndPortal(@NotNull EndPortalActivatedEvent event) {
            Player player = event.getEntity();

            if (player instanceof ServerPlayer serverPlayer) {
                Quote.END_DOORSTEP.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(40));
            }
        }

        @SubscribeEvent
        private static void onGrantAdvancement(AdvancementEvent.@NotNull AdvancementProgressEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            AdvancementEvent.AdvancementProgressEvent.ProgressType progress = event.getProgressType();
            AdvancementHolder advancement = event.getAdvancement();
            if (progress == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT) {
                if (advancement.id().equals(ResourceLocation.withDefaultNamespace("end/enter_end_gateway"))) {
                    Quote.I_WANDERED.play(player, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(160));
                } else if (advancement.id().equals(ResourceLocation.withDefaultNamespace("end/respawn_dragon"))) {
                    Quote.HORRIBLE_EXISTENCE.play(player, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(100));
                } else if (advancement.id().equals(ResourceLocation.withDefaultNamespace("nether/summon_wither"))) {
                    Quote.COUNTLESS_DEAD.play(player, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(20));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onConfirmedDeath(@NotNull LivingDeathEvent event) {
            DamageSource damageSource = event.getSource();
            Entity victim = event.getEntity();
            Entity attacker = damageSource.getEntity();

            if (victim instanceof ServerPlayer serverPlayer) {
                CompoundTag data = EnigmaticHandler.getPersistedData(serverPlayer);
                boolean deathFromEntity = attacker instanceof LivingEntity;

                data.putBoolean("DeathFromEntity", deathFromEntity);
            }

            if (attacker instanceof ServerPlayer serverPlayer && victim instanceof WitherBoss) {
                EnigmaticData data = serverPlayer.getData(EnigmaticAttachments.ENIGMATIC_DATA);

                int witherKills = data.getWitherKills() + 1;
                data.setWitherKills(witherKills);

                switch (witherKills) {
                    case 1: Quote.BREATHES_RELIEVED.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(140));
                    case 2: Quote.APPALLING_PRESENCE.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(140));
                    case 3: Quote.TERRIFYING_FORM.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(140));
                    case 5: Quote.WHETHER_IT_IS.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(140));
                }
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull AttackEntityEvent event) {
            Player player = event.getEntity();
            Entity target = event.getTarget();

            if (target instanceof EnderDragonPart part) {
                target = part.parentMob;
            }

            if (player instanceof ServerPlayer serverPlayer && target instanceof EnderDragon) {
                Quote.POOR_CREATURE.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(60));
            }
        }

        @SubscribeEvent
        private static void onPlayerRespawn(PlayerEvent.@NotNull PlayerRespawnEvent event) {
            // TODO: Remove magic numbers and make the entire codebase more readable (Sisyphus's labor lol)
            final Random RANDOM = new Random();
            Player player = event.getEntity();

            if (!(player instanceof ServerPlayer serverPlayer)) return;
            CompoundTag data = EnigmaticHandler.getPersistedData(serverPlayer);

            boolean deathFromEntity = data.getBoolean("DeathFromEntity");
            data.remove("DeathFromEntity");

            if (event.isEndConquered()) return;

            // On Cursed Ring Destroyed
            if (data.getBoolean("DestroyedCursedRing")) {
                Quote.getRandom(Quote.RING_DESTRUCTION).play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(10));
                data.remove("DestroyedCursedRing");
                return;
            }

            // Death Quote
            if (deathFromEntity)
                Quote.getRandom(Quote.DEATH_QUOTES_ENTITY).play(serverPlayer, Quote.PlayOptions.defaultPlay().dead().ifUnlocked().delay(10));
            else
                Quote.getRandom(Quote.DEATH_QUOTES).play(serverPlayer, Quote.PlayOptions.defaultPlay().dead().ifUnlocked().delay(10));
        }
    }
}