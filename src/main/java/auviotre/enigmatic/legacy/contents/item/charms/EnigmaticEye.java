package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.event.EndPortalActivatedEvent;
import auviotre.enigmatic.legacy.api.event.EnterBlockEvent;
import auviotre.enigmatic.legacy.api.event.SummonedEntityEvent;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
            EnigmaticEye.quoteSubtitles = builder.define("quoteSubtitles", true);
            EnigmaticEye.deathQuoteChance = builder.defineInRange("deathQuoteChance", 80, 0, 100);
        }
    }
//    private static final ResourceLocation EYE_ADVANCEMENT = new ResourceLocation(EnigmaticLegacy.MODID, "book/relics/enigmatic_eye");

    public EnigmaticEye() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerVariants() {
        ItemProperties.register(this, ResourceLocation.withDefaultNamespace("enigmatic_eye_activated"), (stack, world, entity, number) -> {
            if (!this.isDormant(stack))
                return 1F;

            int animTicks = stack.getOrDefault(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);

            if (animTicks > -1) {
                float result;

                if (animTicks > 2) {
                    result = 0.4F;
                } else {
                    result = 0.8F;
                }

                return result;
            }

            return 0F;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        TooltipHandler.line(list);
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

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !this.isDormant(stack);
    }

    @Override
    @NotNull
    public Component getName(@NotNull ItemStack stack) {
        if (this.isDormant(stack))
            return Component.translatable("item.enigmaticlegacy.enigmatic_eye_dormant");
        else
            return Component.translatable("item.enigmaticlegacy.enigmatic_eye_active");
    }

    public void inventoryTick(ItemStack pStack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        int animTicks = pStack.getOrDefault(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);

        if (animTicks > 0) {
            pStack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), animTicks - 1);
        } else if (animTicks == 0) {
            pStack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);
            this.setDormant(pStack, false);
        }

        if (entity instanceof Player player && !this.isDormant(pStack)) {
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);

            if (data.getUnlockedNarrator()) return;

            data.setUnlockedNarrator(true);
            Quote.getRandom(Quote.NARRATOR_INTROS).play((ServerPlayer) player, Quote.PlayOptions.defaultPlay().delay(60));
        }

        super.inventoryTick(pStack, level, entity, slotId, isSelected);
    }
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isDormant(stack) && !stack.has(EnigmaticComponents.ACTIVATION_ANIMATION)) {

            this.activateWithAnimation(stack);
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);

            if (!level.isClientSide) {

                level.playSound(null, player.blockPosition(),
                        EnigmaticSounds.CHARGED_ON.get(),
                        SoundSource.PLAYERS, 1.0F,
                        0.95F + player.getRandom().nextFloat() * 0.1F
                );

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

        if (!this.isDormant(stack)) {
            if (context.entity() instanceof Player) {
                CuriosApi.addSlotModifier(attributes, "charm", getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
                attributes.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(getLocation(this), 3.0, AttributeModifier.Operation.ADD_VALUE));
            }
        }

        return attributes;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return super.getAttributesTooltip(tooltips, context, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {

        @SubscribeEvent
        public static void onPlayerTravel(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
            Player player = event.getEntity();
            ResourceKey<Level> playerDimension = player.level().dimension();

            if (player instanceof ServerPlayer serverPlayer) {
                if (playerDimension == Level.NETHER) {
                    Quote.SULFUR_AIR.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(240));
                } else if (playerDimension == Level.END) {
                    Quote.TORTURED_ROCKS.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(240));
                }
            }
        }

        @SubscribeEvent
        public static void onEndPortal(EndPortalActivatedEvent event) {
            Player player = event.getEntity();

            if (player instanceof ServerPlayer serverPlayer) {
                Quote.END_DOORSTEP.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(40));
            }
        }

        @SubscribeEvent
        public static void onEntitySummon(SummonedEntityEvent event) {
            Player player = event.getEntity();
            Entity entity = event.getSummonedEntity();

            if (player instanceof ServerPlayer serverPlayer) {
                if (entity instanceof WitherBoss) {
                    Quote.COUNTLESS_DEAD.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(20));
                } else if (entity instanceof EnderDragon) {
                    Quote.HORRIBLE_EXISTENCE.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(100));
                }
            }
        }

        @SubscribeEvent
        public static void onEnteredBlock(EnterBlockEvent event) {
            Player player = event.getEntity();

            if (player instanceof ServerPlayer serverPlayer && event.getBlockState().getBlock() == Blocks.END_GATEWAY) {
                Quote.I_WANDERED.play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().once().delay(160));
            }
        }

        @SubscribeEvent
        public static void onConfirmedDeath(@NotNull LivingDeathEvent event) {
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
        public static void onAttack(@NotNull AttackEntityEvent event) {
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

            if (event.isEndConquered()) return;
            if (Math.clamp(RANDOM.nextInt(), 0, 100) > deathQuoteChance.get()) return;

            if (data.getBoolean("DeathFromEntity"))
                Quote.getRandom(Quote.DEATH_QUOTES_ENTITY).play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(10));
            else
                Quote.getRandom(Quote.DEATH_QUOTES).play(serverPlayer, Quote.PlayOptions.defaultPlay().ifUnlocked().delay(10));

            data.remove("DeathFromEntity");
        }
    }
}