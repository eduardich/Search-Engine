package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.config.SiteAttributes;
import com.example.search_engine.entityRepositories.SiteRepository;
import com.example.search_engine.config.UserConfig;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SiteIndexUtil {

    private final SiteRepository siteRepository;
    private final SiteService siteService;
    private final ExtractLinksFromSiteService extractLinksFromSiteService;
    private final UserConfig userConfig;
    @Autowired
    ApplicationContext context;


    public void startIndexing() {

        initializeExecutorIfNeeds("executorForIndexing");

        for (SiteAttributes siteAttr :
                userConfig.getSiteAttributes()) {
            getSiteIndexAndSaveToDB(siteAttr.getUrl(), siteAttr.getName());
        }

    }

    public void initializeExecutorIfNeeds(String executorForIndexingName) {
        ThreadPoolTaskExecutor executor = context.getBeansOfType(ThreadPoolTaskExecutor.class).get(executorForIndexingName);
        if (executor.getThreadPoolExecutor().isShutdown()) {
            executor.initialize();
        }
    }


    public void stopIndexing() {

        ThreadPoolTaskExecutor executor = context.getBeansOfType(ThreadPoolTaskExecutor.class).get("executorForIndexing");
        executor.shutdown();

    }

    public void getSiteIndexAndSaveToDB(String startLink, String siteName) {

        Site site = null;

        try {

            site = saveSiteToDB(startLink, siteName);
            extractLinksFromSiteService.extractLinksFromSite(site, normalizeSiteUrl(startLink));

        } catch (Exception e) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            site.setLastError(sw.toString());

            site.setStatus(Site.IndexStatus.FAILED);
            siteService.updateSiteStatusInDB(site);

        }

    }

    public Site saveSiteToDB(String url, String name) throws MalformedURLException {

        Site site = new Site(
                0, name, getProtocolAuthorityString(url),
                Site.IndexStatus.INDEXING, LocalDateTime.now(),
                null, null, null);

        siteService.findByUrlAndRemoveIfExistsWholeSite(site);
        siteRepository.save(site);

        return site;
    }

    private String normalizeSiteUrl(String url) {
        url += "/";
        URI uri = URI.create(url).normalize();
        return uri.toString();
    }

    private String getProtocolAuthorityString(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        String protocol = url.getProtocol();
        String authority = url.getAuthority();
        return String.format("%s://%s", protocol, authority);
    }

}
