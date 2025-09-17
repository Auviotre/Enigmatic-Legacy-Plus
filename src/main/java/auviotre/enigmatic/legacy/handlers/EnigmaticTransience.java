package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.ELConfig;
import auviotre.enigmatic.legacy.EnigmaticLegacy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;
import java.io.*;

public class EnigmaticTransience {
    private boolean isCursed, isPermanentlyDead;

    public static EnigmaticTransience read(File directory) {
        if (!directory.exists() || !directory.isDirectory())
            throw new IllegalArgumentException("Directory " + directory + " does not exist or is not a folder!");

        File file = new File(directory, "enigmatic_transience.json");
        if (!file.exists() || !file.isFile()) return new EnigmaticTransience();
        FileReader reader = null;

        try {
            reader = new FileReader(file);
            return new Gson().fromJson(reader, EnigmaticTransience.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JsonSyntaxException ex) {
            EnigmaticLegacy.LOGGER.warn("Failed to read " + file + ", will regenerate...");
            close(reader);

            EnigmaticTransience transience = new EnigmaticTransience();
            transience.write(directory);
            return transience;
        } finally {
            close(reader);
        }
    }

    private static void close(@Nullable Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isCursed() {
        return this.isCursed;
    }

    public void setCursed(boolean isCursed) {
        this.isCursed = isCursed;
    }

    public boolean isPermanentlyDead() {
        return ELConfig.CONFIG.SEVEN_CURSES.maxSoulCrystalLoss.getAsInt() >= 10 && this.isPermanentlyDead;
    }

    public void setPermanentlyDead(boolean isPermanentlyDead) {
        this.isPermanentlyDead = isPermanentlyDead;
    }

    public void write(File directory) {
        if (!directory.exists() || !directory.isDirectory())
            throw new IllegalArgumentException("Directory " + directory + " does not exist or is not a folder!");
        try (FileWriter writer = new FileWriter(new File(directory, "enigmatic_transience.json"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
