package com.example.search_engine.services.entityServices;

import com.example.search_engine.entityRepositories.IndexRepository;
import com.example.search_engine.model.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    IndexRepository indexRepository;

    public synchronized void updateOrAddIndexRowToDB(Index index) {

        try {

            boolean updated = indexRepository.updateRankingByPageIdAndLemmaId(
                    index.getRanking(),
                    index.getPageId(),
                    index.getLemmaId()) != 0;

            if (!updated) {
                indexRepository.save(index);
            }
        } catch (Exception e) {
            if (ThrowableCauseHelper.getInitialCause(e).getMessage()
                    .equals("Duplicate entry .+ for key .+?")) {
                updateOrAddIndexRowToDB(index);
            } else {
                throw e;
            }

        }

    }

    public float getRating(int pageId, int lemmaId) {

        return indexRepository
                .findAllByPageIdAndLemmaId(pageId, lemmaId)
                .stream()
                .map(Index::getRanking)
                .reduce(0f, Float::sum); // Summarizing needs if DB table 'indexes' have duplicates

    }


}
