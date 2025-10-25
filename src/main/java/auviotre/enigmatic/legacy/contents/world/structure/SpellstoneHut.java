package auviotre.enigmatic.legacy.contents.world.structure;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticStructureTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpellstoneHut extends Structure {
    private static final ResourceLocation LOCATION = EnigmaticLegacy.location("spellstone_hut");
    private static final MapCodec<SpellstoneHut> CODEC = simpleCodec(SpellstoneHut::new);
    public static MapCodec<SpellstoneHut> codec() {
        return CODEC;
    }
    public SpellstoneHut(StructureSettings settings) {
        super(settings);
    }

    protected Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), 50, chunkpos.getMinBlockZ());
        StructurePiecesBuilder builder = new StructurePiecesBuilder();
        int i = this.generatePiecesAndAdjust(builder, context, blockpos);
        return Optional.of(new Structure.GenerationStub(blockpos.offset(0, i, 0), Either.right(builder)));
    }

    private int generatePiecesAndAdjust(@NotNull StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        WorldgenRandom random = context.random();
        ChunkGenerator generator = context.chunkGenerator();
        builder.addPiece(new Piece(context.structureTemplateManager(), LOCATION, pos, Rotation.getRandom(random)));
        return builder.moveBelowSeaLevel(generator.getSeaLevel(), generator.getMinY(), random, 10);
    }

    public StructureType<?> type() {
        return EnigmaticStructureTypes.SPELLSTONE_HUT.get();
    }

    public static class Piece extends TemplateStructurePiece {
        public Piece(StructureTemplateManager manager, ResourceLocation location, BlockPos pos, Rotation rotation) {
            super(EnigmaticStructureTypes.SPELLSTONE_HUT_PIECE.get(), 0, manager, location, location.toString(), makeSettings(rotation), pos);
        }

        public Piece(StructurePieceSerializationContext context, CompoundTag tag) {
            super(EnigmaticStructureTypes.SPELLSTONE_HUT_PIECE.get(), tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
        }

        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putString("Rot", this.placeSettings.getRotation().name());
        }

        private static StructurePlaceSettings makeSettings(Rotation rotation) {
            return new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }

        protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
            if ("Table".equals(metadata)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                if (box.isInside(pos.below()) && random.nextFloat() < 0.6F) {
                    level.setBlock(pos.below(), Blocks.ENCHANTING_TABLE.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }
}
