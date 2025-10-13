package auviotre.enigmatic.legacy.contents.item.op;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LootGenerator extends BaseItem {
    public List<ResourceKey<LootTable>> lootList = new ArrayList<>();

    public LootGenerator() {
        super(BaseItem.defaultSingleProperties().rarity(Rarity.EPIC).durability(BuiltInLootTables.all().size() * 2));
        Set<ResourceKey<LootTable>> all = BuiltInLootTables.all();
        List<ResourceKey<LootTable>> list = all.stream().sorted().toList();
        for (ResourceKey<LootTable> key : list) {
            String path = key.location().getPath();
            if (path.startsWith("chests/")) this.lootList.add(key);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator2");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGenerator9");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");

        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.lootGeneratorCurrent");
        ResourceKey<LootTable> key = this.lootList.get(getLootTableIndex(stack));
        list.add(Component.literal(key.location().toString()).withStyle(ChatFormatting.GOLD));
    }

    private int getLootTableIndex(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.LOOT_TABLE_ID.get(), 0);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int size = this.lootList.size();
        int id = getLootTableIndex(stack);
        if (!player.isCrouching()) stack.set(EnigmaticComponents.LOOT_TABLE_ID.get(), (id + 1) % size);
        else stack.set(EnigmaticComponents.LOOT_TABLE_ID.get(), (id + size - 1) % size);
        player.swing(hand);

        if (player instanceof ServerPlayer) {
            player.displayClientMessage(Component.literal("Table: " + this.lootList.get(getLootTableIndex(stack)).location()), true);
        }
        return InteractionResultHolder.success(stack);
    }

    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (player == null) return InteractionResult.FAIL;

        if (world.getBlockState(context.getClickedPos()).hasBlockEntity()) {
            if (world.getBlockEntity(context.getClickedPos()) instanceof ChestBlockEntity chest && player.isCrouching()) {
                Direction dir = context.getClickedFace();
                RandomSource lootRandomizer = player.getRandom();
                if (dir == Direction.UP) {
                    chest.setLootTable(this.lootList.get(getLootTableIndex(stack)), lootRandomizer.nextLong());
                    chest.unpackLootTable(player);
                } else if (dir == Direction.DOWN) {
                    HashMap<Item, Integer> lootMap = new HashMap<>();
                    for (int counter = 0; counter < 32768; counter++) {
                        chest.setLootTable(this.lootList.get(getLootTableIndex(stack)), lootRandomizer.nextLong());
                        chest.unpackLootTable(player);

                        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                            ItemStack generatedStack = chest.getItem(slot);
                            Item generatedItem = generatedStack.getItem();
                            int amount = generatedStack.getCount();

                            if (!generatedStack.isEmpty()) {
                                if (lootMap.containsKey(generatedItem)) {
                                    lootMap.put(generatedItem, lootMap.get(generatedItem) + amount);
                                } else {
                                    lootMap.put(generatedItem, amount);
                                }
                            }
                        }
                        chest.clearContent();
                    }

                    EnigmaticLegacy.LOGGER.info("Estimated generation complete in 32768 instances, results:");
                    for (Item theItem : lootMap.keySet()) {
                        EnigmaticLegacy.LOGGER.info("Item: " + theItem.getName(new ItemStack(theItem)).getString() + ", Amount: " + lootMap.get(theItem));
                    }

                    player.sendSystemMessage(Component.translatable("message.enigmaticlegacy.generator_simulation_complete").withStyle(ChatFormatting.DARK_PURPLE));
                } else {
                    chest.clearContent();
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
