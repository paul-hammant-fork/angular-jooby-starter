package de.protubero.ajs;

import org.junit.Test;

import static de.protubero.ajs.Helpers.namedPerson;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public abstract class PersonStoreTest {

    protected abstract PersonStore makePersonStore();

    @Test
    public void shouldBeAbleToStoreKnownPerson() {
        PersonStore ps = makePersonStore();
        Person p = ps.insert(namedPerson("Fred"));
        assertThat(p.getId(), equalTo(1));

        // access directly
        assertTrue(ps.selectById(1).isPresent());
        assertThat(ps.selectById(1).get().getName(), equalTo("Fred"));

        // access again, via selectAll
        assertNotNull(ps.selectAll().get(0));
        assertEquals(ps.selectAll().size(), 1);
        assertThat(ps.selectAll().get(0).getName(), equalTo("Fred"));
    }


    @Test
    public void shouldBeAbleToDeleteKnownPerson() {
        PersonStore ps = makePersonStore();
        Person p = ps.insert(namedPerson("Fred"));
        assertThat(p.getId(), equalTo(1));
        assertThat(p.getName(), equalTo("Fred"));
        assertTrue(ps.selectById(1).isPresent());
        ps.delete(1);
        assertFalse(ps.selectById(1).isPresent());
    }

}
