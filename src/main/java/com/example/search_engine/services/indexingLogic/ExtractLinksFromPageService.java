package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.model.Site;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public interface ExtractLinksFromPageService {


    CompletableFuture<HashSet<String>> parsePageFromUrlAndGetLinks(Site site, String absLink) throws InterruptedException, IOException;

}
