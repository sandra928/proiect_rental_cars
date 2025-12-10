package repository;

import domain.Identifiable;
import Exceptions.RepositoryExceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {
    protected Map<ID, T> entities = new HashMap<>();

    @Override
    public void addElem(T entity) {
        ID id = entity.getId();
        if (entities.containsKey(id)) {
            throw new RepositoryExceptions("Entity with ID " + id + " already exists.");
        }
        entities.put(id, entity);
    }

    @Override
    public void deleteElem(ID id) {
        if (!entities.containsKey(id)) {
            throw new RepositoryExceptions("Entity with ID " + id + " does not exist.");
        }
        entities.remove(id);
    }

    @Override
    public void updateElem(T entity) {
        ID id = entity.getId();
        if (!entities.containsKey(id)) {
            throw new RepositoryExceptions("Entity with ID " + id + " does not exist and cannot be updated.");
        }
        entities.put(id, entity);
    }

    @Override
    public T getById(ID id) {
        T entity = entities.get(id);
        if (entity == null) {
            throw new RepositoryExceptions("Entity with ID " + id + " not found.");
        }
        return entity;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public int size() {
        return entities.size();
    }
}