package repository;

import domain.Identifiable;
import Exceptions.RepositoryExceptions;
import java.io.*;
import java.util.function.Function;

public class TextFileRepository<ID, E extends Identifiable<ID>> extends AbstractFileRepository<ID, E> {

    private Function<String, E> entityFactory;

    public TextFileRepository(String fileName, Function<String, E> entityFactory) {
        super(fileName);
        this.entityFactory = entityFactory;
    }

    @Override
    protected void readFromFile() {
        entities.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { continue; }
                try {
                    E entity = entityFactory.apply(line);
                    entities.put(entity.getId(), entity);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to parse line in " + fileName + ": " + line + ". Error: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RepositoryExceptions("Error reading file: " + fileName + " - " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (E entity : entities.values()) {
                try {
                    java.lang.reflect.Method toFileStringMethod = entity.getClass().getMethod("toFileString");
                    String line = (String) toFileStringMethod.invoke(entity);
                    bw.write(line);
                    bw.newLine();
                } catch (Exception e) {
                    throw new RepositoryExceptions("Entity " + entity.getClass().getSimpleName() + " must have a public method 'toFileString()' that returns String.");
                }
            }
        } catch (IOException e) {
            throw new RepositoryExceptions("Writing error: " + fileName + " - " + e.getMessage());
        }
    }
}