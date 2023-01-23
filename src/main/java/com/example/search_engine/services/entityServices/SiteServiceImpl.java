package com.example.search_engine.services.entityServices;

import com.example.search_engine.entityRepositories.LemmaRepository;
import com.example.search_engine.entityRepositories.PageRepository;
import com.example.search_engine.entityRepositories.SiteRepository;
import com.example.search_engine.model.Site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    SiteRepository siteRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    PageRepository pageRepository;


    public void updateSiteStatusInDB(Site detachedSite) {

        Site site = siteRepository.findById(detachedSite.getId()).get();
        site.setStatus(detachedSite.getStatus());
        site.setStatusTime(LocalDateTime.now());

        if (detachedSite.getStatus().equals(Site.IndexStatus.FAILED)) {
            site.setLastError(detachedSite.getLastError());
        }

        siteRepository.save(site);

    }

    @Transactional
    public void findByUrlAndRemoveIfExistsWholeSite(Site site) {

        if (site.getId() == 0) site = siteRepository.findByUrl(site.getUrl());

        if (site == null) return;

        pageRepository.deleteAllBySite(site);
        lemmaRepository.deleteAllBySite(site);
        siteRepository.delete(site);

    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll();
    }

    @Override
    public boolean isAnySiteHaveStatus(Enum<Site.IndexStatus> status) {
        return siteRepository.isAnySiteHaveStatus(status);
    }

    @Override
    public Site findByUrl(String siteUrl) {
        return siteRepository.findByUrl(siteUrl);
    }

    @Override
    public Site findByUrlLike(String siteUrl) {
        return siteRepository.findByUtlLike(siteUrl);
    }


}
