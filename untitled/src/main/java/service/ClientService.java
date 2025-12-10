package service;

import Exceptions.RepositoryExceptions;
import repository.IRepository;
import validation.ClientValidator;
import Exceptions.ValidationException;
import Exceptions.RepositoryExceptions;
import domain.Client;
import java.util.List;

public class ClientService {

    private final IRepository<Integer, Client> clientRepo;
    private final ClientValidator validator;


    public ClientService(IRepository<Integer, Client> clientRepo, ClientValidator validator) {
        this.clientRepo = clientRepo;
        this.validator = validator;
    }

    public void addElem(Client client) {
        validator.validate(client);
        try {
            clientRepo.addElem(client);
        } catch (RepositoryExceptions e) {
            throw new RepositoryExceptions("Eroare la adăugarea clientului: " + e.getMessage());
        }
    }

    public void deleteElem(Integer id) {
        try {
            clientRepo.deleteElem(id);
        } catch (RepositoryExceptions e) {
            throw new RepositoryExceptions("Eroare la ștergerea clientului cu ID " + id + ": " + e.getMessage());
        }
    }

    public void updateElem(Client client) {
        validator.validate(client);
        try {
            clientRepo.updateElem(client);
        } catch (RepositoryExceptions e) {
            throw new RepositoryExceptions("Eroare la actualizarea clientului: " + e.getMessage());
        }
    }

    public Client getById(Integer id) {
        return clientRepo.getById(id);
    }

    public List<Client> getAll() {
        return clientRepo.getAll();
    }
}