package com.example.search_engine.services.entityServices;


import com.example.search_engine.entityRepositories.PageRepository;
import com.example.search_engine.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageRepository pageRepository;

    public Page addNewPageToDB(Page page) {
        try {
            pageRepository.save(page);
        } catch (Exception e) {
            if (ThrowableCauseHelper.getInitialCause(e)
                    .getMessage()
                    .matches("Duplicate entry .+ for key 'pages\\.uniquePathOnSite'")) {
                page.setCode(0);
            } else {
                throw e;
            }
        }
        return page;
    }

    @Override
    public boolean pathExists(String path, int siteId) {
        return pageRepository.existsByPath(path, siteId);
    }

    @Override
    public Page findByPathAndSiteId(String path, int siteId) {
        return pageRepository.findByPathAndSiteId(path, siteId);
    }

    @Override
    public void delete(Page page) {
        pageRepository.delete(page);
    }


}
