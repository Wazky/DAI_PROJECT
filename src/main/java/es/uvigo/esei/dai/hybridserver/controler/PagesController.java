package es.uvigo.esei.dai.hybridserver.controler;

import java.util.List;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;
import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;

/**
 * Interface defining the contract for managing web pages.
 * Provides methods for CRUD operations and checking existence of pages.
 */
public interface PagesController {

    /**
     * Retrieves a page by its UUID.
     * 
     * @param uuid the UUID of the page to retrieve
     * @return the page with the specified UUID
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    public Page get(String uuid) throws DAOException, PageNotFoundException;

    /**
     * Lists all pages.
     * 
     * @return a list of all pages
     * @throws DAOException if there is an error accessing the data store
     */
    public List<Page> list() throws DAOException;

    /**
    * Creates a new page.
    * 
    * @param page the page to create
    * @throws DAOException if there is an error accessing the data store
    */
    public void create(Page page) throws DAOException;

    /**
     * Updates the specified page.
     *
     * @param page the page to update
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    public void update(Page page) throws DAOException, PageNotFoundException;

    /**
     * Deletes the page with the specified UUID.
     * 
     * @param uuid the UUID of the page to delete
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    public void delete(String uuid) throws DAOException, PageNotFoundException;

    /**
     * Checks if a page with the specified UUID exists.
     * 
     * @param uuid the UUID of the page to check
     * @return true if the page exists, false otherwise
     * @throws DAOException if there is an error accessing the data store
     */
    public boolean exists(String uuid) throws DAOException;

}
