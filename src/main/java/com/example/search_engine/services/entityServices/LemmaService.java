package com.example.search_engine.services.entityServices;

import com.example.search_engine.model.Lemma;

import java.util.List;
import java.util.Set;

public interface LemmaService {

    public Lemma addOrUpdateLemma(Lemma lemma);

    public Lemma updateOrAddLemma(Lemma lemma);

    public List<Lemma> fetchLemmasByNamesIncludeFrequencyLimits(Set<String> searchInfinitiveWordsSet);

    public List<Lemma> fetchLemmasByNamesAndSiteIdIncludeFrequencyLimits(int siteId, Set<String> searchInfinitiveWordsSet);
}
