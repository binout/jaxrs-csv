package io.github.binout.jaxrs.csv;

import io.github.binout.jaxrs.csv.app.Dog;
import io.github.binout.jaxrs.csv.app.Person;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationUtilsTest {

    @Test
    public void testGetSeparatorChar_default_value() throws Exception {
        assertThat(AnnotationUtils.separatorChar(Person.class)).isEqualTo(';');
    }

    @Test
    public void testGetSeparatorChar_override() throws Exception {
        assertThat(AnnotationUtils.separatorChar(Dog.class)).isEqualTo(',');
    }
}