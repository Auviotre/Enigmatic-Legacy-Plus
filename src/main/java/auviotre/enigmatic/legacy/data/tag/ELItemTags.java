package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticItems.*;

public class ELItemTags extends ItemTagsProvider {
    public ELItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, blockTagProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(
                ANIMAL_GUIDEBOOK.get(), HUNTER_GUIDEBOOK.get(),
                ODE_TO_LIVING.get(), SANGUINARY_HANDBOOK.get(),
                ENCHANTMENT_TRANSPOSER.get(), CURSE_TRANSPOSER.get(),
                BLESS_AMPLIFIER.get(), THE_ACKNOWLEDGMENT.get(),
                THE_TWIST.get(), THE_BLESS.get(),
                THE_INFINITUM.get()
        );
        this.tag(ItemTags.LECTERN_BOOKS).add(THE_ACKNOWLEDGMENT.get(), THE_TWIST.get(), THE_BLESS.get(), THE_INFINITUM.get());
        this.tag(ItemTags.VANISHING_ENCHANTABLE).add(THE_ACKNOWLEDGMENT.get(), THE_TWIST.get(), THE_BLESS.get(), THE_INFINITUM.get(), INFERNAL_SHIELD.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(INFERNAL_SHIELD.get(), ETHERIUM_HAMMER.get(), MAJESTIC_ELYTRA.get(), CHAOS_ELYTRA.get());
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(MAJESTIC_ELYTRA.get(), CHAOS_ELYTRA.get());
        this.tag(ItemTags.SWORDS).add(EXECUTION_AXE.get(), ENDER_SLAYER.get(), ETHERIUM_SWORD.get());
        this.tag(ItemTags.HOES).add(ETHERIUM_SCYTHE.get());
        this.tag(ItemTags.BOW_ENCHANTABLE).add(DRAGON_BREATH_BOW.get());
        this.tag(ItemTags.SWORD_ENCHANTABLE).add(THE_INFINITUM.get());
        this.tag(ItemTags.MINING_ENCHANTABLE).add(ETHERIUM_HAMMER.get());
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).add(ETHERIUM_HAMMER.get());
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ETHERIUM_SCYTHE.get(), THE_INFINITUM.get());
        this.tag(ItemTags.WEAPON_ENCHANTABLE).add(ETHERIUM_HAMMER.get());
        this.tag(ItemTags.HEAD_ARMOR).add(ETHERIUM_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR).add(ETHERIUM_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR).add(ETHERIUM_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR).add(ETHERIUM_BOOTS.get());
        this.tag(ItemTags.COMPASSES).add(SOUL_COMPASS.get());
        this.tag(ItemTags.VANISHING_ENCHANTABLE).addTag(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE);

        this.tag(Tags.Items.RAW_MATERIALS).add(RAW_ETHERIUM.get());
        this.tag(Tags.Items.INGOTS).add(ETHERIUM_INGOT.get(), EVIL_INGOT.get());
        this.tag(Tags.Items.NUGGETS).add(ETHERIUM_NUGGET.get());
        this.tag(Tags.Items.TOOLS_SHIELD).add(INFERNAL_SHIELD.get());
        this.tag(Tags.Items.RODS).add(ENDER_ROD.get());
        this.tag(Tags.Items.TOOLS_BOW).add(DRAGON_BREATH_BOW.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ETHERIUM_HAMMER.get());
        this.tag(Tags.Items.ENCHANTABLES).addTag(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE);
        this.tag(Tags.Items.FERTILIZERS).add(INFINIMEAL.get());
        this.tag(Tags.Items.GEMS).add(SACRED_CRYSTAL.get());
        this.tag(Tags.Items.DUSTS).add(INFERNAL_CINDER.get(), ASTRAL_DUST.get());
        this.tag(Tags.Items.POTION_BOTTLE).add(RECALL_POTION.get(), WORMHOLE_POTION.get());
        this.tag(Tags.Items.DRINKS_MAGIC).add(RECALL_POTION.get(), WORMHOLE_POTION.get(), ICHOR_BOTTLE.get(), ENCHANTED_ICHOR_BOTTLE.get(), REDEMPTION_POTION.get(), ICHOR_CURSE_BOTTLE.get());
        this.tag(Tags.Items.FOODS_FRUIT).add(FORBIDDEN_FRUIT.get(), ASTRAL_FRUIT.get(), ENCHANTED_ASTRAL_FRUIT.get());
        this.tag(Tags.Items.FOODS_VEGETABLE).add(ICHOROOT.get());
        this.tag(Tags.Items.FOODS_EDIBLE_WHEN_PLACED).add(EnigmaticBlocks.COSMIC_CAKE.asItem());

        this.tag(EnigmaticTags.Items.ARMOR_CHECK_EXCLUSION).add(Items.ELYTRA, MAJESTIC_ELYTRA.get(), CHAOS_ELYTRA.get());
        this.tag(EnigmaticTags.Items.BYPASS_FOURTH_CURSE).add(THE_TWIST.get(), THE_BLESS.get(), THE_INFINITUM.get());
        this.tag(EnigmaticTags.Items.SPELLSTONES).addTag(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .add(ETHERIUM_CORE.get(), THE_CUBE.get(), CREATION_HEART.get());
        this.tag(EnigmaticTags.Items.THE_CUBE_MATERIAL).add(
                GOLEM_HEART.get(), BLAZING_CORE.get(),
                OCEAN_STONE.get(), ANGEL_BLESSING.get(),
                EYE_OF_NEBULA.get(), VOID_PEARL.get(),
                FORGOTTEN_ICE.get(), REVIVAL_LEAF.get(),
                LOST_ENGINE.get()
        );
        this.tag(EnigmaticTags.Items.SCROLLS).add(
                XP_SCROLL.get(), HEAVEN_SCROLL.get(),
                FABULOUS_SCROLL.get(), NIGHT_SCROLL.get(),
                CURSED_SCROLL.get(), AVARICE_SCROLL.get(),
                CURSED_XP_SCROLL.get(), COSMIC_SCROLL.get()
        );
        this.tag(EnigmaticTags.Items.AMULETS).add(UNWITNESSED_AMULET.get(), ENIGMATIC_AMULET.get(), ASCENSION_AMULET.get(), ELDRITCH_AMULET.get());

        this.tag(EnigmaticTags.Items.ETHERIC_RESONANCE_ENCHANTABLE).add(
                ETHERIUM_HELMET.get(), ETHERIUM_CHESTPLATE.get(),
                ETHERIUM_LEGGINGS.get(), ETHERIUM_BOOTS.get(),
                MAJESTIC_ELYTRA.get(), ETHERIUM_SWORD.get(),
                ETHERIUM_HAMMER.get(), ETHERIUM_SCYTHE.get(),
                ETHEREAL_FORGING_CHARM.get()
        );
        this.tag(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE).add(TOTEM_OF_MALICE.get())
                .addTag(EnigmaticTags.Items.SCROLLS)
                .addTag(CuriosTags.RING)
                .addTag(CuriosTags.CHARM)
                .addOptionalTag(CuriosTags.BACK)
                .addOptionalTag(CuriosTags.BELT)
                .addOptionalTag(CuriosTags.BODY)
                .addOptionalTag(CuriosTags.BRACELET)
                .addOptionalTag(CuriosTags.CURIO)
                .addOptionalTag(CuriosTags.HANDS)
                .addOptionalTag(CuriosTags.HEAD)
                .addOptionalTag(CuriosTags.NECKLACE);

        this.tag(CuriosTags.RING).add(
                IRON_RING.get(), GOLDEN_RING.get(), MINER_RING.get(), MAGNET_RING.get(), DISLOCATION_RING.get(),
                QUARTZ_RING.get(), ENDER_RING.get(), EARTH_PROMISE.get(), CURSED_RING.get(), REDEMPTION_RING.get(), DESOLATION_RING.get()
        );
        this.tag(CuriosTags.CHARM).addTag(EnigmaticTags.Items.AMULETS).add(
                MINING_CHARM.get(), MONSTER_CHARM.get(),
                FORGER_GEM.get(), HELL_BLADE_CHARM.get(),
                ETHEREAL_FORGING_CHARM.get(), BERSERK_EMBLEM.get(),
                ENCHANTER_PEARL.get(), SPELLTUNER.get()
        );
        this.tag(CuriosTags.BACK).add(MAJESTIC_ELYTRA.get(), CHAOS_ELYTRA.get());
    }
}
