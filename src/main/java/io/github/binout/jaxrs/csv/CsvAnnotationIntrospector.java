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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

class CsvAnnotationIntrospector {

    private final char sep;
    private final Optional<String[]> columns;
    private final boolean skipFirstDataRow;

    CsvAnnotationIntrospector(Class csvClass) {
        Annotation sepAnnotation = csvClass.getAnnotation(CsvSchema.class);
        if (sepAnnotation != null) {
            sep = defineSeparator(sepAnnotation);
            columns = defineColumns(sepAnnotation);
            skipFirstDataRow = defineSkipFirstDataRow(sepAnnotation);
        } else {
            // default values
            sep = ';';
            columns = Optional.empty();
            skipFirstDataRow = false;
        }
    }

    public char separator() {
        return sep;
    }

    public Optional<String[]> columns() {
        return columns;
    }

    public boolean skipFirstDataRow() {
        return skipFirstDataRow;
    }

    private static char defineSeparator(Annotation sepAnnotation) {
        try {
            Method method = sepAnnotation.annotationType().getDeclaredMethod("separator");
            return  (char) method.invoke(sepAnnotation, (Object[])null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String[]> defineColumns(Annotation orderAnnotation) {
        try {
            Method method = orderAnnotation.annotationType().getDeclaredMethod("columns");
            return Optional.of((String[]) method.invoke(orderAnnotation, (Object[])null));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean defineSkipFirstDataRow(Annotation orderAnnotation) {
        try {
            Method method = orderAnnotation.annotationType().getDeclaredMethod("skipFirstDataRow");
            return (boolean) method.invoke(orderAnnotation, (Object[])null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
