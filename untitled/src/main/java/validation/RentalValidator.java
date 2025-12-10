package validation;

import domain.Rental;
import Exceptions.ValidationException;

import java.time.LocalDateTime;

public class RentalValidator implements Validator<Rental> {
    @Override
    public void validate(Rental entity) throws ValidationException {
        String errors = "";

        if (entity.getId() != null && entity.getId() <= 0) {
            errors += "ID-ul inchirierii trebuie sa fie un numar pozitiv.\n";
        }

        if (entity.getCar() == null || entity.getCarId() <= 0) {
            errors += "Obiectul Masina nu poate fi null (se presupune ca este deja validat in Service).\n";
        }

        if (entity.getCar() == null && entity.getId() != null) {
            errors += "Obiectul Mașină (Car) lipsește pentru validările complexe.\n";
        }

        LocalDateTime start = entity.getStartDate();
        LocalDateTime end = entity.getEndDate();

        if (start == null) {
            errors += "Data de inceput nu poate fi nula.\n";
        }

        if (end == null) {
            errors += "Data de sfarsit nu poate fi nula.\n";
        }

        if (start != null && end != null) {
            if (start.isAfter(end) || start.isEqual(end)) {
                errors += "Data de inceput trebuie sa fie strict inainte de data de sfarsit.\n";
            }

            if (start.plusMinutes(30).isAfter(end)) {
                errors += "Durata minima de inchiriere este de 30 de minute.\n";
            }

            if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
                errors += "Data de inceput nu poate fi in trecut.\n";
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}