package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.model.Page;
import com.example.search_engine.model.Site;

import java.net.URL;

public interface CreateIndexService {

    void createAndSavePageIndex(Site site, URL url, int code, String content);

    void excludePageFromIndex(Site site, Page page);
}
