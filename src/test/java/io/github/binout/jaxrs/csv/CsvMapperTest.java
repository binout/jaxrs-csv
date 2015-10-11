/*
 * Copyright 2014 Benoît Prioux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.binout.jaxrs.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvMapperTest {

    static String inputStreamToString(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    CsvMapper mapper = new CsvMapper();
    /*CsvSchema schema = CsvSchema.builder()
            .addColumn("firstName")
            .addColumn("lastName")
            .addColumn("age", CsvSchema.ColumnType.NUMBER)
            .setColumnSeparator(';')
            .build(); */
    CsvSchema schema = mapper.schemaFor(Person.class).withColumnSeparator(';');


    @Test
    public void test_reader() throws IOException {
        byte[] csv = inputStreamToString(Thread.currentThread().getContextClassLoader().getResourceAsStream("persons.csv")).getBytes();
        MappingIterator<Person> iterator = mapper.reader(Person.class).with(schema).readValues(csv);
        Person benoit = iterator.next();
        assertThat(benoit.getFirstName()).isEqualTo("Benoit");
        Person alexis = iterator.next();
        assertThat(alexis.getFirstName()).isEqualTo("Alexis");
    }

    @Test
    public void test_writer() throws IOException {
        Person lucas = new Person();
        lucas.setFirstName("Lucas");
        lucas.setLastName("Prioux");
        lucas.setAge(2);

        Person benoit = new Person();
        benoit.setFirstName("Benoit");
        benoit.setLastName("Prioux");
        benoit.setAge(33);

        assertThat(mapper.writer(schema).writeValueAsString(Arrays.asList(lucas, benoit))).isEqualToIgnoringWhitespace(
                "Lucas;Prioux;2\n" +
                "Benoit;Prioux;33");
    }
}
