package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;

public class Insignia extends BaseCurioItem {
    public Insignia() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON).component(EnigmaticComponents.BOOLEAN, true));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.insignia1");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.insignia2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.insignia3");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);

        this.getCustomName(stack).ifPresent(component -> TooltipHandler.line(list, "tooltip.enigmaticlegacy.insigniaName", component));

        Component option = stack.getOrDefault(EnigmaticComponents.BOOLEAN, true) ? Component.translatable("tooltip.enigmaticlegacy.enabled") : Component.translatable("tooltip.enigmaticlegacy.disabled");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.insigniaTagDisplay", option);
    }

    public Optional<Component> getCustomName(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            return Optional.ofNullable(stack.get(DataComponents.CUSTOM_NAME));
        }
        if (stack.has(DataComponents.ITEM_NAME)) {
            return Optional.ofNullable(stack.get(DataComponents.ITEM_NAME));
        }
        return Optional.empty();
    }

    public boolean canSeeTrueName(@Nullable Player player) {
        return player != null && player.isCreative() || EnigmaticHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_EYE);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            ItemStack stack = player.getItemInHand(hand);
            RandomSource random = player.getRandom();
            if (stack.getOrDefault(EnigmaticComponents.BOOLEAN, true)) {
                stack.set(EnigmaticComponents.BOOLEAN, false);
                level.playSound(null, player.blockPosition(), EnigmaticSounds.CHARGED_OFF.get(), SoundSource.PLAYERS, 0.8F + random.nextFloat() * 0.2F, 0.8F + random.nextFloat() * 0.2F);
            } else {
                stack.set(EnigmaticComponents.BOOLEAN, true);
                level.playSound(null, player.blockPosition(), EnigmaticSounds.CHARGED_ON.get(), SoundSource.PLAYERS, 0.8F + random.nextFloat() * 0.2F, 0.8F + random.nextFloat() * 0.2F);
            }
            player.swing(hand);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        CuriosApi.addSlotModifier(attributes, "charm", IItemHelper.getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        return attributes;
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderInsigniaNameplate(Entity entity, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight, EntityRenderDispatcher dispatcher, float partialTick, Font font) {
        double d0 = dispatcher.distanceToSqr(entity);
        if (ClientHooks.isNameplateInRenderDistance(entity, d0)) {
            Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
            if (vec3 == null) return;
            boolean render = font.width(name) > 0;
            boolean override = false;
            if (!render && entity != Minecraft.getInstance().player && EnigmaticItems.INSIGNIA.get().canSeeTrueName(Minecraft.getInstance().player)) {
                name = entity.getDisplayName();
                render = override = true;
            }
            boolean flag = !entity.isDiscrete();
            float f = entity.getBbHeight() + 0.5F;
            int i = 0;
            stack.pushPose();
            stack.translate(vec3.x, vec3.y + 0.5F, vec3.z);
            stack.mulPose(dispatcher.cameraOrientation());
            stack.scale(0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = stack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            float f2 = (float) (-font.width(name) / 2);
            if (render) {
                font.drawInBatch(name, f2, i, 0x20ffffff, false, matrix4f, buffer, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, packedLight);
                if (flag) {
                    font.drawInBatch(name, f2, i, 0xffffffff, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
                }
            }
            if (!override && entity != Minecraft.getInstance().player && EnigmaticItems.INSIGNIA.get().canSeeTrueName(Minecraft.getInstance().player)) {
                stack.pushPose();
                name = Component.literal("(" + entity.getDisplayName().getString() + ")");
                f2 = (float) (-font.width(name) / 2);
                float scale = 0.4F;
                int offset = (int) (-10 * (1.0F / scale));
                stack.scale(scale, scale, scale);
                matrix4f = stack.last().pose();
                font.drawInBatch(name, f2, i - offset, 0x20ffffff, false, matrix4f, buffer, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, packedLight);
                if (flag) font.drawInBatch(name, f2, i - offset, 0xffffffff, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
                stack.popPose();
            }
            stack.popPose();
        }
    }
}
