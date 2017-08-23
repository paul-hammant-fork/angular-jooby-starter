package de.protubero.ajs;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jooby.test.JoobyRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static io.restassured.RestAssured.get;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

public class AppIntegrationTest {

    private static final AppOverriddenForTests APP_OVERRIDDEN_FOR_TESTS = new AppOverriddenForTests();

    @ClassRule
    public static JoobyRule app = new JoobyRule(APP_OVERRIDDEN_FOR_TESTS);

    @Test
    public void integrationTest() {

        // App launches with starter data - ignore that for tests
        Person i1 = APP_OVERRIDDEN_FOR_TESTS.personStore.insert(Helpers.namedPerson("998877"));
        Person i2 = APP_OVERRIDDEN_FOR_TESTS.personStore.insert(Helpers.namedPerson("887766"));

        get("/api/persons/")
                .then()
                .assertThat()
                .body(isJsonArrayOrPeopleWithNames("998877", "887766"))
                .statusCode(200)
                .contentType(containsString("application/json;charset=UTF-8"));

        APP_OVERRIDDEN_FOR_TESTS.personStore.delete(i1.getId());
        APP_OVERRIDDEN_FOR_TESTS.personStore.delete(i2.getId());

    }

    private Matcher<String> isJsonArrayOrPeopleWithNames(final String... uniqueStrings) {
        return new BaseMatcher<String>() {

            @Override
            public boolean matches(Object o) {
                String content = (String) o;

                Person[] people;

                try {
                    // Well it's roughly a Json array of people in
                    people = new com.fasterxml.jackson.databind.ObjectMapper().readValue(content, Person[].class);
                } catch (IOException e) {
                    return false;
                }

                for (String uniqueString : uniqueStrings) {
                    boolean hasIt = false;
                    for (Person person : people) {
                        if (person.getName().equals(uniqueString)) {
                            hasIt = true;
                            break;
                        }
                    }
                    if (!hasIt) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("People array should have contained people with names: " + String.join(", ", asList(uniqueStrings)));

            }

        };
    }
}
