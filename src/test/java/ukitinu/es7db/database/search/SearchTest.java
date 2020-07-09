package ukitinu.es7db.database.search;

import junit.framework.TestCase;
import ukitinu.es7db.config.Property;

public class SearchTest extends TestCase
{

    public void testSearchClass()
    {
        Query query = new Query().filterTerm("status_field", "status_value");

        Search search = new Search.Builder("my_index", query)
                .sortBy("sort_field", false)
                .build();
        assertEquals(Property.ES_SEARCH_SIZE.getInt(), search.getSize());
        assertEquals(Property.ES_SEARCH_FROM.getInt(), search.getFrom());
        assertFalse(search.isScroll());
        System.out.println(search.toSearch());

        Search scrollSearch = new Search.Builder("new_index", query)
                .sortBy("new_sort_field", true)
                .scroll()
                .build();
        assertTrue(scrollSearch.isScroll());
        assertEquals("new_index", scrollSearch.getIndex());
        System.out.println(scrollSearch.toSearch());
    }
}