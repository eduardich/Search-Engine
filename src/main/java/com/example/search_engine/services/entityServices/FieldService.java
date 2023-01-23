package com.example.search_engine.services.entityServices;

import com.example.search_engine.model.Field;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FieldService {

    @Transactional
    public void checkAndInsertDefaultEntities();

    public List<Field> findAll();
}
