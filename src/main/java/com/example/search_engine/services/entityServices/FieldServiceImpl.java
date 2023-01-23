package com.example.search_engine.services.entityServices;

import com.example.search_engine.entityRepositories.FieldRepository;
import com.example.search_engine.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FieldServiceImpl implements FieldService {
    @Autowired
    FieldRepository fieldRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void checkAndInsertDefaultEntities() {

        Optional<Field> optionalFieldTitle = fieldRepository.findById(1);
        Optional<Field> optionalFieldBody = fieldRepository.findById(2);

        if (optionalFieldTitle.isEmpty()) fieldRepository.save(new Field(1, "title", "title", 1f));
        if (optionalFieldBody.isEmpty()) fieldRepository.save(new Field(2, "body", "body", 0.8f));

    }

    public List<Field> findAll() {
        return fieldRepository.findAll();
    }

}
