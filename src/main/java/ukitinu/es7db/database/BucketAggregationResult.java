package ukitinu.es7db.database;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BucketAggregationResult
{
    private static final String KEY = "key";
    private static final String COUNT = "count";
    private static final String LIST = "list";
    private static final String OTHER_COUNT = "other_count";

    private final List<Entry> entryList = new ArrayList<>();
    private long otherCount = -1L;

    void setOtherCount(long otherCount)
    {
        this.otherCount = otherCount;
    }

    void addEntry(String key, long count)
    {
        entryList.add(new Entry(key, count));
    }

    public List<Entry> getEntryList()
    {
        return new ArrayList<>(entryList);
    }

    public long getOtherCount()
    {
        return otherCount;
    }

    @SuppressWarnings("unchecked")
    public JSONObject asJson()
    {
        JSONObject json = new JSONObject();
        json.put(LIST, entryList.stream().map(Entry::asJson).collect(Collectors.toList()));
        if (otherCount != -1L) {
            json.put(OTHER_COUNT, otherCount);
        }
        return json;
    }

    public static final class Entry
    {
        private final String key;
        private final long count;

        private Entry(String key, long count)
        {
            this.key = key;
            this.count = count;
        }

        public String getKey()
        {
            return key;
        }

        public long getCount()
        {
            return count;
        }

        @SuppressWarnings("unchecked")
        private JSONObject asJson()
        {
            JSONObject json = new JSONObject();
            json.put(KEY, key);
            json.put(COUNT, count);
            return json;
        }
    }
}
