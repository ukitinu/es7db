package ukitinu.es7db.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public final class RestBodyChecker
{
    private RestBodyChecker()
    {
        throw new IllegalStateException("Utils class");
    }

    static void checkInput(Map<String, Object> input,
                           RestFieldSet required,
                           RestFieldSet optional)
    {
        Collection<String> missing = new HashSet<>();
        Collection<String> badType = new HashSet<>();
        checkRequired(input, required, missing, badType);
        checkOptional(input, optional, badType);
        sendErrorResponse(missing, badType);
    }

    public static void retain(Map<String, Object> input, RestFieldSet... retainedSets)
    {
        if (retainedSets == null || retainedSets.length == 0) {
            input.clear();
        } else {
            RestFieldSet retained = new RestFieldSet().addAll(retainedSets);
            Collection<String> toRetain = retained
                    .getSet()
                    .stream()
                    .map(RestField::getName)
                    .collect(Collectors.toSet());

            Collection<String> toRemove = new ArrayList<>();
            for (String key : input.keySet()) {
                if (!toRetain.contains(key)) toRemove.add(key);
            }
            for(String key : toRemove) input.remove(key);
        }
    }

    private static void checkRequired(Map<String, Object> input,
                                      RestFieldSet required,
                                      Collection<? super String> missing,
                                      Collection<? super String> badType)
    {
        for (RestField<?> field : required.getSet()) {
            String name = field.getName();
            if (!input.containsKey(name)) {
                missing.add(field.toString());
            } else if (field.isWrongType(input.get(name))) {
                badType.add(field.toString());
            }
        }
    }

    private static void checkOptional(Map<String, Object> input,
                                      RestFieldSet optional,
                                      Collection<? super String> badType)
    {
        for (RestField<?> field : optional.getSet()) {
            String name = field.getName();
            if (input.containsKey(name)) {
                if (field.isWrongType(input.get(name))) {
                    badType.add(field.toString(true));
                }
            }
        }
    }

    private static void sendErrorResponse(Collection<String> missing,
                                          Collection<String> badType)
    {
        String missingFields = missing.isEmpty() ? "" : String.join(", ", missing);
        String badTypeFields = badType.isEmpty() ? "" : String.join(", ", badType);
        if (!missingFields.isEmpty() || !badTypeFields.isEmpty()) {
            String absent = missingFields.isEmpty() ? "" : "Field(s) missing: " + missingFields;
            String wrong = badTypeFields.isEmpty() ? "" : "Wrong type(s): " + badTypeFields;
            String response = absent.isEmpty() ? wrong : wrong.isEmpty() ? absent : absent + System.lineSeparator() + wrong;
            throw new IllegalArgumentException(response);
        }
    }

}
