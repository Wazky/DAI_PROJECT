package es.uvigo.esei.dai.hybridserver.model.dao;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base class for database access objects (DAOs).
 * Provides common functionality for establishing database connections.
 */
public class DBDAO {
    
    private final String DB_URL;    // Database URL
    private final String DB_USER;   // Database user
    private final String DB_PASSWORD; // Database password

    /**
     * Constructs a DBDAO with the specified database connection parameters.
     * 
     * @param url the database URL
     * @param user the database user
     * @param password the database password
     */
    public DBDAO(String url, String user, String password) {
        this.DB_URL = url;
        this.DB_USER = user;
        this.DB_PASSWORD = password;

    }

    /**
     * Establishes and returns a new database connection.
     * 
     * @return A new Connection object to the database.
     * @throws SQLException If a database access error occurs.
     */
    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
