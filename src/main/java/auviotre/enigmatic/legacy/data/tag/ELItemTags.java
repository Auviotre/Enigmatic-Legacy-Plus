package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
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
                ANIMAL_GUIDEBOOK.get(),
                HUNTER_GUIDEBOOK.get(),
                ODE_TO_LIVING.get(),
                ENCHANTMENT_TRANSPOSER.get(),
                CURSE_TRANSPOSER.get(),
                THE_ACKNOWLEDGMENT.get(),
                THE_TWIST.get(),
                THE_INFINITUM.get()
        );
        this.tag(ItemTags.LECTERN_BOOKS).add(THE_ACKNOWLEDGMENT.get(), THE_TWIST.get(), THE_INFINITUM.get());
        this.tag(ItemTags.VANISHING_ENCHANTABLE).add(THE_ACKNOWLEDGMENT.get(), THE_TWIST.get(), THE_INFINITUM.get(), INFERNAL_SHIELD.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(INFERNAL_SHIELD.get(), MAJESTIC_ELYTRA.get());
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(MAJESTIC_ELYTRA.get());
        this.tag(ItemTags.SWORDS).add(EXECUTION_AXE.get(), ENDER_SLAYER.get());
        this.tag(ItemTags.SWORD_ENCHANTABLE).add(THE_INFINITUM.get());
        this.tag(Tags.Items.RAW_MATERIALS).add(RAW_ETHERIUM.get());
        this.tag(Tags.Items.INGOTS).add(ETHERIUM_INGOT.get());
        this.tag(Tags.Items.NUGGETS).add(ETHERIUM_NUGGET.get());
        this.tag(Tags.Items.TOOLS_SHIELD).add(INFERNAL_SHIELD.get());
        this.tag(Tags.Items.RODS).add(ENDER_ROD.get());

        this.tag(EnigmaticTags.Items.ARMOR_CHECK_EXCLUSION).add(Items.ELYTRA, MAJESTIC_ELYTRA.get());
        this.tag(EnigmaticTags.Items.BYPASS_FOURTH_CURSE).add(
                THE_TWIST.get(),
                THE_INFINITUM.get()
        );
        this.tag(EnigmaticTags.Items.SPELLSTONES).add(
                GOLEM_HEART.get(), BLAZING_CORE.get(),
                OCEAN_STONE.get(), ANGEL_BLESSING.get(),
                EYE_OF_NEBULA.get(), VOID_PEARL.get(),
                LOST_ENGINE.get()
        );
        this.tag(EnigmaticTags.Items.SCROLLS).add(
                XP_SCROLL.get(),
                HEAVEN_SCROLL.get(),
                FABULOUS_SCROLL.get(),
                CURSED_SCROLL.get(),
                AVARICE_SCROLL.get()
        );

        this.tag(Tags.Items.ENCHANTABLES).addTag(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE);
        this.tag(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE)
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
                IRON_RING.get(), GOLDEN_RING.get(), MAGNET_RING.get(), DISLOCATION_RING.get(),
                QUARTZ_RING.get(), ENDER_RING.get(), CURSED_RING.get()
        );
        this.tag(CuriosTags.CHARM).add(
                UNWITNESSED_AMULET.get(),
                ENIGMATIC_AMULET.get(),
                ASCENSION_AMULET.get(),
                MINING_CHARM.get(),
                MONSTER_CHARM.get(),
                BERSERK_EMBLEM.get(),
                ENCHANTER_PEARL.get(),
                ELDRITCH_AMULET.get()
        );
        this.tag(CuriosTags.BACK).add(MAJESTIC_ELYTRA.get());
    }
}
