package ui;

import Exceptions.RepositoryExceptions;
import Exceptions.ValidationException;
import domain.Car;
import domain.Client;
import domain.Rental;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.CarService;
import service.ClientService;
import service.RentalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUI extends Application {

    private static CarService carService;
    private static RentalService rentalService;
    private static ClientService clientService;

    private static final java.time.format.DateTimeFormatter DATE_FORMATTER =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static void setServices(CarService cService, RentalService rService, ClientService clService) {
        carService = cService;
        rentalService = rService;
        clientService = clService;
    }

    @Override
    public void start(Stage primaryStage) {

        if (carService == null || rentalService == null || clientService == null) {
            System.err.println("Eroare: Serviciile nu au fost injectate corect!");
            primaryStage.close();
            return;
        }

        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();
        root.setCenter(tabPane);

        tabPane.getTabs().add(new Tab("Mașini ", createCarView()));
        tabPane.getTabs().add(new Tab("Clienți ", createClientView()));
        tabPane.getTabs().add(new Tab("Închirieri ", createRentalView()));
        tabPane.getTabs().add(new Tab("Rapoarte ", createReportsView()));

        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        primaryStage.setTitle("🚗 Aplicație Închirieri Mașini");
        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private VBox createCarView() {
        TableView<Car> carTable = new TableView<>();
        carTable.setEditable(true);


        TableColumn<Car, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Car, String> brandColumn = new TableColumn<>("Marca");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        brandColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        brandColumn.setOnEditCommit(event -> {
            Car carToUpdate = event.getRowValue();
            String newBrand = event.getNewValue();
            try {
                carToUpdate.setBrand(newBrand);
                carService.update(carToUpdate);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Marca a fost actualizată cu succes!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Update Marca", ex.getMessage());
                carTable.refresh();
            }
        });

        TableColumn<Car, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        modelColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        modelColumn.setOnEditCommit(event -> {
            Car carToUpdate = event.getRowValue();
            String newModel = event.getNewValue();
            try {
                carToUpdate.setModel(newModel);
                carService.update(carToUpdate);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Modelul a fost actualizat cu succes!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Update Model", ex.getMessage());
                carTable.refresh();
            }
        });



        carTable.getColumns().addAll(idColumn, brandColumn, modelColumn);
        carTable.getItems().setAll(FXCollections.observableArrayList(carService.getAll()));


        TextField brandInput = new TextField();
        brandInput.setPromptText("Marca (ex: Dacia)");
        TextField modelInput = new TextField();
        modelInput.setPromptText("Modelul (ex: Logan)");

        Button addButton = new Button(" + Adaugă Mașină");
        Button deleteButton = new Button(" - Șterge Selecția");



        addButton.setOnAction(e -> {
            try {
                String brand = brandInput.getText();
                String model = modelInput.getText();

                Car newCar = new Car(brand, model);

                carService.add(newCar);
                carTable.getItems().setAll(FXCollections.observableArrayList(carService.getAll()));

                brandInput.clear();
                modelInput.clear();

            } catch (ValidationException | RepositoryExceptions ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Adăugare", ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare Necunoscută", "A apărut o eroare: " + ex.getMessage());
            }
        });


        deleteButton.setOnAction(e -> {
            Car selectedCar = carTable.getSelectionModel().getSelectedItem();
            if (selectedCar != null) {
                try {
                    carService.delete(selectedCar.getId());
                    carTable.getItems().setAll(FXCollections.observableArrayList(carService.getAll()));
                } catch (RepositoryExceptions ex) {
                    showAlert(Alert.AlertType.ERROR, "Eroare la Ștergere", ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Avertisment", "Selectează o mașină de șters.");
            }
        });


        GridPane form = new GridPane();
        form.setVgap(10); form.setHgap(10);
        form.addRow(0, new Label("Marca:"), brandInput);
        form.addRow(1, new Label("Model:"), modelInput);

        HBox buttons = new HBox(10, addButton, deleteButton);
        buttons.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        VBox carLayout = new VBox(10);
        carLayout.setPadding(new javafx.geometry.Insets(10));
        carLayout.getChildren().addAll(carTable, form, buttons);
        return carLayout;
    }


    private VBox createClientView() {
        TableView<Client> clientTable = new TableView<>();
        clientTable.setEditable(true);


        TableColumn<Client, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Client, String> firstNameColumn = new TableColumn<>("Prenume");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        firstNameColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        firstNameColumn.setOnEditCommit(event -> {
            Client clientToUpdate = event.getRowValue();
            String newFirstName = event.getNewValue();
            try {
                clientToUpdate.setFirstName(newFirstName);
                clientService.updateElem(clientToUpdate);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Prenumele a fost actualizat cu succes!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Update Prenume", ex.getMessage());
                clientTable.refresh();
            }
        });

        TableColumn<Client, String> lastNameColumn = new TableColumn<>("Nume");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        lastNameColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        lastNameColumn.setOnEditCommit(event -> {
            Client clientToUpdate = event.getRowValue();
            String newLastName = event.getNewValue();
            try {
                clientToUpdate.setLastName(newLastName);
                clientService.updateElem(clientToUpdate);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Numele a fost actualizat cu succes!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Update Nume", ex.getMessage());
                clientTable.refresh();
            }
        });

        clientTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn);
        clientTable.getItems().setAll(FXCollections.observableArrayList(clientService.getAll()));


        TextField firstNameInput = new TextField();
        firstNameInput.setPromptText("Prenume (ex: Ion)");
        TextField lastNameInput = new TextField();
        lastNameInput.setPromptText("Nume (ex: Popescu)");

        Button addButton = new Button(" + Adaugă Client");
        Button deleteButton = new Button(" - Șterge Selecția");

        addButton.setOnAction(e -> {
            try {
                String firstName = firstNameInput.getText();
                String lastName = lastNameInput.getText();

                Client newClient = new Client(firstName, lastName);
                clientService.addElem(newClient);

                clientTable.getItems().setAll(FXCollections.observableArrayList(clientService.getAll()));

                firstNameInput.clear();
                lastNameInput.clear();

            } catch (ValidationException | RepositoryExceptions ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Adăugare Client", ex.getMessage());
            }
        });

        deleteButton.setOnAction(e -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                try {
                    clientService.deleteElem(selectedClient.getId());
                    clientTable.getItems().setAll(FXCollections.observableArrayList(clientService.getAll()));
                } catch (RepositoryExceptions ex) {
                    showAlert(Alert.AlertType.ERROR, "Eroare la Ștergere Client", ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Avertisment", "Selectează un client de șters.");
            }
        });

        GridPane form = new GridPane();
        form.setVgap(10); form.setHgap(10);
        form.addRow(0, new Label("Prenume:"), firstNameInput);
        form.addRow(1, new Label("Nume:"), lastNameInput);

        HBox buttons = new HBox(10, addButton, deleteButton);
        buttons.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        VBox clientLayout = new VBox(10);
        clientLayout.setPadding(new javafx.geometry.Insets(10));
        clientLayout.getChildren().addAll(clientTable, form, buttons);
        return clientLayout;
    }



    private VBox createRentalView() {
        TableView<Rental> rentalTable = new TableView<>();


        TableColumn<Rental, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));


        TableColumn<Rental, Integer> clientIdColumn = new TableColumn<>("ID Client");
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));


        TableColumn<Rental, Integer> carIdColumn = new TableColumn<>("ID Mașină");
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));


        TableColumn<Rental, LocalDateTime> startColumn = new TableColumn<>("Start");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startColumn.setCellFactory(column -> new TableCell<Rental, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? null : item.format(DATE_FORMATTER)));
            }
        });

        TableColumn<Rental, LocalDateTime> endColumn = new TableColumn<>("Sfârșit");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endColumn.setCellFactory(column -> new TableCell<Rental, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? null : item.format(DATE_FORMATTER)));
            }
        });

        rentalTable.getColumns().addAll(idColumn, clientIdColumn, carIdColumn, startColumn, endColumn);
        rentalTable.getItems().setAll(FXCollections.observableArrayList(rentalService.getAll()));

        TextField idInput = new TextField();
        idInput.setPromptText("ID Închiriere (Auto)");
        idInput.setDisable(true);
        idInput.setVisible(false);

        TextField clientIdInput = new TextField();
        clientIdInput.setPromptText("ID Client");

        TextField carIdInput = new TextField();
        carIdInput.setPromptText("ID Mașină");

        TextField startDateInput = new TextField();
        startDateInput.setPromptText("Data Start (yyyy-MM-dd HH:mm)");

        TextField endDateInput = new TextField();
        endDateInput.setPromptText("Data Sfârșit (yyyy-MM-dd HH:mm)");

        Button addButton = new Button(" + Adaugă Închiriere");
        Button updateButton = new Button(" ~ Actualizează Selecția");
        Button deleteButton = new Button(" - Șterge Selecția");
        Button clearButton = new Button("X: Curăță Formularul");


        rentalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idInput.setText(newSelection.getId().toString());
                clientIdInput.setText(newSelection.getClientId().toString());
                carIdInput.setText(newSelection.getCarId().toString());
                startDateInput.setText(newSelection.getStartDate().format(DATE_FORMATTER));
                endDateInput.setText(newSelection.getEndDate().format(DATE_FORMATTER));
                idInput.setVisible(true);
            } else {
                idInput.setVisible(false);
            }
        });

        clearButton.setOnAction(e -> {
            idInput.clear();
            clientIdInput.clear();
            carIdInput.clear();
            startDateInput.clear();
            endDateInput.clear();
            rentalTable.getSelectionModel().clearSelection();
            idInput.setVisible(false);
        });


        addButton.setOnAction(e -> {
            try {

                if (!idInput.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Avertisment", "Curăță formularul pentru a adăuga o închiriere nouă.");
                    return;
                }

                int clientId = Integer.parseInt(clientIdInput.getText());
                int carId = Integer.parseInt(carIdInput.getText());
                LocalDateTime startDate = LocalDateTime.parse(startDateInput.getText(), DATE_FORMATTER);
                LocalDateTime endDate = LocalDateTime.parse(endDateInput.getText(), DATE_FORMATTER);

                Car carToRent = carService.getById(carId);

                if (carToRent == null) {
                    showAlert(Alert.AlertType.ERROR, "Eroare Validare", "Mașina cu ID-ul " + carId + " nu există.");
                    return;
                }


                Rental newRental = new Rental(carId, clientId, startDate, endDate);
                newRental.setCar(carToRent);

                rentalService.add(newRental);

                rentalTable.getItems().setAll(FXCollections.observableArrayList(rentalService.getAll()));
                clearButton.fire();


            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Intrare", "ID-urile trebuie să fie numere întregi valide.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Adăugare", ex.getMessage());
            }
        });

        updateButton.setOnAction(e -> {
            try {
                if (idInput.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Avertisment", "Selectează o închiriere din tabel sau introdu ID-ul de actualizat.");
                    return;
                }

                Integer id = Integer.parseInt(idInput.getText());
                int clientId = Integer.parseInt(clientIdInput.getText());
                int carId = Integer.parseInt(carIdInput.getText());
                LocalDateTime startDate = LocalDateTime.parse(startDateInput.getText(), DATE_FORMATTER);
                LocalDateTime endDate = LocalDateTime.parse(endDateInput.getText(), DATE_FORMATTER);


                Car carToRent = carService.getById(carId);
                if (carToRent == null) throw new ValidationException("Mașina cu ID-ul " + carId + " nu există.");

                Rental updatedRental = new Rental(id, carId, clientId, startDate, endDate);
                updatedRental.setCar(carToRent);

                rentalService.update(updatedRental);

                rentalTable.getItems().setAll(FXCollections.observableArrayList(rentalService.getAll()));
                clearButton.fire();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Intrare", "ID-urile trebuie să fie numere întregi valide.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare la Actualizare", "Actualizarea a eșuat: " + ex.getMessage());
            }
        });


        deleteButton.setOnAction(e -> {
            Rental selectedRental = rentalTable.getSelectionModel().getSelectedItem();
            if (selectedRental != null) {
                try {
                    rentalService.delete(selectedRental.getId());
                    rentalTable.getItems().setAll(FXCollections.observableArrayList(rentalService.getAll()));
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Eroare la Ștergere", ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Avertisment", "Selectează o închiriere de șters.");
            }
        });


        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);

        form.addRow(0, new Label("ID Client:"), clientIdInput);
        form.addRow(1, new Label("ID Mașină:"), carIdInput);
        form.addRow(2, new Label("Start:"), startDateInput);
        form.addRow(3, new Label("Sfârșit:"), endDateInput);

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton); // ADĂUGARE AICI
        buttons.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        VBox rentalLayout = new VBox(10);
        rentalLayout.setPadding(new javafx.geometry.Insets(10));
        rentalLayout.getChildren().addAll(rentalTable, form, buttons);
        return rentalLayout;
    }



    private VBox createReportsView() {
        HBox mainReportsLayout = new HBox(20);
        mainReportsLayout.setPadding(new javafx.geometry.Insets(15));
        mainReportsLayout.setFillHeight(true);


        mainReportsLayout.getChildren().add(createReportPanel(
                "1. Cele mai des închiriate mașini",
                rentalService.getMostRentedCars(),
                (sb, entry) -> sb.append(entry.getKey().getBrand()).append(" ").append(entry.getKey().getModel())
                        .append(": ").append(entry.getValue()).append(" ori\n")
        ));

        mainReportsLayout.getChildren().add(createReportPanel(
                "2. Mașinile închiriate cel mai mult timp",
                rentalService.getCarsRentedLongestTime(),
                (sb, entry) -> sb.append(entry.getKey().getBrand()).append(" ").append(entry.getKey().getModel())
                        .append(": ").append(entry.getValue()).append(" zile\n")
        ));

        mainReportsLayout.getChildren().add(createReportPanel(
                "3. Numărul de închirieri pe lună",
                rentalService.getRentalsByMonth(),
                (sb, entry) -> sb.append("Luna ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" închirieri\n")
        ));


        mainReportsLayout.getChildren().add(createReportPanel(
                "4. Top 5 Clienți dupa numarul de inchirieri",
                rentalService.getRentalsByClientId().entrySet().stream()
                        .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, java.util.LinkedHashMap::new)),
                (sb, entry) -> {
                    String clientName;
                    try {
                        Client client = clientService.getById(entry.getKey());
                        clientName = client.getFirstName() + " " + client.getLastName();
                    } catch (Exception e) {
                        clientName = "Client Necunoscut (ID " + entry.getKey() + ")";
                    }
                    sb.append(clientName).append(": ").append(entry.getValue()).append(" închirieri\n");
                }
        ));


        return new VBox(new ScrollPane(mainReportsLayout));
    }

    private <K, V> VBox createReportPanel(String title, Map<K, V> data, ReportFormatter<K, V> formatter) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(600);
        reportArea.setPrefWidth(350);

        StringBuilder sb = new StringBuilder();

        data.entrySet().forEach(entry -> formatter.format(sb, entry));

        reportArea.setText(sb.toString());

        return new VBox(10, titleLabel, reportArea);
    }

    @FunctionalInterface
    private interface ReportFormatter<K, V> {
        void format(StringBuilder sb, Map.Entry<K, V> entry);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}