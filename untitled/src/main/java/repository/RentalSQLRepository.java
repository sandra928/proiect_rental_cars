package repository;

import Exceptions.RepositoryExceptions;
import domain.Rental;
import domain.Car;
import model.RentalFaker;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RentalSQLRepository extends SQLRepository<Integer, Rental> {

    private final CarSQLRepository carRepo;
    private final ClientSQLRepository clientRepo;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public RentalSQLRepository(Properties props, CarSQLRepository carRepo, ClientSQLRepository clientRepo) {
        super(props, "rentals");
        this.carRepo = carRepo;
        this.clientRepo = clientRepo;
        populateIfEmpty();
    }

    @Override
    protected Rental extractEntity(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer carId = rs.getInt("car_id");
        Integer clientId = rs.getInt("client_id");
        LocalDateTime startDate = LocalDateTime.parse(rs.getString("start_date"), FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(rs.getString("end_date"), FORMATTER);

        Rental rental = new Rental(
                id,
                carId,
                clientId,
                startDate,
                endDate
        );

        Car car = carRepo.getById(carId);

        if (car == null) {
            System.err.println("Avertisment: Mașina cu ID-ul " + carId + " pentru închirierea " + id + " nu a fost găsită.");
        } else {
            rental.setCar(car);
        }

        return rental;
    }

    @Override
    protected void setStatementParameters(PreparedStatement ps, Rental rental) throws SQLException {
        ps.setInt(1, rental.getCarId());
        ps.setInt(2, rental.getClientId());
        ps.setString(3, rental.getStartDate().format(FORMATTER));
        ps.setString(4, rental.getEndDate().format(FORMATTER));
    }

    @Override
    protected void setUpdateStatementParameters(PreparedStatement ps, Rental rental) throws SQLException {
        setStatementParameters(ps, rental);
        ps.setInt(5, rental.getId());
    }

    @Override
    public void addElem(Rental rental) {
        String sql = "INSERT INTO rentals (car_id, client_id, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setStatementParameters(ps, rental);
            ps.executeUpdate();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    rental.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la adăugarea închirierii: " + e.getMessage());
        }
    }

    @Override
    public void updateElem(Rental rental) {
        String sql = "UPDATE rentals SET car_id=?, client_id=?, start_date=?, end_date=? WHERE id=?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setUpdateStatementParameters(ps, rental);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la actualizarea închirierii: " + e.getMessage());
        }
    }

    @Override
    public void deleteElem(Integer id) {
        String sql = "DELETE FROM rentals WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la ștergerea închirierii: " + e.getMessage());
        }
    }

    @Override public Rental getById(Integer id) {  String sql = "SELECT * FROM rentals WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEntity(rs);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la căutarea închirierii cu ID " + id + ": " + e.getMessage());
        }

        // Dacă nu s-a găsit niciun rând
        return null;}

    @Override public List<Rental> getAll(){
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rentals.add(extractEntity(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare SQL la preluarea tuturor închirierilor: " + e.getMessage());
        }
        return rentals;
    }


    @Override public int size() { return getCount(); }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM rentals";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Eroare la preluarea ID-urilor închirierilor: " + e.getMessage());
        }
        return ids;
    }

    @Override
    protected void populateIfEmpty() {
        if (getCount() == 0) {
            List<Integer> carIds = carRepo.getAllIds();
            List<Integer> clientIds = clientRepo.getAllIds();

            if (carIds.isEmpty() || clientIds.isEmpty()) {
                System.err.println("[SQL Populator] Eroare: Repository-urile Car/Client sunt goale. Nu se pot genera închirieri.");
                return;
            }

            System.out.println("[SQL Populator] Populez tabelul 'rentals' cu 100 de entități.");
            RentalFaker rentalFaker = new RentalFaker();
            for (int i = 0; i < 100; i++) {
                try {
                    addElem(rentalFaker.generateRandomRental(carIds, clientIds));
                } catch (RepositoryExceptions e) {
                    System.err.println("Eroare la popularea închirierii: " + e.getMessage());
                }
            }
        }
    }
}