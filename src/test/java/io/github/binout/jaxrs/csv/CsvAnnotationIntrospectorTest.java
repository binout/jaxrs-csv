package io.github.binout.jaxrs.csv;

import io.github.binout.jaxrs.csv.app.Dog;
import io.github.binout.jaxrs.csv.app.Person;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvAnnotationIntrospectorTest {

    @Test
    public void testGetSeparatorChar_default_value() throws Exception {
        assertThat(new CsvAnnotationIntrospector(Person.class).separator()).isEqualTo(';');
    }

    @Test
    public void testGetColumns() throws Exception {
        assertThat(new CsvAnnotationIntrospector(Person.class).columns().get()).containsExactly("firstName", "lastName", "age");
    }

    @Test
    public void testGetSeparatorChar_override() throws Exception {
        assertThat(new CsvAnnotationIntrospector(Dog.class).separator()).isEqualTo(',');
    }
}