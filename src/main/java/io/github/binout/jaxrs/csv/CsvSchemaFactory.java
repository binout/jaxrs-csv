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

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.util.Optional;

interface CsvSchemaFactory {

    static CsvSchema buildSchema(CsvMapper mapper, Class csvClass) {
        CsvAnnotationIntrospector introspector = new CsvAnnotationIntrospector(csvClass);
        char separatorChar = introspector.separator();
        Optional<String[]> columns = introspector.columns();

        CsvSchema csvSchema = mapper.schemaFor(csvClass).withColumnSeparator(separatorChar);
        if (columns.isPresent()) {
            // Rebuild columns to take account of order
            CsvSchema.Builder builder = csvSchema.rebuild().clearColumns();
            for (String column : columns.get()) {
                CsvSchema.Column oldColumn = csvSchema.column(column);
                builder.addColumn(column, oldColumn.getType());
            }
            csvSchema = builder.build();
        }

        return csvSchema;
    }
}
