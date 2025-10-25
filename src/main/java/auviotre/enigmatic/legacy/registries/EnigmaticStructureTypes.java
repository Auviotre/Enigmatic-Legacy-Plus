package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.world.structure.SpellstoneHut;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<StructureType<?>, StructureType<SpellstoneHut>> SPELLSTONE_HUT = STRUCTURE_TYPES.register("spellstone_hut", () -> SpellstoneHut::codec);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> SPELLSTONE_HUT_PIECE = STRUCTURE_PIECE_TYPES.register("spellstone_hut", () -> SpellstoneHut.Piece::new);
}