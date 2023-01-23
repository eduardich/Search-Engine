package com.example.search_engine.services.entityServices;

import com.example.search_engine.entityRepositories.LemmaRepository;
import com.example.search_engine.model.Lemma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class LemmaServiceImpl implements LemmaService {

    @Autowired
    private LemmaRepository lemmaRepository;

    public Lemma addOrUpdateLemma(Lemma lemma) {

        try {

            lemmaRepository.save(lemma);

        } catch (Exception e) {
            if (ThrowableCauseHelper.getInitialCause(e).getMessage()
                    .equals("Duplicate entry .+ for key 'lemmas\\.uniqueLemma'")) {
                lemma = updateOrAddLemma(lemma);
            } else {
                throw e;
            }
        }
        return lemma;

    }

    public Lemma updateOrAddLemma(Lemma lemma) {

        boolean updated =
                lemmaRepository.increaseFrequencyBySiteAndLemma(
                        lemma.getFrequency(),
                        lemma.getSite(),
                        lemma.getLemma()
                ) != 0;

        if (updated) {
            lemma = lemmaRepository.findByLemmaAndSite(lemma.getLemma(), lemma.getSite());
        } else {
            lemma = addOrUpdateLemma(lemma);
        }

        return lemma;
    }

    public List<Lemma> fetchLemmasByNamesIncludeFrequencyLimits(Set<String> searchInfinitiveWordsSet) {

        return lemmaRepository.findByNamesIncludeFrequencyLimits(searchInfinitiveWordsSet);

    }

    @Override
    public List<Lemma> fetchLemmasByNamesAndSiteIdIncludeFrequencyLimits(int siteId, Set<String> searchInfinitiveWordsSet) {
        return lemmaRepository.findByNamesAndSiteIdIncludeFrequencyLimits(siteId, searchInfinitiveWordsSet);
    }


}
