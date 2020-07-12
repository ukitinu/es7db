package ukitinu.es7db.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Object> asMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(LIST, entryList.stream().map(Entry::asMap).collect(Collectors.toList()));
        if (otherCount != -1L) {
            map.put(OTHER_COUNT, otherCount);
        }
        return map;
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

        private Map<String, Object> asMap()
        {
            Map<String, Object> map = new HashMap<>();
            map.put(KEY, key);
            map.put(COUNT, count);
            return map;
        }
    }
}
