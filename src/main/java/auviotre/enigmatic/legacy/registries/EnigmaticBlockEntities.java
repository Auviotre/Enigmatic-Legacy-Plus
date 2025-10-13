package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.block.entity.DimensionalAnchorEntity;
import auviotre.enigmatic.legacy.contents.block.entity.SpellstoneTableEntity;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EnigmaticLegacy.MODID);

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, DeferredBlock<?> block) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(Util.fetchChoiceType(References.BLOCK_ENTITY, EnigmaticLegacy.MODID + ":" + name)));
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DimensionalAnchorEntity>> DIMENSIONAL_ANCHOR_ENTITY = register("dimensional_anchor_entity", DimensionalAnchorEntity::new, EnigmaticBlocks.DIMENSIONAL_ANCHOR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpellstoneTableEntity>> SPELLSTONE_TABLE_ENTITY = register("spellstone_table_entity", SpellstoneTableEntity::new, EnigmaticBlocks.SPELLSTONE_TABLE);
}
