package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.config.SiteAttributes;
import com.example.search_engine.config.UserConfig;
import com.example.search_engine.dto.simpleResponses.SimpleResponse;
import com.example.search_engine.model.Page;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.SimpleResponseService;
import com.example.search_engine.services.entityServices.PageService;
import com.example.search_engine.services.entityServices.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ReindexingOnePageServiceImpl implements ReindexingOnePageService {

    private final SimpleResponseService simpleResponseService;
    private final SiteService siteService;
    private final SiteIndexUtil siteIndexUtil;
    private final PageService pageService;
    private final ExtractLinksFromPageService extractLinksFromPageService;
    private final CreateIndexService createIndexService;
    private final UserConfig userConfig;

    private boolean wasNewPage;

    @Override
    public SimpleResponse reindexUrl(String urlStr) {


        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return simpleResponseService
                    .getErrorResponse(
                            "Не удаётся распознать URL-адрес, проверьте корректность написания");
        }

        String siteName = getSiteNameFromProperties(url);
        if (siteName == null) {
            return simpleResponseService
                    .getErrorResponse(
                            "Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }

        Site site = siteService.findByUrlLike(url.getHost());
        if (site == null) {
            wasNewPage = true;
            try {
                site = siteIndexUtil.saveSiteToDB(url.toString(), siteName);
            } catch (MalformedURLException ignored) {
            }
        }


        Page page = pageService.findByPathAndSiteId(url.getPath(), site.getId());
        if (page != null) {
            createIndexService.excludePageFromIndex(site, page);
            pageService.delete(page);
        }

        siteIndexUtil.initializeExecutorIfNeeds("executorForIndexing");

        try {
            extractLinksFromPageService.parsePageFromUrlAndGetLinks(site, url.toString()).join();
        } catch (IOException e) {
            return simpleResponseService.getErrorResponse("Во время индексации произошла ошибка, попробуйте снова");
        } catch (InterruptedException e) {
            return simpleResponseService.getErrorResponse("Индексация была остановлена пользователем");
        }

        if (wasNewPage && site.getStatus().equals(Site.IndexStatus.INDEXING)) {
            site.setStatus(Site.IndexStatus.INDEXED);
            siteService.updateSiteStatusInDB(site);
        }

        return simpleResponseService.getSuccessResponse();
    }

    private String getSiteNameFromProperties(URL url) {
        return userConfig.getSiteAttributes().stream()
                .filter(siteAttributes -> {
                    try {
                        return new URL(siteAttributes.getUrl()).getHost().equals(url.getHost());
                    } catch (MalformedURLException e) {
                        return false;
                    }
                })
                .map(SiteAttributes::getName)
                .findFirst()
                .orElse(null);
    }

}
