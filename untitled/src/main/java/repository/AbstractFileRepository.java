package repository;

import domain.Identifiable;

public abstract class AbstractFileRepository<ID, E extends Identifiable<ID>> extends InMemoryRepository<ID, E> {
    protected String fileName;

    public AbstractFileRepository(String fileName) {
        this.fileName = fileName;
        readFromFile();
    }

    protected abstract void readFromFile();
    protected abstract void writeToFile();

    @Override
    public void addElem(E entity) {
        super.addElem(entity);
        writeToFile();
    }

    @Override
    public void deleteElem(ID id) {
        super.deleteElem(id);
        writeToFile();
    }

    @Override
    public void updateElem(E entity) {
        super.updateElem(entity);
        writeToFile();
    }
}