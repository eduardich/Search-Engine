package com.example.search_engine.model;

import lombok.*;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import java.util.Objects;

@Entity
@Table(name = "fields",
        indexes = {
                @Index(name = "uniqueName", columnList = "name", unique = true)
        })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Field {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "selector", nullable = false)
    private String selector;

    @Column(name = "weight", nullable = false)
    private float weight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return Float.compare(field.weight, weight) == 0 &&
                name.equals(field.name) &&
                selector.equals(field.selector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, selector, weight);
    }
}

