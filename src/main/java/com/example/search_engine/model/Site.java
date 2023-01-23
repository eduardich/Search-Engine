package com.example.search_engine.model;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sites",
        indexes = {
                @Index(name = "uniqueUrl", columnList = "url", unique = true)
        })
public class Site {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private IndexStatus status;

    @Column(name = "status_time", nullable = false, columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @OneToMany(targetEntity = Page.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "site")
    private List<Page> sitesPages;

    @OneToMany(targetEntity = Lemma.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "site")
    private List<Lemma> sitesLemmas;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site)) return false;
        Site site = (Site) o;
        return name.equals(site.name) && url.equals(site.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }

    public enum IndexStatus {
        INDEXING, INDEXED, FAILED
    }
}
