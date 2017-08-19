package de.protubero.ajs;

import org.junit.Test;

import static de.protubero.ajs.Helpers.namedPerson;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class JSimpleDBPersonStoreImplTest extends PersonStoreTest {

    protected PersonStore makePersonStore() {
        return new JSimpleDBPersonStoreImpl();
    }

}
