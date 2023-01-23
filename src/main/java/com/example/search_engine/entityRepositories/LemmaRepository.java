package com.example.search_engine.entityRepositories;

import com.example.search_engine.model.Lemma;
import com.example.search_engine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query("select count(l) from Lemma l where l.site.id = ?1")
    long countLemmasBySiteId(int id);

    @Transactional
    @Modifying
    @Query("select l from Lemma l join ("
            + "select (max(lfilter.frequency)-min(lfilter.frequency))/2 as fboundary, lfilter.site.id as site_id from Lemma lfilter group by site_id"
            + ") filter on l.site.id = filter.site_id where l.lemma in ?1 and l.frequency < filter.fboundary")
    List<Lemma> findByNamesIncludeFrequencyLimits(Collection<String> lemma);

    @Transactional
    @Modifying
    @Query("select l from Lemma l join ("
            + "select (max(lfilter.frequency)-min(lfilter.frequency))/2 as fboundary, lfilter.site.id as site_id from Lemma lfilter group by site_id"
            + ") filter on l.site.id = filter.site_id where l.site.id = ?1 and l.lemma in ?2 and l.frequency < filter.fboundary")
    List<Lemma> findByNamesAndSiteIdIncludeFrequencyLimits(int siteId, Collection<String> lemma);


    @Transactional
    @Modifying
    @Query("update Lemma l set l.frequency = l.frequency + ?1 where l.site = ?2 and l.lemma = ?3")
    int increaseFrequencyBySiteAndLemma(float frequency, @NonNull Site site, @NonNull String lemma);

    @Query("select l from Lemma l where l.lemma = ?1 and l.site = ?2")
    Lemma findByLemmaAndSite(String lemma, Site site);

    @Transactional
    @Modifying
    @Query("delete from Lemma l where l.site = ?1")
    int deleteAllBySite(Site site);


}