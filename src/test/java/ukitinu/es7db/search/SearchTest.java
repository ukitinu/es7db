package ukitinu.es7db.search;

import org.junit.jupiter.api.Test;
import ukitinu.es7db.config.Property;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {

    @Test
    public void testClass() {
        Query query = new Query().filterTerm("status_field", "status_value");

        Search search = new Search.Builder("my_index", query)
                .sortBy("sort_field", false)
                .build();
        assertEquals(Property.ES_SEARCH_SIZE.getInt(), search.getSize());
        assertEquals(Property.ES_SEARCH_FROM.getInt(), search.getFrom());
        assertFalse(search.isScroll());
        assertDoesNotThrow(search::toSearch);

        Search scrollSearch = new Search.Builder("new_index", query)
                .sortBy("new_sort_field", true)
                .scroll()
                .build();
        assertTrue(scrollSearch.isScroll());
        assertEquals("new_index", scrollSearch.getIndex());
        assertDoesNotThrow(scrollSearch::toSearch);
    }
}