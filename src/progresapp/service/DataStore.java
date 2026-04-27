package progresapp.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataStore {
    private final Path dataFile;

    public DataStore(Path dataFile) {
        this.dataFile = dataFile;
    }

    public UniversityData load() {
        try {
            if (Files.notExists(dataFile.getParent())) {
                Files.createDirectories(dataFile.getParent());
            }

            if (Files.notExists(dataFile)) {
                return null;
            }

            try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(dataFile))) {
                return (UniversityData) input.readObject();
            }
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
    }

    public void save(UniversityData data) {
        try {
            if (Files.notExists(dataFile.getParent())) {
                Files.createDirectories(dataFile.getParent());
            }

            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(dataFile))) {
                output.writeObject(data);
            }
        } catch (IOException ignored) {
        }
    }

    public static class UniversityData implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}
