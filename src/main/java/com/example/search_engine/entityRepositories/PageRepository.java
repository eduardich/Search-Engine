package com.example.search_engine.entityRepositories;

import com.example.search_engine.model.Page;
import com.example.search_engine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface PageRepository extends JpaRepository<Page, Integer> {
    @Query("select count(p) from Page p where p.site.id = ?1")
    long countPagesBySiteId(int id);

    @Query("select count(p) from Page p where p.site = :site")
    long countPagesBySite(@Param("site") Site site);

    @Transactional
    @Modifying
    @Query("delete from Page p where p.site = ?1")
    int deleteAllBySite(Site site);

    @Query("select (count(p) > 0) from Page p where p.path like concat('%', ?1, '%') and p.site.id = ?2")
    boolean existsByPath(@NonNull String path, int siteId);

    Page findByPathAndSiteId(String path, int siteId);
}