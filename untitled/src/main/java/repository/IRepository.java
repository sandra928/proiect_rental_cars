package repository;

import domain.Identifiable;
import java.util.List;

public interface IRepository<ID, T extends Identifiable<ID>> {
    void addElem(T entity);
    void deleteElem(ID id);
    void updateElem(T entity);
    T getById(ID id);
    List<T> getAll();
    int size();
}