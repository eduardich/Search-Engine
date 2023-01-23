package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.model.Site;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ExtractLinksFromSiteService {

    void extractLinksFromSite(Site site, String StartLink) throws InterruptedException, IOException, ExecutionException;


}
