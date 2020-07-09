package ukitinu.es7db.database;

import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResult
{
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

    public boolean isEmpty()
    {
        return total == 0;
    }
}
