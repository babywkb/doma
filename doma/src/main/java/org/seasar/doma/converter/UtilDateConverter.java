/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.converter;


/**
 * @author taedium
 * 
 */
public class UtilDateConverter implements Converter<java.util.Date> {

    protected static String DEFAULT_PATTERN = "yyyy-MM-dd";

    protected final ConversionSupport conversionSupport = new ConversionSupport();

    @Override
    public java.util.Date convert(Object value, String pattern) {
        if (value == null) {
            return null;
        }
        if (java.util.Date.class.isInstance(value)) {
            return java.util.Date.class.cast(value);
        }
        if (String.class.isInstance(value)) {
            return parse(String.class.cast(value), pattern);
        }
        throw new UnsupportedConversionException(value.getClass().getName(),
                java.util.Date.class.getName(), value);
    }

    protected java.util.Date parse(String value, String pattern) {
        String p = pattern != null ? pattern : DEFAULT_PATTERN;
        return conversionSupport.parseToDate(value, p);
    }

}