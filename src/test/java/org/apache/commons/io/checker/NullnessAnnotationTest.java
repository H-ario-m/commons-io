/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.checker;

import java.io.File;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * This class demonstrates nullness annotations and verifies that they compile correctly.
 */
public class NullnessAnnotationTest {
    
    /**
     * Method with a non-null return value and non-null parameter.
     * 
     * @param input must be non-null
     * @return never null
     */
    @NonNull
    public String nonNullMethod(@NonNull String input) {
        return input.toUpperCase();
    }
    
    /**
     * Method that accepts null parameter.
     * 
     * @param input may be null
     * @return the input or a default value
     */
    @NonNull
    public String nullableParamMethod(@Nullable String input) {
        return input != null ? input : "default";
    }
    
    /**
     * Method that may return null.
     * 
     * @param input the input value
     * @return null if input is empty, otherwise the input
     */
    @Nullable
    public String mayReturnNullMethod(@NonNull String input) {
        return input.isEmpty() ? null : input;
    }
    
    /**
     * Test that nullness annotations compile correctly.
     */
    @Test
    public void testNullnessAnnotations() {
        // Test non-null method
        assertEquals("HELLO", nonNullMethod("hello"));
        
        // Test nullable parameter
        assertEquals("hello", nullableParamMethod("hello"));
        assertEquals("default", nullableParamMethod(null));
        
        // Test nullable return
        assertEquals("hello", mayReturnNullMethod("hello"));
        assertNull(mayReturnNullMethod(""));
    }
    
    /**
     * Example of similar methods to those in FileUtils.
     */
    @Test
    public void testFileUtils() {
        // Non-null return value
        @NonNull File tempDir = getTempDirectory();
        assertTrue(tempDir.exists());
        
        // Nullable parameter
        assertTrue(exists(new File(".")));
        assertFalse(exists(null));
    }
    
    /**
     * Gets a file representing the system temporary directory.
     *
     * @return the system temporary directory.
     */
    @NonNull
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    /**
     * Tests whether the file exists.
     *
     * @param file the file to test, may be {@code null}
     * @return {@code true} if the file exists, {@code false} if the file does not exist or if the file is {@code null}
     */
    private boolean exists(@Nullable final File file) {
        return file != null && file.exists();
    }
} 