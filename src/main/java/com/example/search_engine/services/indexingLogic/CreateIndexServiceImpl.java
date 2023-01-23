package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.model.*;

import com.example.search_engine.services.entityServices.*;
import com.example.search_engine.services.searchLogic.LemmaFinder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CreateIndexServiceImpl implements CreateIndexService {

    private final SiteService siteService;
    private final LemmaService lemmaService;
    private final PageService pageService;
    private final IndexService indexService;
    private final FieldService fieldService;
    private final LemmaFinder lemmaFinder;


    public void createAndSavePageIndex(Site site, URL url, int code, String content) {

        Page page = new Page();
        String path = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
        page.setPath(path);
        page.setCode(code);
        page.setContent(content);
        page.setSite(site);

        page = pageService.addNewPageToDB(page);

        siteService.updateSiteStatusInDB(site);

        if (page.getCode() == 200) {
            createPageIndex(site, page, false);
        }

    }


    @Async(value = "executorForIndexing")
    public void excludePageFromIndex(Site site, Page page) {
        createPageIndex(site, page, true);
    }

    private void createPageIndex(Site site, Page page, boolean excludeFromIndex) {

        Document doc = Jsoup.parse(page.getContent());

        for (Field field : fieldService.findAll()) {

            String text = doc.select(field.getSelector()).text();

            Map<String, Integer> m = lemmaFinder.collectLemmas(text);

            for (Map.Entry<String, Integer> entry :
                    m.entrySet()) {

                Lemma lemma = new Lemma();
                lemma.setLemma(entry.getKey());

                int frequency = entry.getValue();
                frequency *= excludeFromIndex ? -1 : 1;
                lemma.setFrequency(frequency);
                lemma.setSite(site);
                lemma = lemmaService.updateOrAddLemma(lemma);


                Index index = new Index();
                index.setPageId(page.getId());
                index.setLemmaId(lemma.getId());
                float rank = field.getWeight() * lemma.getFrequency();
                rank *= excludeFromIndex ? -1 : 1;
                index.setRanking(rank);
                indexService.updateOrAddIndexRowToDB(index);

            }
        }
    }
}
