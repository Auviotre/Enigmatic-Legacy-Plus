package auviotre.enigmatic.legacy.data;


import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.data.loot.ELBlockLoot;
import auviotre.enigmatic.legacy.data.loot.ELChestLoot;
import auviotre.enigmatic.legacy.data.loot.ELEntityLoot;
import auviotre.enigmatic.legacy.data.loot.ELGlobalModifier;
import auviotre.enigmatic.legacy.data.tag.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class ELDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new RegistryDataGenerator(output, lookupProvider));
        ELBlockTags blockTags = new ELBlockTags(output, lookupProvider, helper);
        generator.addProvider(includeServer, new ELEntityTypeTags(output, lookupProvider, helper));
        generator.addProvider(includeServer, blockTags);
        generator.addProvider(includeServer, new ELItemTags(output, lookupProvider, blockTags.contentsGetter(), helper));
        generator.addProvider(includeServer, new ELEnchantmentTags(output, lookupProvider, helper));
        generator.addProvider(includeServer, new ELDamageTypeTags(output, lookupProvider, helper));
        generator.addProvider(includeServer, new ELEffectTags(output, lookupProvider, helper));
        generator.addProvider(includeServer, new ELRecipeProvider(output, lookupProvider));
        generator.addProvider(includeServer, new ELAdvancementProvider(output, lookupProvider, helper));
        generator.addProvider(includeServer, new LootTableProvider(output, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(ELEntityLoot::new, LootContextParamSets.ENTITY),
                new LootTableProvider.SubProviderEntry(ELBlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ELChestLoot::new, LootContextParamSets.CHEST)
        ), lookupProvider));

        generator.addProvider(includeServer, new ELGlobalModifier(output, lookupProvider));
        generator.addProvider(includeServer, new ELCuriosDataProvider(output, helper, lookupProvider));
    }
}
