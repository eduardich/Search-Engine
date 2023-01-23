package com.example.search_engine.services.entityServices;

import com.example.search_engine.model.Index;

public interface IndexService {

    public  void updateOrAddIndexRowToDB(Index index);

    public float getRating(int pageId, int lemmaId);
}
