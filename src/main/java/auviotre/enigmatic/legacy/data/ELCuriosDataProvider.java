package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class ELCuriosDataProvider extends CuriosDataProvider {
    public ELCuriosDataProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> provider) {
        super(EnigmaticLegacy.MODID, output, fileHelper, provider);
    }

    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createEntities("init").addEntities(EntityType.PLAYER).addSlots(
                "charm", "ring", "back", "scroll", "spellstone"
        );
        this.createSlot("back").size(1);
        this.createSlot("ring").size(1);
        this.createSlot("charm").size(2);
        this.createSlot("scroll").size(0).icon(EnigmaticLegacy.location("slot/empty_scroll_slot"));
        this.createSlot("spellstone").size(0).icon(EnigmaticLegacy.location("slot/empty_spellstone_slot"));
    }
}
