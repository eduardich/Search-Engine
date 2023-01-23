package com.example.search_engine.services.entityServices;

import com.example.search_engine.model.Site;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SiteService {

    public void updateSiteStatusInDB(Site detachedSite);

    @Transactional
    public void findByUrlAndRemoveIfExistsWholeSite(Site site);

    public List<Site> findAll();

    public boolean isAnySiteHaveStatus(Enum<Site.IndexStatus> status);

    Site findByUrl(String siteUrl);

    Site findByUrlLike(String siteUrl);
}
