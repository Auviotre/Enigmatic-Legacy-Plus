package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticEnchantments.*;

public class ELEnchantmentTags extends EnchantmentTagsProvider {
    public ELEnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EnchantmentTags.CURSE).add(NEMESIS_CURSE, SORROW_CURSE, ETERNAL_BINDING);
        this.tag(EnchantmentTags.TREASURE).add(NEMESIS_CURSE, SORROW_CURSE, ETERNAL_BINDING);
        this.tag(EnchantmentTags.NON_TREASURE).add(SLAYER, WRATH, CEASELESS, SHARPSHOOTER);
        this.tag(EnchantmentTags.DAMAGE_EXCLUSIVE).add(SLAYER, WRATH);
        this.tag(EnchantmentTags.RIPTIDE_EXCLUSIVE).add(WRATH);
        this.tag(EnchantmentTags.BOW_EXCLUSIVE).add(CEASELESS);

        this.tag(EnigmaticTags.Enchantments.BINDING_CURSE_EXCLUSIVE).add(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE, ETERNAL_BINDING);
    }
}
