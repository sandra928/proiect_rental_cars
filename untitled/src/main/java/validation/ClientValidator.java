package validation;

import Exceptions.ValidationException;
import domain.Client;

public class ClientValidator implements Validator<Client> {

    @Override
    public void validate(Client client) {
        String errors = "";

        if (client.getId() != null && client.getId() <= 0) {
            errors += "ID-ul clientului, dacă este setat, trebuie să fie un număr pozitiv.\n";
        }

        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            errors += "Prenumele clientului nu poate fi gol.\n";
        }

        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            errors += "Numele de familie al clientului nu poate fi gol.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}