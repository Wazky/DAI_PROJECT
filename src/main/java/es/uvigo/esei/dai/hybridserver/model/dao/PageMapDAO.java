package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;

public class PageMapDAO implements PageDAO {

    private Map<String, String> pages;

    public PageMapDAO() {
        this.pages = new HashMap<>();
    }

    public PageMapDAO(Map<String, String> pages) {
        this.pages = pages;
    }

    @Override
    public Page get(String uuid) throws DAOException, PageNotFoundException {
        if (!this.pages.containsKey(uuid)) {
            throw new PageNotFoundException("Page with UUID " + uuid + " not found");
        }
        return new Page(uuid, this.pages.get(uuid));
    }

    @Override
    public List<Page> list() throws DAOException {

        List<Page> list = new LinkedList<>();
        for (String uuid : this.pages.keySet()) {
            list.add(new Page(uuid, this.pages.get(uuid)));
        }

        return list;
    }

    @Override
    public void create(Page page) throws DAOException, IllegalArgumentException {

        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }

        if (page.getUuid() == null || page.getUuid().isEmpty()) {
            throw new IllegalArgumentException("Page UUID cannot be null or empty");
        }

        this.pages.put(page.getUuid(), page.getContent());

    }

    @Override
    public void update(Page page) throws DAOException, PageNotFoundException {
        if (!this.pages.containsKey(page.getUuid())) {
            throw new PageNotFoundException("Page with UUID " + page.getUuid() + " not found");
        }

        this.pages.put(page.getUuid(), page.getContent());
        
    }

    @Override
    public void delete(String uuid) throws DAOException, PageNotFoundException {
        
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }

        if (!this.pages.containsKey(uuid)) {
            throw new PageNotFoundException("Page with UUID " + uuid + " not found");
        }

        this.pages.remove(uuid);
    }

    @Override
    public boolean exists(String uuid) throws DAOException {
        return this.pages.containsKey(uuid);    
    }
    
}
