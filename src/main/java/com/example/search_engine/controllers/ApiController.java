package com.example.search_engine.controllers;

import com.example.search_engine.dto.simpleResponses.SimpleResponse;
import com.example.search_engine.dto.statistics.StatisticsResponse;

import com.example.search_engine.services.SimpleResponseService;
import com.example.search_engine.services.indexingLogic.ReindexingOnePageService;
import com.example.search_engine.services.indexingLogic.SiteIndexUtil;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.statictics.StatisticsService;
import com.example.search_engine.services.entityServices.SiteService;
import com.example.search_engine.services.searchLogic.SearchResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final StatisticsService statisticsService;
    private final SiteService siteService;
    private final SiteIndexUtil siteIndexUtil;
    private final SimpleResponseService simpleResponseService;
    private final SearchResponseService searchResponseService;
    private final ReindexingOnePageService reindexingOnePageService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public SimpleResponse startIndexing() {

        if (siteService.isAnySiteHaveStatus(Site.IndexStatus.INDEXING)) {
            return simpleResponseService.getErrorResponse("Индексация уже запущена");
        } else {
            siteIndexUtil.startIndexing();
            return simpleResponseService.getSuccessResponse();
        }
    }

    @GetMapping("/stopIndexing")
    public SimpleResponse stopIndexing() {

        if (siteService.isAnySiteHaveStatus(Site.IndexStatus.INDEXING)) {
            siteIndexUtil.stopIndexing();
            return simpleResponseService.getSuccessResponse();
        } else {
            return simpleResponseService.getErrorResponse("Индексация не запущена");
        }
    }

    @PostMapping("/indexPage")
    public ResponseEntity<SimpleResponse> indexPage(@RequestParam(name = "url") String url) {

        return ResponseEntity.ok(reindexingOnePageService.reindexUrl(url));

    }


    @GetMapping("/search")
    public SimpleResponse search(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "site", required = false) Optional<String> siteUrlOpt,
            @RequestParam(name = "offset", required = false) Optional<Integer> offsetOpt,
            @RequestParam(name = "limit", required = false) Optional<Integer> limitOpt
    ) {

        query = URLDecoder.decode(query, StandardCharsets.UTF_8);
        if (query.isBlank()) return simpleResponseService.getErrorResponse("Введите значение для поиска");

        String siteUrl = siteUrlOpt.map(s -> URLDecoder.decode(s, StandardCharsets.UTF_8)).orElse(null);
        int offset = offsetOpt.orElse(0);
        int limit = limitOpt.orElse(10);

        return searchResponseService.getSearchResponse(query, offset, limit, siteUrl);

    }

}
