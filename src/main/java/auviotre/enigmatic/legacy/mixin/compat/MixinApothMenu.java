package auviotre.enigmatic.legacy.mixin.compat;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import dev.shadowsoffire.apothic_enchanting.Ench;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import dev.shadowsoffire.apothic_enchanting.table.infusion.InfusionRecipe;
import dev.shadowsoffire.apothic_enchanting.util.MiscUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu")
public abstract class MixinApothMenu extends EnchantmentMenu {

    @Shadow
    protected EnchantmentTableStats stats;

    public MixinApothMenu(int i, Inventory inventory) {
        super(i, inventory);
    }

    @Shadow
    protected abstract List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int slot, int cost);

    @Inject(at = @At("HEAD"), method = "clickMenuButton", cancellable = true)
    private void onEnchantedItem(Player player, int id, CallbackInfoReturnable<Boolean> info) {
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.ENCHANTER_PEARL)) {
            if (id >= 0 && id < this.costs.length) {
                int level = this.costs[id];
                ItemStack toEnchant = this.enchantSlots.getItem(0);
                int cost = id + 1;
                if (this.costs[id] > 0 && !toEnchant.isEmpty() && (player.experienceLevel >= cost && player.experienceLevel >= this.costs[id] || player.getAbilities().instabuild)) {
                    this.access.execute((world, pos) -> {
                        float eterna = this.stats.eterna();
                        float quanta = this.stats.quanta();
                        float arcana = this.stats.arcana();
                        boolean stable = this.stats.stable();
                        List<EnchantmentInstance> list = this.getEnchantmentList(toEnchant, id, this.costs[id]);
                        if (!list.isEmpty()) {
                            EnchantmentUtils.chargeExperience(player, MiscUtil.getExpCostForSlot(level, id));
                            player.onEnchantmentPerformed(toEnchant, 0);
                            if (list.getFirst().enchantment.is(Ench.Enchantments.INFUSION)) {
                                InfusionRecipe match = InfusionRecipe.findMatch(world, toEnchant, eterna, quanta, arcana);
                                if (match == null) {
                                    return;
                                }

                                this.enchantSlots.setItem(0, match.assemble(toEnchant, eterna, quanta, arcana));
                            } else {
                                this.enchantSlots.setItem(0, toEnchant.getItem().applyEnchantments(toEnchant, list));
                            }
                            Registry<Enchantment> enchantments = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                            ItemStack doubleEnchanted = EnchantmentHelper.enchantItem(player.getRandom(), toEnchant.copy(), Math.min(this.costs[id] + 7, 40), enchantments.holders().map(holders -> holders));

                            this.enchantSlots.setItem(0, EnigmaticHandler.mergeEnchantments(this.enchantSlots.getItem(0), doubleEnchanted, false, false));

                            player.awardStat(Stats.ENCHANT_ITEM);
                            if (player instanceof ServerPlayer sp) {
                                Ench.Triggers.ENCHANTED.trigger(sp, this.enchantSlots.getItem(0), level, eterna, quanta, arcana, stable);
                                CriteriaTriggers.ENCHANTED_ITEM.trigger(sp, this.enchantSlots.getItem(0), level);
                            }

                            this.enchantSlots.setChanged();
                            this.enchantmentSeed.set(player.getEnchantmentSeed());
                            this.slotsChanged(this.enchantSlots);
                            world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                        }

                    });
                    info.setReturnValue(true);
                } else info.setReturnValue(false);
            } else {
                String var10000 = String.valueOf(player.getName());
                Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
                info.setReturnValue(false);
            }
        }
    }
}
