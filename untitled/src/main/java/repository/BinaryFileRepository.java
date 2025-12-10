package repository;

import domain.Identifiable;
import Exceptions.RepositoryExceptions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinaryFileRepository<ID, E extends Identifiable<ID> & Serializable> extends AbstractFileRepository<ID, E> {

    @SuppressWarnings("unchecked")
    public BinaryFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    protected void readFromFile() {
        entities.clear();
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) { return; }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<E> entitiesList = (List<E>) ois.readObject();
            for (E entity : entitiesList) {
                entities.put(entity.getId(), entity);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryExceptions("Reading error (binary file): " + fileName + " - " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(new ArrayList<>(entities.values()));
        } catch (IOException e) {
            throw new RepositoryExceptions("Writing error (binary file): " + fileName + " - " + e.getMessage());
        }
    }
}