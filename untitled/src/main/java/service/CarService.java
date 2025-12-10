package service;

import domain.Car;
import repository.IRepository;
import settings.IdGenerator;
import validation.CarValidator;

import java.util.List;

public class CarService {
    private final IRepository<Integer, Car> carRepository;
    private final CarValidator validator;
    private final IdGenerator idGenerator;

    public CarService(IRepository<Integer, Car> carRepository, CarValidator validator, IdGenerator idGenerator) {
        this.carRepository = carRepository;
        this.validator = validator;
        this.idGenerator = idGenerator;
    }


    public void add(Car car) {
        validator.validate(car);
        carRepository.addElem(car);
    }


    public Car getById(Integer id) {
        return carRepository.getById(id);
    }


    public List<Car> getAll() {
        return carRepository.getAll();
    }


    public void update(Car car) {
        validator.validate(car);
        carRepository.updateElem(car);
    }


    public void delete(Integer id) {
        carRepository.deleteElem(id);
    }

    public int getNextIdValue() {
        return idGenerator.getNextId();
    }
}