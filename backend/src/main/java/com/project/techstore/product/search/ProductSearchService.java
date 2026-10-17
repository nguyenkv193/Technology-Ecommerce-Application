package com.project.techstore.product.search;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.product.dto.ProductFilterRequest;
import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.product.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ElasticsearchOperations operations;

    public PageResponse<ProductResponse> search(ProductFilterRequest filter) {
        int page = Math.max(0, filter.getPage() == null ? 0 : filter.getPage());
        int size = Math.min(MAX_PAGE_SIZE, Math.max(1, filter.getSize() == null ? 20 : filter.getSize()));
        Pageable pageable = PageRequest.of(page, size);

        try {
            NativeQuery query = buildSearchQuery(filter, pageable);
            SearchHits<ProductSearchDocument> hits = operations.search(query, ProductSearchDocument.class);
            List<ProductResponse> products = hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(ProductSearchDocument::toResponse)
                    .toList();

            return PageResponse.of(new PageImpl<>(products, pageable, hits.getTotalHits()));
        } catch (DataAccessException exception) {
            throw new SearchUnavailableException("Elasticsearch product search is unavailable", exception);
        }
    }

    private NativeQuery buildSearchQuery(ProductFilterRequest filter, Pageable pageable) {
        String keyword = filter.getKeyword().trim();

        BoolQuery.Builder keywordClauses = new BoolQuery.Builder();
        keywordClauses.should(query -> query.multiMatch(match -> match
                .query(keyword)
                .fields(List.of(
                        "name^5",
                        "brandName^3",
                        "categoryName^2",
                        "attributesText^2",
                        "description"
                ))));
        keywordClauses.should(query -> query.nested(nested -> nested
                .path("variants")
                .query(nestedQuery -> nestedQuery.multiMatch(match -> match
                        .query(keyword)
                        .fields(List.of("variants.name^2", "variants.sku^4"))))));
        keywordClauses.minimumShouldMatch("1");

        BoolQuery.Builder combined = new BoolQuery.Builder();
        combined.must(Query.of(query -> query.bool(keywordClauses.build())));

        String status = normalizedStatus(filter.getStatus());
        if (status != null) {
            combined.filter(query -> query.term(term -> term.field("status").value(status)));
        }
        if (filter.getCategoryId() != null) {
            combined.filter(query -> query.term(term ->
                    term.field("categoryId").value(filter.getCategoryId())));
        }
        if (filter.getBrandId() != null) {
            combined.filter(query -> query.term(term ->
                    term.field("brandId").value(filter.getBrandId())));
        }
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            combined.filter(query -> query.nested(nested -> nested
                    .path("variants")
                    .query(nestedQuery -> nestedQuery.range(range -> range.number(numberRange -> {
                        numberRange.field("variants.price");
                        if (filter.getMinPrice() != null) {
                            numberRange.gte(filter.getMinPrice().doubleValue());
                        }
                        if (filter.getMaxPrice() != null) {
                            numberRange.lte(filter.getMaxPrice().doubleValue());
                        }
                        return numberRange;
                    })))));
        }

        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(Query.of(query -> query.bool(combined.build())))
                .withPageable(pageable)
                .withTrackTotalHits(true);

        String sortBy = StringUtils.hasText(filter.getSortBy())
                ? filter.getSortBy().trim()
                : "relevance";
        SortOrder order = "ASC".equalsIgnoreCase(filter.getSortDirection())
                ? SortOrder.Asc
                : SortOrder.Desc;

        if ("relevance".equalsIgnoreCase(sortBy)) {
            builder.withSort(sort -> sort.score(score -> score.order(SortOrder.Desc)));
        } else {
            String sortField = switch (sortBy) {
                case "name" -> "nameSort";
                case "createdAt" -> "createdAt";
                default -> "createdAt";
            };
            builder.withSort(sort -> sort.field(field ->
                    field.field(sortField).order(order)));
        }
        builder.withSort(sort -> sort.field(field -> field.field("productId").order(SortOrder.Asc)));
        return builder.build();
    }

    private String normalizedStatus(String requestedStatus) {
        if (!StringUtils.hasText(requestedStatus)) {
            return ProductStatus.ACTIVE.name();
        }
        try {
            return ProductStatus.valueOf(requestedStatus.trim().toUpperCase()).name();
        } catch (IllegalArgumentException ignored) {
            // Match the existing JPA search behavior: an invalid status does not add a status predicate.
            return null;
        }
    }
}
