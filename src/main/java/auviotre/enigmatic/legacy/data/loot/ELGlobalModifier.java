package auviotre.enigmatic.legacy.data.loot;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.loot.modifiers.AddCurseLootModifier;
import auviotre.enigmatic.legacy.contents.loot.modifiers.SpecialLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticLootTables.Chests;
import static auviotre.enigmatic.legacy.registries.EnigmaticLootTables.Entities;
import static net.minecraft.world.level.storage.loot.BuiltInLootTables.*;

public class ELGlobalModifier extends GlobalLootModifierProvider {
    public ELGlobalModifier(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, EnigmaticLegacy.MODID);
    }

    protected void start() {
        this.addChest(Chests.OVERWORLD_SIMPLE,
                SIMPLE_DUNGEON, ABANDONED_MINESHAFT,
                STRONGHOLD_CORRIDOR, STRONGHOLD_CROSSING,
                DESERT_PYRAMID, JUNGLE_TEMPLE,
                IGLOO_CHEST, WOODLAND_MANSION,
                SHIPWRECK_SUPPLY, PILLAGER_OUTPOST
        );
        this.addChest(Chests.END_SIMPLE, END_CITY_TREASURE);
        this.addChest(Chests.DARKEST_SCROLL, BASTION_TREASURE);
        this.addChest(Chests.GOLEM_HEART, ABANDONED_MINESHAFT, SIMPLE_DUNGEON);
        this.addChest(Chests.BLAZING_CORE, RUINED_PORTAL, NETHER_BRIDGE, BASTION_OTHER, BASTION_BRIDGE);
        this.addChest(Chests.OCEAN_STONE, SHIPWRECK_TREASURE, BURIED_TREASURE, UNDERWATER_RUIN_BIG);
        this.addChest(Chests.ANGEL_BLESSING, DESERT_PYRAMID, JUNGLE_TEMPLE);
        this.addChest(Chests.EYE_OF_NEBULA, STRONGHOLD_CROSSING, STRONGHOLD_CORRIDOR, END_CITY_TREASURE);
        this.addChest(Chests.VOID_PEARL, END_CITY_TREASURE);
        this.addChest(Chests.LOST_ENGINE, STRONGHOLD_CROSSING, STRONGHOLD_CORRIDOR, SIMPLE_DUNGEON);

        this.addCurse(EntityType.BLAZE, Entities.BLAZE);
        this.addCurse(EntityType.CAVE_SPIDER, Entities.SPIDER);
        this.addCurse(EntityType.CREEPER, Entities.CREEPER);
        this.addCurse(EntityType.DROWNED, Entities.DROWNED);
        this.addCurse(EntityType.ELDER_GUARDIAN, Entities.ELDER_GUARDIAN);
        this.addCurse(EntityType.ENDERMAN, Entities.ENDERMAN);
        this.addCurse(EntityType.EVOKER, Entities.EVOKER);
        this.addCurse(EntityType.GHAST, Entities.GHAST);
        this.addCurse(EntityType.GUARDIAN, Entities.GUARDIAN);
        this.addCurse(EntityType.HUSK, Entities.ZOMBIE);
        this.addCurse(EntityType.MAGMA_CUBE, Entities.MAGMA_CUBE);
        this.addCurse(EntityType.PIGLIN, Entities.PIGLIN);
        this.addCurse(EntityType.PIGLIN_BRUTE, Entities.PIGLIN_BRUTE);
        this.addCurse(EntityType.PILLAGER, Entities.ILLAGER);
        this.addCurse(EntityType.RAVAGER, Entities.RAVAGER);
        this.addCurse(EntityType.SHULKER, Entities.SHULKER);
        this.addCurse(EntityType.SKELETON, Entities.SKELETON);
        this.addCurse(EntityType.SPIDER, Entities.SPIDER);
        this.addCurse(EntityType.STRAY, Entities.SKELETON);
        this.addCurse(EntityType.VEX, Entities.VEX);
        this.addCurse(EntityType.VILLAGER, Entities.VILLAGER);
        this.addCurse(EntityType.VINDICATOR, Entities.ILLAGER);
        this.addCurse(EntityType.WITCH, Entities.WITCH);
        this.addCurse(EntityType.WITHER, Entities.WITHER);
        this.addCurse(EntityType.WITHER_SKELETON, Entities.WITHER_SKELETON);
        this.addCurse(EntityType.ZOMBIE, Entities.ZOMBIE);
        this.addCurse(EntityType.ZOMBIFIED_PIGLIN, Entities.ZOMBIFIED_PIGLIN);

        this.add("special_loot_modifier", new SpecialLootModifier(new LootItemCondition[]{}));
    }

    private void addCurse(EntityType<?> entityType, ResourceKey<LootTable> lootTable) {
        this.add("entities/" + EntityType.getKey(entityType).getPath() + "_addon",
                new AddCurseLootModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(entityType.getDefaultLootTable().location()).build()
                }, lootTable)
        );
    }

    @SafeVarargs
    private void addChest(ResourceKey<LootTable> addon, ResourceKey<LootTable>... lootTables) {
        AnyOfCondition.Builder builder = new AnyOfCondition.Builder();
        for (ResourceKey<LootTable> lootTable : lootTables) {
            builder = builder.or(LootTableIdCondition.builder(lootTable.location()));
        }
        this.add(addon.location().getPath(),
                new AddTableLootModifier(new LootItemCondition[]{builder.build()}, addon)
        );
    }
}
