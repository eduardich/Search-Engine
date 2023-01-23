package com.example.search_engine.config;

import com.example.search_engine.model.Site;
import com.example.search_engine.services.entityServices.SiteService;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

@Component
public class IndexingExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Autowired
    private SiteService siteService;

    @Autowired
    private ApplicationContext context;


    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        if (method.getName().equals("extractLinksFromSite")) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            for (Object param : params) {
                if (!(param instanceof Site)) continue;

                Site site = (Site) param;

                site.setLastError(
                        ex instanceof java.lang.InterruptedException ?
                                "Индексация остановлена пользователем" :
                                sw.toString()
                );

                site.setStatus(Site.IndexStatus.FAILED);
                siteService.updateSiteStatusInDB(site);
            }

        } else {
            ex.printStackTrace();
        }

    }
}

