package com.example.search_engine.dto.search;

import lombok.*;

import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {

    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResultDTO)) return false;
        SearchResultDTO that = (SearchResultDTO) o;
        return Double.compare(that.relevance, relevance) == 0 &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(title, that.title) &&
                Objects.equals(snippet, that.snippet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, title, snippet, relevance);
    }

}
