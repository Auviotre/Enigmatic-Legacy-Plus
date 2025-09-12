package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SoulArchive {
    private static final Type SOUL_RECORDS_TYPE = new TypeToken<List<SoulData>>() {
    }.getType();
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static SoulArchive instance;
    private final File saveFile;
    private final Multimap<ResourceKey<Level>, SoulData> data = HashMultimap.create();

    public SoulArchive(File saveFolder) {
        Preconditions.checkArgument(saveFolder.exists() && saveFolder.isDirectory(), "File " + saveFolder + " does not exist or is not a folder!");
        this.saveFile = new File(saveFolder, "soul_archive.json");
    }

    public static SoulArchive getInstance() {
        return instance;
    }

    public static void initialize(MinecraftServer server) {
        instance = new SoulArchive(server.getWorldPath(LevelResource.ROOT).toFile());
        instance.load();
    }

    public Optional<Tuple<UUID, BlockPos>> findNearest(Player player, BlockPos pos) {
        var data = this.data.get(player.level().dimension()).stream()
                .filter(record -> record.type == 0)
                .filter(record -> Objects.equals(record.ownerID, player.getUUID()))
                .reduce((r1, r2) -> pos.distSqr(r1.pos) > pos.distSqr(r2.pos) ? r2 : r1)
                .orElse(null);

        return data != null ? Optional.of(new Tuple<>(data.id, data.pos)) : Optional.empty();
    }

    public Optional<Tuple<UUID, BlockPos>> findNearest(Level level, BlockPos pos) {
        var data = this.data.get(level.dimension()).stream()
                .filter(record -> record.type == 0)
                .reduce((r1, r2) -> pos.distSqr(r1.pos) > pos.distSqr(r2.pos) ? r2 : r1)
                .orElse(null);

        return data != null ? Optional.of(new Tuple<>(data.id, data.pos)) : Optional.empty();
    }

    public void save() {
        try {
            try (OutputStream out = FileUtils.openOutputStream(this.saveFile, false)) {
                IOUtils.write(this.saveToBytes(), out);
            }
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("FAILED TO SAVE FILE: " + this.saveFile);
            throw new RuntimeException(ex);
        }
    }

    public void load() {
        try {
            if (!this.saveFile.exists() || !this.saveFile.isFile())
                return;

            try (InputStream in = FileUtils.openInputStream(this.saveFile)) {
                byte[] bytes = in.readAllBytes();
                this.loadFromBytes(bytes);
            }
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("FAILED TO LOAD FILE: " + this.saveFile);
            throw new RuntimeException(ex);
        }
    }

    private byte[] saveToBytes() {
        String text = new GsonBuilder().setPrettyPrinting().create().toJson(new ArrayList<>(this.data.values()));
        return text.getBytes(UTF8);
    }

    private void loadFromBytes(byte[] bytes) {
        this.data.clear();

        String text = new String(bytes, UTF8);
        List<SoulData> list = new Gson().fromJson(text, SOUL_RECORDS_TYPE);

        list.forEach(record -> {
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, record.dimension);
            this.data.put(key, record);
        });
    }

    private void synchronize() {
//		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
//			if (SuperpositionHandler.hasCurio(player, EnigmaticItems.SOUL_COMPASS)) {
//				SuperpositionHandler.updateSoulCompass(player);
//			}
//		});
    }

    public void addItem(PermanentItemEntity item) {
        SoulData record = new SoulData(item.level().dimension().location(), item.getUUID(), item.getOwnerId(),
                item.blockPosition(), item.containsSoul() ? 0 : 1);

        if (this.data.put(item.level().dimension(), record)) {
            this.save();
            this.synchronize();
        }
    }

    public void removeItem(UUID id) {
        if (this.data.values().removeIf(record -> record.id.equals(id))) {
            this.save();
            this.synchronize();
        }
    }

    public void removeItem(PermanentItemEntity item) {
        if (this.data.values().removeIf(record -> record.isEqual(item))) {
            this.save();
            this.synchronize();
        }
    }

    private static class SoulData {
        private ResourceLocation dimension;
        private UUID id;
        private UUID ownerID;
        private BlockPos pos;
        private int type;

        SoulData(ResourceLocation dimension, UUID id, UUID ownerID, BlockPos pos, int type) {
            this.dimension = dimension;
            this.id = id;
            this.ownerID = ownerID;
            this.pos = pos;
            this.type = type;
        }

        public int hashCode() {
            return Objects.hash(this.dimension, this.id, this.ownerID, this.pos, this.type);
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            SoulData other = (SoulData) obj;
            return Objects.equals(this.dimension, other.dimension) && Objects.equals(this.id, other.id) && Objects.equals(this.ownerID, other.ownerID) && Objects.equals(this.pos, other.pos) && this.type == other.type;
        }

        public String toString() {
            return "SoulData [dimension=" + this.dimension + ", id=" + this.id + ", ownerID=" + this.ownerID + ", pos=" + this.pos + ", type=" + this.type + "]";
        }

        boolean isEqual(PermanentItemEntity item) {
            return Objects.equals(this.id, item.getUUID()) && Objects.equals(this.ownerID, item.getOwnerId());
        }
    }

    public static class DimensionalPosition {
        public double posX;
        public double posY;
        public double posZ;
        public Level world;

        public DimensionalPosition(double x, double y, double z, Level world) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.world = world;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public Level getWorld() {
            return this.world;
        }
    }
}
