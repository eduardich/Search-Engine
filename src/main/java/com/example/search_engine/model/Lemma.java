package com.example.search_engine.model;

import lombok.*;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lemmas",
        indexes = {
                @Index(name = "uniqueLemmaOnSite", columnList = "lemma, site_id", unique = true)
        })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private int id;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private int frequency;

    @ToString.Exclude
    @ManyToOne(targetEntity = Site.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lemmaList")
    private List<Page> pageList;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lemma)) return false;
        Lemma lemma = (Lemma) o;
        return frequency == lemma.frequency &&
                this.lemma.equals(lemma.lemma) &&
                Objects.equals(site, lemma.site);
    }
}
