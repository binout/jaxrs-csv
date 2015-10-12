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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;

@Provider
@Consumes("text/csv")
@Produces("text/csv")
public class CsvMessageBodyProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public Object readFrom(Class<Object> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        CsvMapper mapper = new CsvMapper();
        Class csvClass = (Class) (((ParameterizedType) type).getActualTypeArguments())[0];
        CsvSchema schema = CsvSchemaFactory.buildSchema(mapper, csvClass);
        return mapper.reader(csvClass).with(schema).readValues(inputStream).readAll();
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        CsvMapper mapper = new CsvMapper();
        String body = objectClass(o, aClass).map(csvClass -> {
            CsvSchema schema = CsvSchemaFactory.buildSchema(mapper, csvClass);
            try {
                return mapper.writer(schema).writeValueAsString(o);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).orElse("");
        outputStream.write(body.getBytes(StandardCharsets.UTF_8));
    }

    private Optional<Class> objectClass(Object o, Class<?> aClass) {
        Optional<Class> csvClass;
        if (o instanceof Collection) {
            Collection collection = (Collection) o;
            if (collection.isEmpty()) {
                csvClass = Optional.empty();
            } else {
                csvClass = Optional.of(collection.iterator().next().getClass());
            }
        } else {
            csvClass = Optional.of(aClass);
        }
        return csvClass;
    }

}
