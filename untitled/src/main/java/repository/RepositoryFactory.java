package repository;

import domain.Car;
import domain.Rental;
import domain.Client;
import Exceptions.RepositoryExceptions;
import settings.IdGenerator;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class RepositoryFactory {
    private final Properties props;
    private final IdGenerator carIdGenerator;
    private final IdGenerator rentalIdGenerator;
    private final String dbUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static boolean dbInitialized = false;

    public RepositoryFactory(Properties properties, IdGenerator carIdGenerator, IdGenerator rentalIdGenerator) {
        this.props = properties;
        this.carIdGenerator = carIdGenerator;
        this.rentalIdGenerator = rentalIdGenerator;

        // CORECTAT: Eliminat paranteza ')' în plus din URL
        this.dbUrl = props.getProperty("Db_Url", "jdbc:sqlite:rental.db");
    }

    public void initializeDatabase() {
        String repoType = props.getProperty("Repository_Car", "memory").toLowerCase();
        if (repoType.equals("sql")) {
            executeSqlScript("db_init.sql");
        }
    }

    private synchronized void executeSqlScript(String scriptFileName) {
        if (dbInitialized) return;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             InputStream stream = getClass().getClassLoader().getResourceAsStream(scriptFileName)) {

            if (stream == null) {
                throw new RepositoryExceptions("SQL initialization script not found in resources: " + scriptFileName);
            }

            String sqlScript = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            String[] statements = sqlScript.split(";");

            for (String sql : statements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Database initialized successfully from " + scriptFileName);
            dbInitialized = true; // Setează flag-ul după succes

        } catch (SQLException e) {
            throw new RepositoryExceptions("Error executing SQL script: " + e.getMessage());
        } catch (IOException e) {
            throw new RepositoryExceptions("Error reading SQL script file: " + e.getMessage());
        }
    }

    public IRepository<Integer, Client> createClientRepository() {
        String repoType = props.getProperty("Repository_Client", "memory").toLowerCase();
        String fileName = props.getProperty("File_Client", "clients.txt").replace("\"", "");

        IRepository<Integer, Client> clientRepository;

        switch (repoType) {
            case "sql":
                clientRepository = new ClientSQLRepository(props);
                break;
            case "memory": default:
                clientRepository = new InMemoryRepository<>();
                break;
        }

        System.out.println("Client Repository loaded with " + clientRepository.size() + " entries from " + repoType + ".");
        return clientRepository;
    }


    public IRepository<Integer, Car> createCarRepository() {
        String repoType = props.getProperty("Repository_Car", "memory").toLowerCase();
        String fileName = props.getProperty("File_Car", "cars.txt").replace("\"", "");

        // ATENȚIE: Apelul executeSqlScript a fost mutat în initializeDatabase()
        // pentru a evita dubla rulare.

        IRepository<Integer, Car> carRepository;

        switch (repoType) {
            case "text":
                carRepository = new TextFileRepository<>(fileName, Car::fromString);
                break;
            case "binary":
                carRepository = new BinaryFileRepository<>(fileName);
                break;
            case "sql":
                // Instanțierea repo care va rula popularea Faker
                carRepository = new CarSQLRepository(props);
                break;
            case "memory": default:
                carRepository = new InMemoryRepository<>();
                if (carRepository.size() == 0) {
                    addInitialData(carRepository);
                }
                break;
        }

        System.out.println("Car Repository loaded with " + carRepository.size() + " entries from " + repoType + ".");
        return carRepository;
    }



    public IRepository<Integer, Rental> createRentalRepository(IRepository<Integer, Car> carRepository,IRepository<Integer, Client> clientRepository) {
        String repoType = props.getProperty("Repository_Rental", "memory").toLowerCase();
        String fileName = props.getProperty("File_Rental", "rentals.bin").replace("\"", "");

        // IMPLEMENTARE rentalFactory (pentru citirea din fișier text)
        Function<String, Rental> rentalFactory = (line) -> {
            String[] parts = line.split(",");
            // Verifică 5 părți: ID, CarId, ClientId, StartDate, EndDate
            if (parts.length != 5) throw new IllegalArgumentException("Invalid Rental data format: " + line);

            Integer id = Integer.parseInt(parts[0].trim());
            Integer carId = Integer.parseInt(parts[1].trim());
            Integer clientId = Integer.parseInt(parts[2].trim()); // NOU
            LocalDateTime startDate = LocalDateTime.parse(parts[3].trim(), FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(parts[4].trim(), FORMATTER);

            return new Rental(
                    id,
                    carId,
                    clientId,
                    startDate,
                    endDate
            );
        };

        IRepository<Integer, Rental> rentalRepository;

        switch (repoType) {
            case "text": rentalRepository = new TextFileRepository<>(fileName, rentalFactory); break;
            case "binary": rentalRepository = new BinaryFileRepository<>(fileName); break;
            case "sql":
                rentalRepository = new RentalSQLRepository(props,
                        (CarSQLRepository) carRepository,
                        (ClientSQLRepository) clientRepository);
                break;
            case "memory": default:
                rentalRepository = new InMemoryRepository<>();
                break;
        }

        // Adaugare date inițiale hardcodate DOAR pentru Memory/File
        if (!repoType.equals("sql") && rentalRepository.size() == 0) {
            addInitialRentalData(rentalRepository, carRepository);
        }
        System.out.println("Rental Repository loaded with " + rentalRepository.size() + " entries from " + repoType + ".");
        return rentalRepository;
    }


    private void addInitialData(IRepository<Integer, Car> repo) {
        // Logica hardcodată pentru 5 mașini (Memory/File)
        repo.addElem(new Car(100, "Dacia", "Logan"));
        repo.addElem(new Car(101, "BMW", "X5"));
        repo.addElem(new Car(102, "Renault", "Clio"));
        repo.addElem(new Car(103, "Audi", "A4"));
        repo.addElem(new Car(104, "Mercedes", "C-Class"));
        carIdGenerator.setCurrentId(104);
    }

    private void addInitialRentalData(IRepository<Integer, Rental> rentalRepo, IRepository<Integer, Car> carRepo) {
        // Logica hardcodată pentru 5 închirieri (Memory/File)
        try {
            Car m100 = carRepo.getById(100);
            Car m101 = carRepo.getById(101);
            Car m103 = carRepo.getById(103);

            // CORECTAT: Am adăugat ID-ul Clientului hardcodat (presupunem Clientul 1)
            final Integer DUMMY_CLIENT_ID = 1;

            rentalRepo.addElem(new Rental(200, m100.getId(), DUMMY_CLIENT_ID, LocalDateTime.parse("2025-12-01T10:00:00", FORMATTER), LocalDateTime.parse("2025-12-05T10:00:00", FORMATTER)));
            rentalRepo.addElem(new Rental(201, m101.getId(), DUMMY_CLIENT_ID, LocalDateTime.parse("2025-12-06T12:00:00", FORMATTER), LocalDateTime.parse("2025-12-10T12:00:00", FORMATTER)));
            rentalRepo.addElem(new Rental(202, m100.getId(), DUMMY_CLIENT_ID, LocalDateTime.parse("2025-12-11T09:00:00", FORMATTER), LocalDateTime.parse("2025-12-12T15:00:00", FORMATTER)));
            rentalRepo.addElem(new Rental(203, m103.getId(), DUMMY_CLIENT_ID, LocalDateTime.parse("2025-12-15T14:30:00", FORMATTER), LocalDateTime.parse("2025-12-18T14:30:00", FORMATTER)));
            rentalRepo.addElem(new Rental(204, m103.getId(), DUMMY_CLIENT_ID, LocalDateTime.parse("2025-12-20T11:00:00", FORMATTER), LocalDateTime.parse("2025-12-25T11:00:00", FORMATTER)));
            rentalIdGenerator.setCurrentId(204);
        } catch (RepositoryExceptions e) {
            System.err.println("Error initializing rental data: " + e.getMessage());
        }
    }
}