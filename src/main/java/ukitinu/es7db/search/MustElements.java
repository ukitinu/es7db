package ukitinu.es7db.search;

import java.util.ArrayList;
import java.util.List;

class MustElements
{
    private final List<MatchElement> matches = new ArrayList<>();

    List<Queryable> getElements()
    {
        return new ArrayList<>(matches);
    }

    void addMatch(String field, String text, boolean isFuzzy)
    {
        matches.add(new MatchElement(field, text, isFuzzy));
    }
}
