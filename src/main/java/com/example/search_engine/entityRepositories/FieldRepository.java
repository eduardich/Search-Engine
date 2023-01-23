package com.example.search_engine.entityRepositories;

import com.example.search_engine.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Integer>  {

}