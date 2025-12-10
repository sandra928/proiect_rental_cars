package settings;

import Exceptions.RepositoryExceptions;

import java.io.*;
import java.util.Properties;

public class IdGenerator {
    private static final int INITIAL_ID = 100;
    private final String generatorFileName;
    private int currentId;

    public IdGenerator(String generatorFileName) {
        this.generatorFileName = generatorFileName;
        loadId();
    }

    private void loadId() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(generatorFileName)) {
            props.load(input);
            currentId = Integer.parseInt(props.getProperty("last_id", String.valueOf(INITIAL_ID - 1)));
        } catch (FileNotFoundException e) {
            currentId = INITIAL_ID - 1;
        } catch (IOException e) {
            throw new RepositoryExceptions("Error reading ID generator file: " + e.getMessage());
        }
    }

    private void saveId() {
        Properties props = new Properties();
        props.setProperty("last_id", String.valueOf(currentId));
        try (OutputStream output = new FileOutputStream(generatorFileName)) {
            props.store(output, "Last generated ID");
        } catch (IOException e) {
            System.err.println("Warning: Could not save ID generator state: " + e.getMessage());
        }
    }

    public int getNextId() {
        currentId++;
        saveId();
        return currentId;
    }

    public void setCurrentId(int newId) {
        if (newId > currentId) {
            currentId = newId;
            saveId();
        }
    }
}