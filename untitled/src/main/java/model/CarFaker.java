package model;

import com.github.javafaker.Faker;
import domain.Car;

import java.util.Random;

public class CarFaker {
    private final Faker faker = new Faker();

    public Car generateRandomCar() {
        String brand = faker.options().option("Audi", "BMW", "Mercedes", "Dacia", "Toyota", "Ford", "Volvo", "Tesla", "Renault");
        String model;

        switch (brand) {
            case "Audi":
                model = faker.options().option("A3", "A4", "Q5", "e-tron") + " " + faker.number().digit();
                break;
            case "BMW":
                model = "Seria " + faker.number().numberBetween(1, 8) + " " + faker.options().option("i", "x");
                break;
            case "Mercedes":
                model = faker.options().option("C-Class", "E-Class", "GLC", "EQS");
                break;
            case "Dacia":
                model = faker.options().option("Logan", "Sandero", "Duster");
                break;
            case "Toyota":
                model = faker.options().option("Corolla", "CHR", "RAV4");
                break;
            default:
                model = faker.commerce().productName() + " " + faker.number().numberBetween(100, 999);
                break;
        }
        return new Car(brand, model);
    }
}