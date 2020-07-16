package ukitinu.es7db.database.search;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import ukitinu.es7db.config.Property;

import java.util.Objects;

public final class Search {
    private static final int PAGINATION_THRESHOLD = 10_000;
    private static final int DEFAULT_FROM = Property.ES_SEARCH_FROM.getInt();
    private static final int DEFAULT_SIZE = Property.ES_SEARCH_SIZE.getInt();

    private String index;
    private Query query;
    private SortElement sort;
    private int from;
    private int size;

    private boolean isScroll;

    private Search() {
        //use the Builder class
    }

    public String getIndex() {
        return index;
    }

    public Query getQuery() {
        return query;
    }

    public SortElement getSort() {
        return sort;
    }

    public int getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }

    public boolean isScroll() {
        return isScroll;
    }

    public SearchSourceBuilder toSearch() {
        SearchSourceBuilder search = new SearchSourceBuilder();
        search.query(query.toQuery());
        if (sort != null) {
            search.sort(sort.getField(), sort.getSortOrder());
        }
        if (!isScroll) {
            search.size(size);
            search.from(from);
        }
        return search;
    }

    public static class Builder {
        private final Query query;
        private final String index;
        private SortElement sort;
        private int from;
        private int size;
        private boolean isScroll;

        public Builder(String index, Query query) {
            Objects.requireNonNull(index, "Search index must not be null");
            Objects.requireNonNull(query, "Search query must not be null");
            this.index = index;
            this.query = query;
            this.sort = null;
            this.from = DEFAULT_FROM;
            this.size = DEFAULT_SIZE;
            this.isScroll = false;
        }

        public Builder sortBy(String field, boolean isAscending) {
            sortBy(new SortElement(field, isAscending));
            return this;
        }

        public Builder sortBy(SortElement sort) {
            this.sort = sort;
            return this;
        }

        public Builder withBounds(int from, int size) {
            this.from = from;
            this.size = size;
            return this;
        }

        public Builder scroll() {
            this.isScroll = true;
            return this;
        }

        public Search build() {
            validate();
            return create();
        }

        private Search create() {
            Search search = new Search();
            search.index = this.index;
            search.query = this.query;
            search.sort = sort;
            search.isScroll = this.isScroll;
            search.from = this.from;
            search.size = this.size;
            return search;
        }

        private void validate() {
            StringBuilder errors = new StringBuilder();
            if (from + size > PAGINATION_THRESHOLD) {
                errors.append("Pagination threshold exceeded: size + from = ")
                        .append(from + size)
                        .append(System.lineSeparator());
            }
            if (from < 0) {
                errors.append("\"from\" value negative: ")
                        .append(from)
                        .append(System.lineSeparator());
            }
            if (size < 0) {
                errors.append("\"size\" value negative: ")
                        .append(size)
                        .append(System.lineSeparator());
            }
            if (errors.length() > 0) {
                throw new IllegalStateException("Invalid search: " + System.lineSeparator() + errors.toString());
            }
        }
    }

}
