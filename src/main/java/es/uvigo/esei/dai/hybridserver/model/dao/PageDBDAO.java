package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;

import java.sql.SQLException;
import es.uvigo.esei.dai.hybridserver.PageNotFoundException;

/**
 * Database implementation of the PageDAO interface.
 * This class provides methods to perform CRUD operations on Page entities
 * stored in a relational database.
 */
public class PageDBDAO extends DBDAO implements PageDAO {

    /**
     * Constructs a PageDBDAO with the specified database connection parameters.
     * 
     * @param url the database URL
     * @param user the database user
     * @param password the database password
     */
    public PageDBDAO(String url, String user, String password) {
        super(url, user, password);
    }

    /**
     * Retrieves a page by its UUID from the database.
     * @param uuid The UUID of the page to retrieve.
     * @return The Page with the specified UUID.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    @Override
    public Page get(String uuid) throws DAOException, PageNotFoundException {
        
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("Page UUID cannot be null or empty");
        }

        // Open connection with DB
        try (Connection conn = this.getConnection()) {

            // create query
            final String query = "SELECT uuid, content FROM HTML WHERE uuid = ?";

            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {

                statement.setString(1, uuid);

                // Execute query
                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new PageNotFoundException("Page with UUID " + uuid + " not found", uuid);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("ERROR getting page from the database", e);
        }
    }

    /**
     * Lists all pages in the database.
     * @return A list of all pages.
     * @throws DAOException If an error occurs while accessing the database.
     */
    @Override
    public List<Page> list() throws DAOException {
        // Open connection with DB
        try (Connection conn = this.getConnection()) {

            // Create query
            final String query = "SELECT uuid, content FROM HTML";

            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {

                // Execute query
                try (final ResultSet result = statement.executeQuery()) {

                    final List<Page> pages = new java.util.ArrayList<>();

                    while (result.next()) {
                        pages.add(rowToEntity(result));
                    }
                    return pages;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("Error listing pages from the database", e);
        }
    }

    /**
     * Creates a new page in the database.
     * @param page The page to create.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws IllegalArgumentException If the page is null or has a null/empty UUID.
     */
    @Override
    public void create(Page page) throws DAOException, IllegalArgumentException {
        
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }

        if (page.getUuid() == null || page.getUuid().isEmpty()) {
            throw new IllegalArgumentException("Page UUID or content cannot be null");
        }

        // Open connection with DB
        try (Connection conn = this.getConnection()) {
            
            // Create query
            final String query = "INSERT INTO HTML (uuid, content) VALUES (?, ?)";
            
            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {
            
                statement.setString(1, page.getUuid());
                statement.setString(2, page.getContent());
            
                // Execute query
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new SQLException("Failed to insert the page into the database");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("Error creating page in the database", e);
        }
    }

    /**
     * Updates the specified page in the database.
     * @param page The page to update.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    @Override
    public void update(Page page) throws DAOException, PageNotFoundException {

        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }

        if (page.getUuid() == null || page.getUuid().isEmpty()) {
            throw new IllegalArgumentException("Page UUID or content cannot be null");
        }

        // Open connection with DB
        try (Connection conn = this.getConnection()) {

            // Create query
            final String query = "UPDATE HTML SET content = ? WHERE uuid = ?";            
            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {
            
                statement.setString(1, page.getContent());
                statement.setString(2, page.getUuid());
            
                // Execute query
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new PageNotFoundException("Page with UUID " + page.getUuid() + " not found", page.getUuid());
                } else if (rowsAffected > 1) {
                    throw new SQLException("Multiple pages updated, expected only one");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("Error updating page in the database", e);
        }
    }

    /**
     * Deletes the page with the specified UUID from the database.
     * @param uuid The UUID of the page to delete.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    @Override
    public void delete(String uuid) throws DAOException, PageNotFoundException {
        
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("Page UUID cannot be null or empty");
        }

        // Open connection with DB
        try (Connection conn = this.getConnection()) {

            // Create query
            final String query = "DELETE FROM HTML WHERE uuid = ?";
            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {

                statement.setString(1, uuid);

                // Execute query
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new PageNotFoundException("Page with UUID " + uuid + " not found", uuid);
                } else if (rowsAffected > 1) {
                    throw new SQLException("Multiple pages deleted, expected only one");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("Error deleting page from the database", e);
        }
    }

    /**
     * Checks if a page with the specified UUID exists in the database.
     * @param uuid The UUID of the page to check.
     * @return true if the page exists, false otherwise.
     * @throws DAOException If an error occurs while accessing the database.
     */
    @Override
    public boolean exists(String uuid) throws DAOException {
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("Page UUID cannot be null or empty");
        }

        // Open connection with DB
        try (Connection conn = this.getConnection()) {

            // Create query
            final String query = "SELECT 1 FROM HTML WHERE uuid = ?";

            // Prepare statement
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, uuid);

                // Execute query
                try (final ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("Error checking page existence in the database", e);
        }
    }

    /**
     * Converts a ResultSet row into a Page entity.
     * 
     * @param row The ResultSet positioned at the desired row.
     * @return A Page entity representing the data in the row.
     * @throws SQLException If an SQL error occurs while accessing the ResultSet.
     */
    private Page rowToEntity(final ResultSet row) throws SQLException {
        return new Page(
            row.getString("uuid"),
            row.getString("content")
        );
    }

}
