package es.uvigo.esei.dai.hybridserver.controler;

import java.util.List;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;
import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;
import es.uvigo.esei.dai.hybridserver.model.dao.PageDAO;

/**
 * Default implementation of the PagesController interface.
 * This class uses a PageDAO to perform CRUD operations on Page entities.
 */
public class DefaultPagesController implements PagesController {

    private final PageDAO dao;  // Data Access Object for Page entities

    /**
     * Constructs a DefaultPagesController with the specified PageDAO.
     * 
     * @param dao the PageDAO to use for data access
     */
    public DefaultPagesController(PageDAO dao) {
        this.dao = dao;
    }

    /**
     * Retrieves a page by its UUID.
     * 
     * @param uuid the UUID of the page to retrieve
     * @return the page with the specified UUID
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    @Override
    public Page get(String uuid) throws DAOException, PageNotFoundException {
        return this.dao.get(uuid);
    }

    /**
     * Lists all pages.
     * 
     * @return a list of all pages
     * @throws DAOException if there is an error accessing the data store
     */
    @Override
    public List<Page> list() throws DAOException {
        return this.dao.list();        
    }
    
    /**
     * Creates a new page.
     * 
     * @param page the page to create
     * @throws DAOException if there is an error accessing the data store
     */
    @Override
    public void create(Page page) throws DAOException {
        this.dao.create(page);
    }

    /**
     * Updates the specified page.
     * 
     * @param page the page to update
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    @Override
    public void update(Page page) throws DAOException, PageNotFoundException {
        this.dao.update(page);
    }

    /**
     * Deletes the page with the specified UUID.
     * 
     * @param uuid the UUID of the page to delete
     * @throws DAOException if there is an error accessing the data store
     * @throws PageNotFoundException if the page with the specified UUID does not exist
     */
    @Override
    public void delete(String uuid) throws DAOException, PageNotFoundException {
        this.dao.delete(uuid);
    }

    /**
     * Checks if a page with the specified UUID exists.
     * 
     * @param uuid the UUID of the page to check
     * @return true if the page exists, false otherwise
     * @throws DAOException if there is an error accessing the data store
     */
    @Override
    public boolean exists(String uuid) throws DAOException{
        return this.dao.exists(uuid);
    }

}
