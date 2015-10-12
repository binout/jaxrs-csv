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

interface AnnotationUtils {

    static char separatorChar(Class csvClass) {
        char sep = ';';
        Annotation sepAnnotation = csvClass.getAnnotation(CsvSeparator.class);
        if (sepAnnotation != null) {
            Method method = sepAnnotation.annotationType().getDeclaredMethods()[0];
            try {
                sep = (char) method.invoke(sepAnnotation, (Object[])null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return sep;
    }
}
