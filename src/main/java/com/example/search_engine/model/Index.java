package com.example.search_engine.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "indexes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Index {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(name = "page_id", nullable = false)
    private int pageId;

    @Column(name = "lemma_id", nullable = false)
    private int lemmaId;

    @Column(name = "ranking", nullable = false)
    private float ranking;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index)) return false;
        Index index = (Index) o;
        return getPageId() == index.getPageId() &&
                getLemmaId() == index.getLemmaId() &&
                Float.compare(index.getRanking(), getRanking()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPageId(), getLemmaId(), getRanking());
    }
}
