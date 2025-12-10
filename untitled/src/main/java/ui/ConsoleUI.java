package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import service.CarService;
import service.RentalService;
import service.ClientService;
import domain.Car;
import domain.Client;
import domain.Rental;

public class ConsoleUI {
    private final CarService carService;
    private final RentalService rentalService;
    private final ClientService clientService;
    private final Scanner scanner = new Scanner(System.in);


    public ConsoleUI(CarService carService, RentalService rentalService, ClientService clientService) {
        this.carService = carService;
        this.rentalService = rentalService;
        this.clientService = clientService;
    }

    public void run() {
        System.out.println("---  Închirieri Mașini  ---");
        boolean running = true;

        while (running) {
            System.out.println("\n--- MENIU PRINCIPAL ---");
            System.out.println("1. Mașini");
            System.out.println("2. Clienți");
            System.out.println("3. Închirieri");
            System.out.println("0. Ieșire");
            System.out.print(">");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        handleCarMenu();
                        break;
                    case "2":
                        handleClientMenu();
                        break;
                    case "3":
                        handleRentalMenu();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Aplicația se închide.");
                        break;
                    default:
                        System.out.println("Opțiune invalidă.");
                }
            } catch (Exception e) {
                System.err.println("Eroare neașteptată: " + e.getMessage());
            }
        }
        scanner.close();
    }


    private void handleCarMenu() {
        System.out.println("\n--- Meniu Mașini ---");
        System.out.println("1. Afișează toate mașinile (" + carService.getAll().size() + ")");
        System.out.println("2. Adaugă mașină");
        System.out.println("3. Șterge mașină după ID");
        System.out.println("4. Actualizează mașină");
        System.out.print(">");
        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1": carService.getAll().forEach(System.out::println); break;
                case "2": addCarUI(); break;
                case "3": deleteCarUI(); break;
                case "4": updateCarUI(); break;
                default: System.out.println("Opțiune invalidă.");
            }
        } catch (Exception e) {
            System.err.println("Eroare Mașini: " + e.getMessage());
        }
    }

    private void handleClientMenu() {
        System.out.println("\n--- Meniu Clienți ---");
        System.out.println("1. Afișează toți clienții (" + clientService.getAll().size() + ")");
        System.out.println("2. Adaugă client");
        System.out.println("3. Șterge client după ID");
        System.out.println("4. Actualizează client");
        System.out.print(">");
        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1": clientService.getAll().forEach(System.out::println); break;
                case "2": addClientUI(); break;
                case "3": deleteClientUI(); break;
                case "4": updateClientUI(); break;
                default: System.out.println("Opțiune invalidă.");
            }
        } catch (Exception e) {
            System.err.println("Eroare Clienți: " + e.getMessage());
        }
    }

    private void handleRentalMenu() {
        System.out.println("\n--- Meniu Închirieri ---");
        System.out.println("1. Afișează toate închirierile (" + rentalService.getAll().size() + ")");
        System.out.println("2. Adaugă închiriere");
        System.out.println("3. Șterge închiriere după ID");
        System.out.println("4. Actualizează închiriere");
        System.out.print(">");
        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1": rentalService.getAll().forEach(System.out::println); break;
                case "2": addRentalUI(); break;
                case "3": deleteRentalUI(); break;
                case "4": updateRentalUI(); break;
                default: System.out.println("Opțiune invalidă.");
            }
        } catch (Exception e) {
            System.err.println("Eroare Închirieri: " + e.getMessage());
        }
    }




    private void addCarUI() {
        System.out.print("Brand (Marca): ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();

        try {
            Car newCar = new Car(brand, model);
            carService.add(newCar);
            System.out.println("Mașina adăugată cu succes: ID " + newCar.getId());
        } catch (Exception e) {
            System.err.println("Adăugare Mașină eșuată: " + e.getMessage());
        }
    }

    private void updateCarUI() {
        System.out.print("Introduceți ID-ul mașinii de actualizat: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Car existingCar = carService.getById(id);

            if (existingCar == null) {
                System.err.println("Mașina cu ID-ul " + id + " nu există.");
                return;
            }

            System.out.println("Actualizare Mașină ID " + id + " (Curent: " + existingCar.getBrand() + " " + existingCar.getModel() + ")");

            System.out.print("Noua Marcă (Lăsați gol pentru a păstra '" + existingCar.getBrand() + "'): ");
            String newBrand = scanner.nextLine();

            System.out.print("Noul Model (Lăsați gol pentru a păstra '" + existingCar.getModel() + "'): ");
            String newModel = scanner.nextLine();

            if (!newBrand.trim().isEmpty()) {
                existingCar.setBrand(newBrand);
            }
            if (!newModel.trim().isEmpty()) {
                existingCar.setModel(newModel);
            }

            carService.update(existingCar);
            System.out.println("Mașina actualizată cu succes.");

        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți un număr întreg.");
        } catch (Exception e) {
            System.err.println("Actualizare eșuată: " + e.getMessage());
        }
    }

    private void deleteCarUI() {
        System.out.print("Introduceți ID-ul mașinii de șters: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            carService.delete(id);
            System.out.println("Mașina cu ID-ul " + id + " a fost ștearsă.");
        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți un număr întreg.");
        } catch (Exception e) {
            System.err.println("Ștergere eșuată: " + e.getMessage());
        }
    }



    private void addClientUI() {
        System.out.print("Prenume: ");
        String firstName = scanner.nextLine();
        System.out.print("Nume: ");
        String lastName = scanner.nextLine();

        try {
            Client newClient = new Client(firstName, lastName);
            clientService.addElem(newClient);
            System.out.println("Client adăugat cu succes: ID " + newClient.getId());
        } catch (Exception e) {
            System.err.println("Adăugare Client eșuată: " + e.getMessage());
        }
    }

    private void updateClientUI() {
        System.out.print("Introduceți ID-ul clientului de actualizat: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Client existingClient = clientService.getById(id);

            if (existingClient == null) {
                System.err.println("Clientul cu ID-ul " + id + " nu există.");
                return;
            }

            System.out.println("Actualizare Client ID " + id + " (Curent: " + existingClient.getFirstName() + " " + existingClient.getLastName() + ")");

            System.out.print("Noul Prenume (Lăsați gol pentru a păstra '" + existingClient.getFirstName() + "'): ");
            String newFirstName = scanner.nextLine();

            System.out.print("Noul Nume (Lăsați gol pentru a păstra '" + existingClient.getLastName() + "'): ");
            String newLastName = scanner.nextLine();

            if (!newFirstName.trim().isEmpty()) {
                existingClient.setFirstName(newFirstName);
            }
            if (!newLastName.trim().isEmpty()) {
                existingClient.setLastName(newLastName);
            }

            clientService.updateElem(existingClient);
            System.out.println("Client actualizat cu succes.");

        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți un număr întreg.");
        } catch (Exception e) {
            System.err.println("Actualizare eșuată: " + e.getMessage());
        }
    }

    private void deleteClientUI() {
        System.out.print("Introduceți ID-ul clientului de șters: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            clientService.deleteElem(id);
            System.out.println("Clientul cu ID-ul " + id + " a fost șters.");
        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți un număr întreg.");
        } catch (Exception e) {
            System.err.println("Ștergere eșuată: " + e.getMessage());
        }
    }


    private void addRentalUI() {
        System.out.println("--- Adaugă Închiriere ---");
        clientService.getAll().forEach(c -> System.out.println("Client: " + c.getId() + " " + c.getFirstName()));
        System.out.print("ID Client: ");
        String clientIdStr = scanner.nextLine();

        carService.getAll().forEach(c -> System.out.println("Mașină: " + c.getId() + " " + c.getBrand() + " " + c.getModel()));
        System.out.print("ID Mașină: ");
        String carIdStr = scanner.nextLine();

        System.out.println("Introduceți data/ora Început (ex: 2025-12-09T10:00:00):");
        String startStr = scanner.nextLine();
        System.out.println("Introduceți data/ora Sfârșit (ex: 2025-12-10T10:00:00):");
        String endStr = scanner.nextLine();

        try {
            Integer clientId = Integer.parseInt(clientIdStr);
            Integer carId = Integer.parseInt(carIdStr);
            LocalDateTime startDate = LocalDateTime.parse(startStr);
            LocalDateTime endDate = LocalDateTime.parse(endStr);

            Rental newRental = new Rental(carId, clientId, startDate, endDate);
            rentalService.add(newRental); // Folosim addElem
            System.out.println("Închiriere adăugată cu succes: ID " + newRental.getId());

        } catch (NumberFormatException e) {
            System.err.println("Eroare: ID-urile sau durata trebuie să fie numere.");
        } catch (DateTimeParseException e) {
            System.err.println("Eroare de format dată/ora. Folosiți formatul ISO (YYYY-MM-DDTHH:MM:SS).");
        } catch (Exception e) {
            System.err.println("Adăugare Închiriere eșuată: " + e.getMessage());
        }
    }

    private void updateRentalUI() {
        System.out.print("Introduceți ID-ul închirierii de actualizat: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Rental existingRental = rentalService.getById(id);

            if (existingRental == null) {
                System.err.println("Închirierea cu ID-ul " + id + " nu există.");
                return;
            }

            System.out.println("Actualizare Închiriere ID " + id);


            System.out.println("   ID Client curent: " + existingRental.getClientId());
            System.out.println("   ID Mașină curent: " + existingRental.getCarId());
            System.out.println("   Data Start curentă: " + existingRental.getStartDate());
            System.out.println("   Data Sfârșit curentă: " + existingRental.getEndDate());


            System.out.print("Noul ID Client (Lăsați gol pentru a păstra): ");
            String newClientIdStr = scanner.nextLine();
            if (!newClientIdStr.trim().isEmpty()) {
                Integer newClientId = Integer.parseInt(newClientIdStr);
                existingRental.setClientId(newClientId);
            }


            System.out.print("Noul ID Mașină (Lăsați gol pentru a păstra): ");
            String newCarIdStr = scanner.nextLine();
            if (!newCarIdStr.trim().isEmpty()) {
                Integer newCarId = Integer.parseInt(newCarIdStr);
                existingRental.setCarId(newCarId);
            }


            System.out.print("Noua Dată Start (Lăsați gol pentru a păstra): ");
            String newStartStr = scanner.nextLine();
            if (!newStartStr.trim().isEmpty()) {
                LocalDateTime newStartDate = LocalDateTime.parse(newStartStr);
                existingRental.setStartDate(newStartDate);
            }


            System.out.print("Noua Dată Sfârșit (Lăsați gol pentru a păstra): ");
            String newEndStr = scanner.nextLine();
            if (!newEndStr.trim().isEmpty()) {
                LocalDateTime newEndDate = LocalDateTime.parse(newEndStr);
                existingRental.setEndDate(newEndDate);
            }

            rentalService.update(existingRental);
            System.out.println("Închiriere actualizată cu succes.");

        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți numere întregi valide.");
        } catch (DateTimeParseException e) {
            System.err.println("Eroare de format dată/ora. Folosiți formatul ISO (YYYY-MM-DDTHH:MM:SS).");
        } catch (Exception e) {
            System.err.println("Actualizare închiriere eșuată: " + e.getMessage());
        }
    }


    private void deleteRentalUI() {
        System.out.print("Introduceți ID-ul închirierii de șters: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            rentalService.delete(id);
            System.out.println("Închirierea cu ID-ul " + id + " a fost ștearsă.");
        } catch (NumberFormatException e) {
            System.err.println("ID invalid. Introduceți un număr întreg.");
        } catch (Exception e) {
            System.err.println("Ștergere eșuată: " + e.getMessage());
        }
    }
}