package ukitinu.es7db.database;

import org.elasticsearch.search.SearchHit;

import java.util.*;
import java.util.stream.Collectors;

public class SearchResult
{
    private static final String COUNT = "count";
    private static final String LIST = "list";

    private final List<Document> documents;
    private final long total;

    SearchResult(List<Document> documents, long total)
    {
        this.documents = documents;
        this.total = total;
    }

    SearchResult(SearchHit[] hits, long total)
    {
        this.total = total;
        if (hits == null) {
            this.documents = Collections.emptyList();
        } else {
            this.documents = Arrays
                    .stream(hits)
                    .map(Document::new)
                    .collect(Collectors.toList());
        }
    }

    public List<Document> getDocuments()
    {
        return documents;
    }

    public long getTotal()
    {
        return total;
    }

    public Map<String, Object> asMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(LIST, documents.stream().map(Document::getSource).collect(Collectors.toList()));
        map.put(COUNT, total);
        return map;
    }

    public boolean isEmpty()
    {
        return total == 0;
    }
}
