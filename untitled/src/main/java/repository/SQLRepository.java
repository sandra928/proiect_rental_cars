package repository;

import domain.Identifiable;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public abstract class SQLRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {

    protected final String url;
    protected final String tableName;

    public SQLRepository(Properties props, String tableName) {
        this.url = props.getProperty("Db_Url");
        this.tableName = tableName;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driverul SQLite nu a fost găsit. Adăugați dependency-ul: " + e.getMessage());
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url);
    }

    protected int getCount() {
        String sql = "SELECT COUNT(*) FROM " + tableName + ";";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la numărarea înregistrărilor din tabelul " + tableName + ": " + e.getMessage());
        }
        return 0;
    }

    protected abstract T extractEntity(ResultSet rs) throws SQLException;
    protected abstract void setStatementParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setUpdateStatementParameters(PreparedStatement ps, T entity) throws SQLException;


    @Override public abstract void addElem(T entity);
    @Override public abstract void deleteElem(ID id);
    @Override public abstract void updateElem(T entity);
    @Override public abstract T getById(ID id);
    @Override public abstract List<T> getAll();
    @Override public abstract int size();


    public abstract List<ID> getAllIds();
    protected abstract void populateIfEmpty();
}