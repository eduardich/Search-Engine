package com.example.search_engine.services.entityServices;

import com.example.search_engine.model.Page;

public interface PageService {

    Page addNewPageToDB(Page page);

    boolean pathExists(String path, int siteId);

    Page findByPathAndSiteId(String path, int siteId);

    void delete(Page page);
}
