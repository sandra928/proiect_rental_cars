package service;

import domain.Car;
import domain.Rental;
import repository.IRepository;
import settings.IdGenerator;
import validation.RentalValidator;
import Exceptions.ValidationException;
import Exceptions.RepositoryExceptions;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RentalService {
    private final IRepository<Integer, Rental> rentalRepository;
    private final IRepository<Integer, Car> carRepository;
    private final RentalValidator validator;
    private final IdGenerator idGenerator;

    public RentalService(IRepository<Integer, Rental> rentalRepository, IRepository<Integer, Car> carRepository, RentalValidator validator, IdGenerator idGenerator) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.validator = validator;
        this.idGenerator = idGenerator;
    }




    public void add(Rental newRental) {
        Car car = carRepository.getById(newRental.getCarId());
        if (car == null) {
            throw new ValidationException("Mașina cu ID-ul " + newRental.getCarId() + " nu există în sistem.");
        }
        newRental.setCar(car);

        validator.validate(newRental);
        try {
            rentalRepository.addElem(newRental);
        } catch (Exception e) {
            throw new RepositoryExceptions("Eroare la salvarea închirierii: " + e.getMessage());
        }
    }

    public int getNextIdValue() {
        return idGenerator.getNextId();
    }

    public Rental getById(Integer id) {
        return rentalRepository.getById(id);
    }

    public List<Rental> getAll() {
        return rentalRepository.getAll();
    }

    public void update(Rental updatedRental) {
        validator.validate(updatedRental);
        for (Rental existing : rentalRepository.getAll()) {
            if (!existing.getId().equals(updatedRental.getId()) && updatedRental.overlapsWith(existing)) {
                throw new ValidationException("Perioada actualizată se suprapune cu o altă închiriere existentă.");
            }
        }
        rentalRepository.updateElem(updatedRental);
    }

    public void delete(Integer id) {
        rentalRepository.deleteElem(id);
    }


    public Map<Car, Long> getMostRentedCars() {
        return rentalRepository.getAll().stream()
                .collect(Collectors.groupingBy(Rental::getCar, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));
    }


    public Map<Car, Long> getCarsRentedLongestTime() {
        return rentalRepository.getAll().stream()
                .collect(Collectors.groupingBy(
                        Rental::getCar,
                        Collectors.summingLong(rental -> ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate()))
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));
    }

    public Map<Integer, Long> getRentalsByMonth() {
        return rentalRepository.getAll().stream()
                .collect(Collectors.groupingBy(
                        rental -> rental.getStartDate().getMonthValue(), // Gruparea se face dupa luna de start
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));
    }

    public Map<Integer, Long> getRentalsByClientId() {
        List<Rental> allRentals = rentalRepository.getAll();

        if (allRentals.isEmpty()) {
            return java.util.Collections.emptyMap();
        }


        return allRentals.stream().collect(Collectors.groupingBy(
                        Rental::getClientId,
                        Collectors.counting()));
    }
}