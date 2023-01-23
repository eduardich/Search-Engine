package com.example.search_engine.entityRepositories;

import com.example.search_engine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface SiteRepository extends JpaRepository<Site, Integer> {
    @Query("select s from Site s where s.url like concat('%' , ?1 , '%')")
    Site findByUtlLike(String url);


    @Query("select (count(s) > 0) from Site s where s.status like ?1")
    boolean isAnySiteHaveStatus(@NonNull Enum<Site.IndexStatus> status);

    Site findByUrl(String url);

}