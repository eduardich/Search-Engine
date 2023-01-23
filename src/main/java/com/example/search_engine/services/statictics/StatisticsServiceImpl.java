package com.example.search_engine.services.statictics;

import com.example.search_engine.dto.statistics.DetailedStatisticsItem;
import com.example.search_engine.dto.statistics.StatisticsData;
import com.example.search_engine.dto.statistics.StatisticsResponse;
import com.example.search_engine.dto.statistics.TotalStatistics;
import com.example.search_engine.entityRepositories.LemmaRepository;
import com.example.search_engine.entityRepositories.PageRepository;
import com.example.search_engine.entityRepositories.SiteRepository;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteService siteService;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticsResponse getStatistics() {

        List<Site> siteList = siteRepository.findAll();

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (Site site :
                siteList) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            item.setPages((int) pageRepository.countPagesBySiteId(site.getId()));
            item.setLemmas((int) lemmaRepository.countLemmasBySiteId(site.getId()));
            item.setStatus(site.getStatus().toString());
            item.setError(site.getLastError() == null ? " - " : site.getLastError());
            item.setStatusTime(site.getStatusTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            detailed.add(item);
        }

        TotalStatistics total = new TotalStatistics();
        total.setSites(siteList.size());
        total.setIndexing(siteService.isAnySiteHaveStatus(Site.IndexStatus.INDEXING));
        total.setPages((int) pageRepository.count());
        total.setLemmas((int) lemmaRepository.count());

        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);

        StatisticsResponse response = new StatisticsResponse();
        response.setStatistics(data);
        response.setResult(true);
        return response;

    }
}
