package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;

/**
 * Data Access Object (DAO) interface for managing Page entities in the database.
 * This interface defines methods for CRUD operations and checking existence of pages.
 */
public interface PageDAO {
    
    /**
     * Retrieves a page by its UUID from the database.
     * @param uuid The UUID of the page to retrieve.
     * @return The Page with the specified UUID.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    public Page get(String uuid) throws DAOException, PageNotFoundException;

    /**
     * Lists all pages in the database.
     * @return A list of all pages.
     * @throws DAOException If an error occurs while accessing the database.
     */
    public List<Page> list() throws DAOException;

    /**
     * Creates a new page in the database.
     * @param page The page to create.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws IllegalArgumentException If the page is null or has a null/empty UUID.
     */
    public void create(Page page) throws DAOException;

    /**
     * Updates the specified page in the database.
     * @param page The page to update.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    public void update(Page page) throws DAOException, PageNotFoundException;

    /**
     * Deletes the page with the specified UUID from the database.
     * @param uuid The UUID of the page to delete.
     * @throws DAOException If an error occurs while accessing the database.
     * @throws PageNotFoundException If no page with the specified UUID exists.
     */
    public void delete(String uuid) throws DAOException, PageNotFoundException;

    /**
     * Checks if a page with the specified UUID exists in the database.
     * @param uuid The UUID of the page to check.
     * @return true if the page exists, false otherwise.
     * @throws DAOException If an error occurs while accessing the database.
     */
    public boolean exists(String uuid) throws DAOException;

}
