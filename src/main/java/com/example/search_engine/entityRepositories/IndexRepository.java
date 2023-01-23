package com.example.search_engine.entityRepositories;

import com.example.search_engine.model.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IndexRepository extends JpaRepository<Index, Integer> {
    @Transactional
    @Modifying
    @Query("update Index i set i.ranking = ?1 where i.pageId = ?2 and i.lemmaId = ?3")
    int updateRankingByPageIdAndLemmaId(float ranking, int pageId, int lemmaId);

    @Transactional
    @Modifying
    @Query("select i from Index i where i.pageId = ?1 and i.lemmaId = ?2")
    List<Index> findAllByPageIdAndLemmaId(int pageId, int lemmaId);
}