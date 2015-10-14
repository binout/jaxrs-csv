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

import com.github.kevinsawicki.http.HttpRequest;
import io.github.binout.jaxrs.csv.app.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

public class IntegrationTest extends Arquillian {

    static final String CSV_PERSONS = "Lucas;Prioux;2\n" + "Benoit;Prioux;33\n";
    static final String CSV_DOGS = "labrador,medor\n" + "setter,izidor\n";

    @Deployment
    public static WebArchive createDeployment() {
        File[] jacksonCsv = Maven.resolver().resolve(
                "com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.4.1").withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
                // Framework
                .addClass(CsvMessageBodyProvider.class)
                .addClass(CsvSchema.class)
                .addClass(CsvSchemaFactory.class)
                .addClass(CsvAnnotationIntrospector.class)
                .addAsLibraries(jacksonCsv)
                 // Test
                .addClass(JaxRsApp.class)
                .addClass(Person.class)
                .addClass(PersonResource.class)
                .addClass(Dog.class)
                .addClass(DogResource.class);
    }

    @ArquillianResource
    private URL baseURL;

    @Test
    @RunAsClient
    public void getAndPostAndGet() {
        String personsUrl = baseURL + "rest/persons";

        // First GET : empty repository
        HttpRequest getEmpty = HttpRequest.get(personsUrl).accept("text/csv");
        Assert.assertEquals(getEmpty.code(), 200);
        Assert.assertEquals(getEmpty.body(), "");

        // POST csv body
        HttpRequest post = HttpRequest.post(personsUrl).contentType("text/csv").send(CSV_PERSONS);
        Assert.assertEquals(post.code(), 200);

        // Znd GET : retrieve csv
        HttpRequest get = HttpRequest.get(personsUrl).accept("text/csv");
        Assert.assertEquals(get.code(), 200);
        Assert.assertEquals(get.body(), CSV_PERSONS);
    }

    @Test
    @RunAsClient
    public void getWithCsvSeparator() {
        String personsUrl = baseURL + "rest/dogs";

        HttpRequest get = HttpRequest.get(personsUrl).accept("text/csv");
        Assert.assertEquals(get.code(), 200);
        Assert.assertEquals(get.body(), CSV_DOGS);
    }
}
