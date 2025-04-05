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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * This demo application demonstrates how the Checker Framework's Nullness Checker 
 * helps prevent errors when using Apache Commons IO.
 * 
 * <p>This file contains examples of code that would cause compile-time errors
 * if the nullness checker is enabled. These examples are commented out to allow
 * compilation, but would be caught by the Checker Framework.</p>
 * 
 * <p>Run with: 
 * {@code javac -processor org.checkerframework.checker.nullness.NullnessChecker NullnessSafetyDemo.java}</p>
 */
public class NullnessSafetyDemo {

    /**
     * Shows examples of nullness errors that would be caught at compile time.
     * 
     * @param args command line arguments (not used)
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        demonstrateNonNullParameters();
        demonstrateNullableParameters();
        demonstrateNonNullReturns();
        demonstrateNullableReturns();
    }
    
    /**
     * Demonstrates how methods with @NonNull parameters reject null inputs.
     */
    private static void demonstrateNonNullParameters() {
        System.out.println("=== @NonNull Parameters ===");
        
        // Safe: passing non-null to a method requiring non-null parameter
        File file = new File("example.txt");
        
        // This would compile successfully with nullness checking
        FileUtils.getTempDirectoryPath();
        
        // Unsafe: passing null to method requiring non-null parameter
        // Uncomment to see compile-time error:
        // FileUtils.readFileToString(null, StandardCharsets.UTF_8);  // ERROR: incompatible types
        
        System.out.println("If using the Nullness Checker, passing null to readFileToString() would cause a compile-time error");
    }
    
    /**
     * Demonstrates how methods with @Nullable parameters accept null inputs.
     */
    private static void demonstrateNullableParameters() throws IOException {
        System.out.println("=== @Nullable Parameters ===");
        
        // Safe: passing null to a method accepting nullable parameter
        // This would compile successfully with nullness checking
        File file = new File("example.txt");
        FileUtils.readFileToString(file, (String) null);
        
        // Also safe for documented nullable parameters
        InputStream nullStream = null;
        IOUtils.closeQuietly(nullStream);
        
        System.out.println("Passing null for the encoding parameter is valid and detected as safe by the Nullness Checker");
    }
    
    /**
     * Demonstrates how methods with @NonNull return types provide non-null guarantees.
     */
    private static void demonstrateNonNullReturns() {
        System.out.println("=== @NonNull Returns ===");
        
        // Safe: using return value from method guaranteed to return non-null
        File tempDir = FileUtils.getTempDirectory();
        String path = tempDir.getPath();  // No null check needed
        
        // This would compile successfully with nullness checking
        System.out.println("Temp directory: " + path);
    }
    
    /**
     * Demonstrates how methods with @Nullable return types require null checking.
     */
    private static void demonstrateNullableReturns() {
        System.out.println("=== @Nullable Returns ===");
        
        // Example method that might return null
        File file = new File("/");  // Root directory
        File parent = file.getParentFile();  // Might be null
        
        // Unsafe: dereferencing potentially null return value
        // Uncomment to see compile-time error:
        // String parentPath = parent.getPath();  // ERROR: dereference of possibly-null reference
        
        // Safe: checking for null before using
        // This would compile successfully with nullness checking
        if (parent != null) {
            String parentPath = parent.getPath();
            System.out.println("Parent path: " + parentPath);
        } else {
            System.out.println("Parent is null (root directory)");
        }
        
        System.out.println("The Nullness Checker would require a null check before using the parent File");
    }
} 