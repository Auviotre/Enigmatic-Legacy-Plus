package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticItems.ASTRAL_FRUIT;
import static auviotre.enigmatic.legacy.registries.EnigmaticItems.INFERNAL_CINDER;

public class ELDataMaps extends DataMapProvider {
    protected ELDataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void gather(HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(IItemHelper.getLocation(ASTRAL_FRUIT), new Compostable(1.0F), false);

        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(IItemHelper.getLocation(INFERNAL_CINDER), new FurnaceFuel(2000), false);
    }
}
