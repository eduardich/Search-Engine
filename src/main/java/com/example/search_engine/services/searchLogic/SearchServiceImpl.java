package com.example.search_engine.services.searchLogic;


import com.example.search_engine.dto.search.SearchResultDTO;
import com.example.search_engine.model.Lemma;
import com.example.search_engine.model.Page;
import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.IndexService;
import com.example.search_engine.services.entityServices.LemmaService;
import com.example.search_engine.services.entityServices.SiteService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final LemmaFinder lemmaFinder;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final SiteService siteService;
    private final int snippetLineLimit = 5;

    @Override
    public List<SearchResultDTO> getResultDTOs(String searchQueue, String siteUrl) {
        Set<String> searchInfinitiveWordsSet = lemmaFinder.getLemmaSet(searchQueue);

        Map<Integer, TreeSet<Lemma>> siteIdFilteredSearchLemmasMap = collectSiteIdFilteredSearchLemmasMap(searchInfinitiveWordsSet, siteUrl);

        Set<String> searchLemmas = extractLemmas(siteIdFilteredSearchLemmasMap);

        Map<Page, Float> pageRelMap = getPagesRelevanceMap(siteIdFilteredSearchLemmasMap);

        return pageRelMap.entrySet().stream()
                .map(entry -> {
                    Page page = entry.getKey();
                    float relevance = entry.getValue();
                    return new SearchResultDTO(
                            page.getSite().getUrl(),
                            page.getSite().getName(),
                            page.getPath(),
                            getPageTitle(page),
                            getPageSnippet(page, searchLemmas, snippetLineLimit),
                            relevance);
                })
                .sorted(Comparator.comparing(SearchResultDTO::getRelevance).reversed())
                .collect(Collectors.toList());
    }

    private Set<String> extractLemmas(Map<Integer, TreeSet<Lemma>> siteIdFilteredSearchLemmasMap) {
        return siteIdFilteredSearchLemmasMap.values().stream()
                .flatMap(TreeSet::stream)
                .map(Lemma::getLemma)
                .collect(Collectors.toSet());
    }

    private String getPageTitle(Page page) {
        return Jsoup.parse(page.getContent()).title();
    }

    private String getPageSnippet(Page page, Set<String> searchLemmas, int snippetLineLimit) {

        Document doc = Jsoup.parse(page.getContent());

        Set<String> wordsOnPage = lemmaFinder.getSearchOriginWordsSet(doc.text(), searchLemmas);

        String cssQueryRegex = "*:matchesWholeOwnText((?iu).*?\\b" +
                String.join("\\b.*?), *:matchesWholeOwnText((?iu).*?\\b", wordsOnPage) +
                "\\b.*?)";
        Elements elements = doc.select(cssQueryRegex);

        HashSet<String> pageSnippetSet = new HashSet<>();

        int count = 0;
        eachTextElement:
        for (String elementText : elements.eachText()) {

            for (String wordOnPage : wordsOnPage) {

                Pattern pattern = Pattern.compile(
                        "(?iU)((\\b\\w+?\\b\\W+?){0,4})(\\b"
                                .concat(wordOnPage)
                                .concat("\\b)((\\W+?\\b\\w+?\\b){0,4})"));
                Matcher matcher = pattern.matcher(elementText);
                StringBuilder elementSnippetStrBuilder;

                while (matcher.find()) {
                    elementSnippetStrBuilder = new StringBuilder();
                    elementSnippetStrBuilder.append(matcher.group(1));
                    elementSnippetStrBuilder.append("<b>");
                    elementSnippetStrBuilder.append(matcher.group(3));
                    elementSnippetStrBuilder.append("</b>");
                    elementSnippetStrBuilder.append(matcher.group(4));
                    elementSnippetStrBuilder.append(" ...");

                    if (++count > snippetLineLimit) break eachTextElement;
                    pageSnippetSet.add(elementSnippetStrBuilder.toString());
                }

            }

        }

        return String.join(System.lineSeparator(), pageSnippetSet).trim();
    }


    private Map<Page, Float> getPagesRelevanceMap(Map<Integer, TreeSet<Lemma>> siteIdFilteredSearchLemmasMap) {

        Map<Integer, TreeSet<Page>> resultSearchSiteIdPagesMap = getSiteIdPagesMap(siteIdFilteredSearchLemmasMap);
        Map<Page, Float> pageAbsRelevanceMap = new HashMap<>();

        for (Map.Entry<Integer, TreeSet<Page>> entry :
                resultSearchSiteIdPagesMap.entrySet()) {
            Map<Page, Float> pageRelForOneSiteMap = new HashMap<>();
            TreeSet<Page> pages = entry.getValue();


            for (Page p : pages) {

                float pageRelevance = 0;

                for (Lemma l : siteIdFilteredSearchLemmasMap.get(entry.getKey())) {

                    pageRelevance += indexService.getRating(p.getId(), l.getId());

                }

                pageRelForOneSiteMap.put(p, pageRelevance);

            }


            float maxRelevanceOnOneSite = pageRelForOneSiteMap
                    .values()
                    .stream()
                    .max(Float::compareTo)
                    .orElse(0f);

            pageRelForOneSiteMap.replaceAll((k, v) -> v / maxRelevanceOnOneSite);
            pageAbsRelevanceMap.putAll(pageRelForOneSiteMap);

        }

        return pageAbsRelevanceMap;

    }

    private Map<Integer, TreeSet<Lemma>> collectSiteIdFilteredSearchLemmasMap(Set<String> searchInfinitiveWordsSet, String siteUrl) {

        Site site = fetchSiteByThisUrl(siteUrl);

        List<Lemma> fetchedLemmas = site == null ?
                lemmaService.fetchLemmasByNamesIncludeFrequencyLimits(searchInfinitiveWordsSet) :
                lemmaService.fetchLemmasByNamesAndSiteIdIncludeFrequencyLimits(site.getId(), searchInfinitiveWordsSet);

        return fetchedLemmas.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getSite().getId(),
                        Collectors.toCollection(
                                () -> new TreeSet<>(Comparator.comparingInt(Lemma::getFrequency))
                        )
                ));
    }

    private Site fetchSiteByThisUrl(String siteUrl) {
        return siteService.findByUrl(siteUrl);
    }

    private Map<Integer, TreeSet<Page>> getSiteIdPagesMap(Map<Integer, TreeSet<Lemma>> siteIdFilteredSearchLemmasMap) {

        Map<Integer, TreeSet<Page>> siteIdPagesMap = new HashMap<>();

        for (Map.Entry<Integer, TreeSet<Lemma>> entry :
                siteIdFilteredSearchLemmasMap.entrySet()) {

            int siteId = entry.getKey();
            TreeSet<Lemma> lemmas = entry.getValue();

            if (lemmas.size() == 1) {
                siteIdPagesMap.put(siteId, new TreeSet<>(lemmas.first().getPageList()));
                continue;
            }

            siteIdPagesMap.put(
                    siteId,
                    lemmas.first()
                            .getPageList().stream()
                            .filter(page ->
                                    lemmas.stream()
                                            .skip(1)
                                            .map(Lemma::getPageList)
                                            .flatMap(Collection::stream)
                                            .anyMatch(page1 -> Objects.equals(page1, page)))
                            .collect(Collectors.toCollection(TreeSet::new)));

        }

        return siteIdPagesMap;
    }


}
