package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.config.UserConfig;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.PageService;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;


@Service
public class ExtractLinksFromPageServiceImpl implements ExtractLinksFromPageService {

    @Autowired
    UserConfig userConfig;
    private int delayForNewGetRequest = 500;
    private static final int stepForDelayForNewGetRequest = 500;
    private static final int maxLimitForDelayForNewGetRequest = 6000;
    @Autowired
    private CreateIndexService createIndexService;
    @Autowired
    private PageService pageService;

    @Async(value = "executorForIndexing")
    public CompletableFuture<HashSet<String>> parsePageFromUrlAndGetLinks(Site site, String absLink) throws InterruptedException, IOException {

        if (pageService.pathExists(
                URLDecoder.decode(new URL(absLink).getPath(), StandardCharsets.UTF_8),
                site.getId())) {
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        Connection connection = SSLHelper.getConnection(absLink)
                .userAgent(userConfig.getJsoupConnectionProperties().get("user-agent"))
                .referrer(userConfig.getJsoupConnectionProperties().get("referrer"))
                .maxBodySize(16_777_215);

        try {

            Connection.Response httpResponse = getHttpResponse(site, connection);
            Document document = httpResponse.parse();
            createIndexService.createAndSavePageIndex(
                    site,
                    httpResponse.url(),
                    httpResponse.statusCode(),
                    document.outerHtml()
            );
            return CompletableFuture.completedFuture(findChildrenHRefs(site, absLink, document));

        } catch (NullPointerException ignored) {
        }
        return CompletableFuture.completedFuture(new HashSet<>());

    }

    private Connection.Response getHttpResponse(Site site, Connection connection) throws IOException, InterruptedException {

        for (int delay = delayForNewGetRequest; delay <= maxLimitForDelayForNewGetRequest; delay += stepForDelayForNewGetRequest) {
            try {

                Thread.sleep(delay);
                return connection.execute();

            } catch (HttpStatusException e) {

                if (delay > maxLimitForDelayForNewGetRequest - stepForDelayForNewGetRequest) {
                    createIndexService.createAndSavePageIndex(
                            site,
                            URI.create(e.getUrl()).toURL(),
                            e.getStatusCode(),
                            e.getMessage().substring(0, e.getMessage().indexOf("."))
                    );
                    break;
                }
            }
        }
        return null;
    }

    private HashSet<String> findChildrenHRefs(Site site, String absLink, Document document) {

        HashSet<String> treeLinks = new HashSet<>();
        Elements elements = document.body().select("a[href^=/]");

        for (Element e : elements) {
            String absInnerLink = e.attr("abs:href");

            try {
                if (!absInnerLink.startsWith(absLink)) continue;
                absInnerLink = new URL(URLDecoder.decode(absInnerLink, StandardCharsets.UTF_8)).toString();
                if (absInnerLink.matches(".+#.*$")) continue;
            } catch (MalformedURLException ex) {
                continue;
            }

            treeLinks.add(absInnerLink);
        }

        return treeLinks;
    }
}
