package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes.DARKNESS;
import static auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes.NEMESIS_CURSE;

public class ELDamageTypeTags extends DamageTypeTagsProvider {
    public ELDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(NEMESIS_CURSE, DARKNESS);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(DARKNESS);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(DARKNESS);
        this.tag(DamageTypeTags.WITHER_IMMUNE_TO).add(DARKNESS);

        this.tag(EnigmaticTags.DamageTypes.GOLEM_HEART_IMMUNE_TO).add(
                DamageTypes.CACTUS,
                DamageTypes.CRAMMING,
                DamageTypes.IN_WALL,
                DamageTypes.FALLING_BLOCK,
                DamageTypes.SWEET_BERRY_BUSH
        );
        this.tag(EnigmaticTags.DamageTypes.GOLEM_HEART_IS_MELEE).add(
                DamageTypes.GENERIC,
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK
        );
        this.tag(EnigmaticTags.DamageTypes.ANGEL_BLESSING_IMMUNE_TO).addTag(DamageTypeTags.IS_FALL).add(
                DamageTypes.FLY_INTO_WALL
        );
        this.tag(EnigmaticTags.DamageTypes.ANGEL_BLESSING_VULNERABLE_TO).add(
                DamageTypes.WITHER,
                DamageTypes.FELL_OUT_OF_WORLD
        );
        this.tag(EnigmaticTags.DamageTypes.LOST_ENGINE_IMMUNE_TO).addTag(DamageTypeTags.IS_EXPLOSION).addTag(DamageTypeTags.IS_FALL).add(
                DamageTypes.CACTUS,
                DamageTypes.SWEET_BERRY_BUSH
        );
    }
}
