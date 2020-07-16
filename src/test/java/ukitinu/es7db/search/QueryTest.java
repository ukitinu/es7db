package ukitinu.es7db.search;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {

    @Test
    public void testClass()
    {
        Query query = new Query()
                .filterExists("mustexist")
                .filterTerm("string_field_1", "value1", "value2","value3")
                .filterTerm("string_field_2", "single_value")
                .filterTerm("bool_field_1", true, true, false)
                .filterTerm("bool_field_2", false)
                .filterTerm("num_field_1", 1, 2, -15)
                .filterTerm("num_field_", 777)
                .filterRange("range_field_1", "10m", RangeParameter.LTE)
                .filterRange("range_field_2", "2km", RangeParameter.LT)
                .filterRange("range_field_3", "15m", RangeParameter.GTE)
                .filterRange("range_field_4", "31cm", RangeParameter.GT)
                .mustMatch("text_field_1", "match this phrase", true)
                .mustMatch("text_field_2", "a word", false)
                .mustNotExists("cannotexist")
                .mustNotGeoCircle("circle_field", new CoordinatePoint(1.0, 0.0), new Distance(30, DistanceUnit.CM))
                .shouldTerm(true, "should_string_1", "some")
                .shouldTerm(true, "should_string_2", "must")
                .shouldTerm(true, "should_string_3", "be")
                .shouldTerm(true, "should_string_4", "satisfied")
                .shouldTerm(false, "should_string_5", "even")
                .shouldTerm(false, "should_string_6", "if")
                .shouldTerm(false, "should_string_7", "negative")
                .minimumShould(4);

        assertDoesNotThrow(query::toQuery);
    }

}