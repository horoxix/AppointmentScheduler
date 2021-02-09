package data;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> get(int id);

    ObservableList<T> getAll();

    void save(T t);

    void update(T t);

    void delete(T t);

    Connection getConnection() throws SQLException;

    void closeConnections();
}
