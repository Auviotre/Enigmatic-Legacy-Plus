package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.etherium.EtheriumArmor;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EnigmaticEye extends BaseCurioItem {
    public static ModConfigSpec.BooleanValue quoteSubtitles;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            EnigmaticEye.quoteSubtitles = builder.define("quoteSubtitles", true);
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
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
    public Component getName(ItemStack stack) {
        if (this.isDormant(stack))
            return Component.translatable("item.enigmaticlegacy.enigmatic_eye_dormant");
        else
            return Component.translatable("item.enigmaticlegacy.enigmatic_eye_active");
    }

    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        int animTicks = pStack.getOrDefault(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);

        if (animTicks > 0) {
            pStack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), animTicks - 1);
        } else if (animTicks == 0) {
            pStack.set(EnigmaticComponents.ACTIVATION_ANIMATION.get(), -1);
            this.setDormant(pStack, false);
        }

        if (pEntity instanceof Player player && !this.isDormant(pStack)) {
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);

            if (!data.getUnlockedNarrator()) {
                data.setUnlockedNarrator(true);
                Quote.getRandom(Quote.NARRATOR_INTROS).play((ServerPlayer) player, 60);
            }
        }

        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
        ItemStack stack = player.getItemInHand(pHand);

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
                    // data.needsSync = true;

                    if (player instanceof ServerPlayer serverPlayer) {
                        Quote.getRandom(Quote.NARRATOR_INTROS).play(serverPlayer, 80);
                    }
                }
            }

            return InteractionResultHolder.success(stack);
        }

        return super.use(level, player, pHand);
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
}
