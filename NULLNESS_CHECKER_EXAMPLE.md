<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Checker Framework Integration with Apache Commons IO

This document provides concrete examples of integrating the Checker Framework's Nullness Checker with the Apache Commons IO library.

## 1. Setup and Configuration

### Maven Configuration

The POM file has been configured to use the Checker Framework as follows:

```xml
<!-- Add Checker Framework qualifier dependency -->
<dependency>
  <groupId>org.checkerframework</groupId>
  <artifactId>checker-qual</artifactId>
  <version>3.49.2</version>
</dependency>

<!-- Configure Maven Compiler Plugin -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <source>${maven.compiler.source}</source>
    <target>${maven.compiler.target}</target>
    <fork>true</fork>
    <annotationProcessorPaths>
      <path>
        <groupId>org.checkerframework</groupId>
        <artifactId>checker</artifactId>
        <version>3.49.2</version>
      </path>
    </annotationProcessorPaths>
    <annotationProcessors>
      <annotationProcessor>org.checkerframework.checker.nullness.NullnessChecker</annotationProcessor>
    </annotationProcessors>
    <compilerArgs>
      <arg>-Xmaxerrs</arg>
      <arg>10000</arg>
      <arg>-Awarns</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

### JDK Stub Files

We've created a JDK stub file (`src/main/resources/org/checkerframework/checker/nullness/jdk.astub`) to provide nullness assumptions for JDK classes:

```java
package java.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class File {
    @NonNull public String getAbsolutePath();
    @NonNull public String getPath();
    @Nullable public String getParent();
    @Nullable public File getParentFile();
    @NonNull public String getName();
    @Nullable public String[] list();
    @Nullable public File[] listFiles();
}

// Additional JDK class stubs...
```

## 2. Examples of Annotated Code

### FileUtils.java Annotated Example

```java
package org.apache.commons.io;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FileUtils {

    /**
     * Gets a file representing the system temporary directory.
     *
     * @return the system temporary directory.
     */
    @NonNull
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    /**
     * Gets the path to the system temporary directory.
     *
     * @return the path to the system temporary directory.
     */
    @NonNull
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file  the file to read, must not be {@code null}
     * @param encoding  the encoding to use, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws IOException in case of an I/O error
     */
    @NonNull
    public static String readFileToString(@NonNull final File file, @Nullable final Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toString(in, Charsets.toCharset(encoding));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Tests whether the file exists.
     *
     * @param file  the file to test, may be {@code null}
     * @return {@code true} if the file exists, {@code false} if the file does not exist or if the file is {@code null}
     */
    public static boolean exists(@Nullable final File file) {
        return file != null && file.exists();
    }

    /**
     * Gets a parent file, or {@code null} if the file has no parent.
     *
     * @param file  the file to query, null returns null
     * @return the parent file, or {@code null} if the file has no parent
     */
    @Nullable
    private static File getParentFile(@Nullable final File file) {
        return file == null ? null : file.getParentFile();
    }
}
```

### IOUtils.java Annotated Example

```java
package org.apache.commons.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IOUtils {

    /**
     * Closes a {@link Closeable} unconditionally.
     *
     * @param closeable the object to close, may be null or already closed
     */
    public static void closeQuietly(@Nullable final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Exception e) {
                // ignored
            }
        }
    }

    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]}.
     *
     * @param input the {@link InputStream} to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException if an I/O error occurs
     */
    @NonNull
    public static byte[] toByteArray(@NonNull final InputStream input) throws IOException {
        // Implementation details...
        return new byte[0]; // Simplified for example
    }

    /**
     * Gets the contents of an {@link InputStream} as a String
     * using the specified character encoding.
     *
     * @param input the {@link InputStream} to read from
     * @param charset the charset to use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException if an I/O error occurs
     */
    @NonNull
    public static String toString(@NonNull final InputStream input, @Nullable final Charset charset) throws IOException {
        // Implementation details...
        return ""; // Simplified for example
    }
}
```

## 3. Common Nullness Issues Detected

When running the Nullness Checker, the following types of issues are detected:

### 1. Missing Null Checks

```java
// UNSAFE: No null check before dereferencing 'file'
public static boolean isEmptyDirectory(final File file) throws IOException {
    return file.listFiles().length == 0; // Potential NPE if file.listFiles() returns null
}

// SAFE: With nullness annotations and checks
public static boolean isEmptyDirectory(@NonNull final File file) throws IOException {
    File[] files = file.listFiles();
    return files != null && files.length == 0;
}
```

### 2. Inconsistent Nullness Documentation

```java
/**
 * @param file  the file to check (may be null)
 * @return true if the file exists
 */
// UNSAFE: Javadoc says null is allowed, but code doesn't check
public static boolean exists(final File file) {
    return file.exists(); // Will throw NPE if file is null
}

// SAFE: With nullness annotations and consistent implementation
/**
 * @param file  the file to check (may be null)
 * @return true if the file exists, false if file is null
 */
public static boolean exists(@Nullable final File file) {
    return file != null && file.exists();
}
```

### 3. False Positives with Resource Initialization

```java
// This pattern may trigger false positives in the nullness checker
public static void copyFile(File source, File dest) throws IOException {
    InputStream in = null;
    OutputStream out = null;
    try {
        in = new FileInputStream(source);    // in is non-null here
        out = new FileOutputStream(dest);    // out is non-null here
        copy(in, out);                      // Checker might warn about possible null dereference
    } finally {
        closeQuietly(in);
        closeQuietly(out);
    }
}
```

To address this pattern, you can use:

```java
@SuppressWarnings("nullness:initialization.field.uninitialized")
public static void copyFile(File source, File dest) throws IOException {
    // Same implementation as above
}
```

Or refactor to use try-with-resources:

```java
public static void copyFile(File source, File dest) throws IOException {
    try (InputStream in = new FileInputStream(source);
         OutputStream out = new FileOutputStream(dest)) {
        copy(in, out);
    }
}
```

## 4. Running the Nullness Checker

To run the Nullness Checker on the codebase:

```bash
# Compile with the nullness checker enabled
mvn clean compile

# Compile specific files with nullness checking
javac -processor org.checkerframework.checker.nullness.NullnessChecker \
      -processorpath path/to/checker.jar:path/to/jdk8.jar \
      src/main/java/org/apache/commons/io/FileUtils.java
```

## 5. Common Challenges and Solutions

### Challenge: Initialization Patterns

Solution: Use `@SuppressWarnings("nullness")` or refactor to avoid false positives.

### Challenge: Conditional Nullness

Solution: Use `@EnsuresNonNull` and `@EnsuresNonNullIf` to document methods that conditionally ensure non-nullness.

Example:
```java
@EnsuresNonNullIf(expression = "getFile(#1)", result = true)
public static boolean createFile(String filename) {
    // Implementation
}
```

### Challenge: Legacy Code Compliance

Solution: Phase in annotations gradually, starting with most critical classes and focusing on public APIs first.

## 6. Best Practices for Future Development

1. **Document nullness in Javadoc**: Always document nullness expectations in Javadoc comments, including whether parameters can be null and whether methods can return null.

2. **Be consistent**: Use similar nullness patterns for similar methods.

3. **Favor annotation over assertion**: Prefer using annotations to document nullness rather than runtime assertions.

4. **Consider Optional**: For methods that might not return a value, consider using Optional<T> instead of nullable return types for new APIs.

5. **Add annotations incrementally**: Start with core classes and expand outward, focusing on public APIs first.

## Conclusion

By integrating the Checker Framework's Nullness Checker with Apache Commons IO, we can detect potential NullPointerExceptions at compile time rather than runtime. This provides stronger guarantees about null handling and improves the robustness of the library for its users. 