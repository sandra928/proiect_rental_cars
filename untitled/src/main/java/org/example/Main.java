package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import repository.IRepository;
import repository.RepositoryFactory;
import service.CarService;
import service.ClientService;
import service.RentalService;
import validation.CarValidator;
import validation.ClientValidator;
import validation.RentalValidator;
import ui.ConsoleUI;
import ui.GUI;
import domain.Car;
import domain.Client;
import domain.Rental;
import settings.IdGenerator;
import Exceptions.RepositoryExceptions;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();


        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("settings.properties")) {
            if (input == null) {
                System.err.println("FATAL: settings.properties not found. Using default settings.");
            } else {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load settings.properties: " + e.getMessage());
        }


        props.putIfAbsent("Repository_Car", "memory");
        props.putIfAbsent("Repository_Rental", "memory");
        props.putIfAbsent("Repository_Client", "memory");
        props.putIfAbsent("Start_Mode", "console");
        props.putIfAbsent("Db_Url", "jdbc:sqlite:rental.db");



        IdGenerator carIdGenerator = new IdGenerator("car_id_gen.properties");
        IdGenerator rentalIdGenerator = new IdGenerator("rental_id_gen.properties");


        RepositoryFactory factory = new RepositoryFactory(props, carIdGenerator, rentalIdGenerator);
        factory.initializeDatabase();


        IRepository<Integer, Client> clientRepo = factory.createClientRepository();
        IRepository<Integer, Car> carRepo = factory.createCarRepository();
        IRepository<Integer, Rental> rentalRepo = factory.createRentalRepository(carRepo, clientRepo);

        System.out.println("Repositories initialized.");


        CarValidator carValidator = new CarValidator();
        RentalValidator rentalValidator = new RentalValidator();
        ClientValidator clientValidator = new ClientValidator();

        CarService carService = new CarService(carRepo, carValidator, carIdGenerator);
        RentalService rentalService = new RentalService(rentalRepo, carRepo, rentalValidator, rentalIdGenerator);
        ClientService clientService = new ClientService(clientRepo, clientValidator); // NOU


        String startMode = props.getProperty("Start_Mode").toLowerCase();

        if (startMode.equals("gui")) {
            System.out.println("Starting GUI mode (JavaFX)...");
            GUI.setServices(carService, rentalService, clientService);
            javafx.application.Application.launch(GUI.class, args);
        } else {
            System.out.println("Starting Console mode...");
            ConsoleUI console = new ConsoleUI(carService, rentalService, clientService);
            console.run();
        }
    }
}