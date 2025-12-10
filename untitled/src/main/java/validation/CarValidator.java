package validation;

import domain.Car;
import Exceptions.ValidationException;

public class CarValidator implements Validator<Car> {
    @Override
    public void validate(Car entity) throws ValidationException {
        String errors = "";

        if (entity.getId() != null && entity.getId() <= 0) {
            errors += "ID-ul masinii trebuie sa fie un numar pozitiv.\n";
        }

        if (entity.getBrand() == null || entity.getBrand().trim().isEmpty()) {
            errors += "Marca masinii nu poate fi goala.\n";
        }

        if (entity.getModel() == null || entity.getModel().trim().isEmpty()) {
            errors += "Modelul masinii nu poate fi gol.\n";
        }

        if (entity.getBrand() != null && entity.getBrand().length() < 2) {
            errors += "Marca masinii trebuie sa aiba minim 2 caractere.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}