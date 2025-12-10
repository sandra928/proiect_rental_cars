package repository;

import Exceptions.RepositoryExceptions;
import domain.Client;
import model.ClientFaker;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientSQLRepository extends SQLRepository<Integer, Client> {

    public ClientSQLRepository(Properties props) {
        super(props, "clients");
        populateIfEmpty();
    }

    @Override
    protected Client extractEntity(ResultSet rs) throws SQLException {

        return new Client(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name")
        );
    }

    @Override
    protected void setStatementParameters(PreparedStatement ps, Client client) throws SQLException {

        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
    }

    @Override
    protected void setUpdateStatementParameters(PreparedStatement ps, Client client) throws SQLException {

        ps.setString(1, client.getFirstName());
        ps.setString(2, client.getLastName());
        ps.setInt(3, client.getId());
    }

    @Override
    public void addElem(Client client) {
        String sql = "INSERT INTO clients (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            setStatementParameters(ps, client);
            ps.executeUpdate();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {

                if (rs.next()) {

                    client.setId(rs.getInt(1));
                } else {

                    throw new RepositoryExceptions("Nu s-a putut obține ID-ul generat pentru client.");
                }
            }

        } catch (SQLException e) {

            throw new RepositoryExceptions("Eroare la adăugarea clientului: " + e.getMessage());
        }
    }

    @Override
    public void updateElem(Client client) {
        String sql = "UPDATE clients SET first_name=?, last_name=? WHERE id=?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setUpdateStatementParameters(ps, client);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la actualizarea clientului: " + e.getMessage());
        }
    }

    @Override
    public void deleteElem(Integer id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la ștergerea clientului: " + e.getMessage());
        }
    }

    @Override public Client getById(Integer id) { return null; }

    @Override
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clients.add(extractEntity(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryExceptions("Eroare la preluarea clienților: " + e.getMessage());
        }
        return clients;
    }

    @Override public int size() { return getCount(); }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM clients";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Eroare la preluarea ID-urilor clienților: " + e.getMessage());
        }
        return ids;
    }

    @Override
    protected void populateIfEmpty() {
        if (getCount() == 0) {
            System.out.println("[SQL Populator] Populez tabelul 'clients' cu 50 de entități.");
            ClientFaker clientFaker = new ClientFaker();
            for (int i = 0; i < 50; i++) {
                try {

                    addElem(clientFaker.generateRandomClient());
                } catch (RepositoryExceptions e) {
                    System.err.println("Eroare la popularea clientului: " + e.getMessage());
                }
            }
        }
    }
}