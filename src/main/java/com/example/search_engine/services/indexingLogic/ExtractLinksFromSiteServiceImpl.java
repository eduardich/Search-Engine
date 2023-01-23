package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ExtractLinksFromSiteServiceImpl implements ExtractLinksFromSiteService {
    @Autowired
    SiteService siteService;
    @Autowired
    ExtractLinksFromPageService extractLinksFromPageService;


    @Async(value = "executorForIndexing")
    public void extractLinksFromSite(Site site, String startLink) throws InterruptedException, IOException, ExecutionException {

        Queue<String> queueUrlsToExtract = new ConcurrentLinkedQueue<String>();
        queueUrlsToExtract.add(startLink);

        while (!queueUrlsToExtract.isEmpty()) {

            List<CompletableFuture<HashSet<String>>> extractedLinksFutures = new ArrayList<>();

            for (String link : queueUrlsToExtract) {
                extractedLinksFutures.add(
                        extractLinksFromPageService.parsePageFromUrlAndGetLinks(site, link)
                );
            }

            CompletableFuture<Void> allFutures =
                    CompletableFuture.allOf(
                            extractedLinksFutures.toArray(new CompletableFuture[0])
                    );

            CompletableFuture<List<String>> allExtractedLinksFutures =
                    allFutures.thenApply(future -> extractedLinksFutures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(HashSet::stream)
                            .collect(Collectors.toList()));

            queueUrlsToExtract = new ConcurrentLinkedQueue<>(allExtractedLinksFutures.get());

        }

        if (site.getStatus().equals(Site.IndexStatus.INDEXING)) {
            site.setStatus(Site.IndexStatus.INDEXED);
            siteService.updateSiteStatusInDB(site);
        }

    }


}
