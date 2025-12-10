package repository;

import Exceptions.RepositoryExceptions;
import domain.Car;
import model.CarFaker;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarSQLRepository extends SQLRepository<Integer, Car> {

    public CarSQLRepository(Properties props) {
        super(props, "cars");
        populateIfEmpty();
    }

    @Override
    protected Car extractEntity(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("id"),
                rs.getString("brand"),
                rs.getString("model")
        );
    }

    @Override
    protected void setStatementParameters(PreparedStatement ps, Car car) throws SQLException {

        ps.setString(1, car.getBrand());
        ps.setString(2, car.getModel());
    }

    @Override
    protected void setUpdateStatementParameters(PreparedStatement ps, Car car) throws SQLException {

        ps.setString(1, car.getBrand());
        ps.setString(2, car.getModel());
        ps.setInt(3, car.getId());
    }

    @Override
    public void addElem(Car car) {
        String sql = "INSERT INTO cars (brand, model) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            setStatementParameters(ps, car);
            ps.executeUpdate();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    car.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la adăugarea mașinii: " + e.getMessage());
        }
    }

    @Override
    public void updateElem(Car car) {
        String sql = "UPDATE cars SET brand=?, model=? WHERE id=?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setUpdateStatementParameters(ps, car);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la actualizarea mașinii: " + e.getMessage());
        }
    }

    @Override
    public void deleteElem(Integer id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la ștergerea mașinii: " + e.getMessage());
        }
    }

    @Override
    public Car getById(Integer id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la căutarea mașinii: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cars.add(extractEntity(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la preluarea tuturor mașinilor: " + e.getMessage());
        }
        return cars;
    }

    @Override public int size() { return getCount(); }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM cars";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Eroare la preluarea ID-urilor mașinilor: " + e.getMessage());
        }
        return ids;
    }

    @Override
    protected void populateIfEmpty() {
        if (getCount() == 0) {
            System.out.println("[SQL Populator] Populez tabelul 'cars' cu 100 de entități.");
            CarFaker carFaker = new CarFaker();
            for (int i = 0; i < 100; i++) {
                try {
                    addElem(carFaker.generateRandomCar());
                } catch (RepositoryExceptions e) {
                    System.err.println("Eroare la popularea mașinii: " + e.getMessage());
                }
            }
        }
    }
}