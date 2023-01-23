package com.example.search_engine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.persistence.Index;

import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "pages",
        indexes = {
                @Index(name = "uniquePathOnSite", columnList = "path, site_id", unique = true)
        })
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Page implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;


    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "code", nullable = false)
    private int code;

    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
    @ToString.Exclude
    @ManyToOne(targetEntity = Site.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "indexes",
            joinColumns = @JoinColumn(name = "page_id"),
            inverseJoinColumns = @JoinColumn(name = "lemma_id")
    )
    private List<Lemma> lemmaList;


    @Override
    public int compareTo(Object o) {
        Page page = (Page) o;
        return (this.path.compareTo(page.path));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;
        Page page = (Page) o;
        return
                getCode() == page.getCode() &&
                        getPath().equals(page.getPath()) &&
                        getContent().equals(page.getContent()) &&
                        Objects.equals(site, page.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPath(), getCode(), getContent());
    }

}
