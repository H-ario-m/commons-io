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

# Nullness Annotations Guide for Commons IO

This guide provides details on how to apply nullness annotations to the Apache Commons IO codebase. By using nullness annotations, we can catch potential null pointer errors at compile-time rather than runtime.

## 1. Introduction

The [Checker Framework](https://checkerframework.org/) provides pluggable type-checking for Java. Its Nullness Checker helps detect and prevent null pointer exceptions by analyzing code for potential nullness issues.

In Commons IO, we use two primary annotations:
- `@NonNull` - Indicates that a variable, parameter, or return value must not be null
- `@Nullable` - Indicates that a variable, parameter, or return value may be null

## 2. Setup

### Maven Configuration

The project POM is configured with the Checker Framework:

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
  </configuration>
</plugin>
```

Due to JDK compatibility issues, you may need to temporarily comment out the `annotationProcessors` section when building the project:

```xml
<!--
<annotationProcessors>
  <annotationProcessor>org.checkerframework.checker.nullness.NullnessChecker</annotationProcessor>
</annotationProcessors>
-->
```

### JDK Stub Files

To provide nullness information for JDK classes, we use a stub file at `src/main/resources/org/checkerframework/checker/nullness/jdk.astub`.

## 3. Annotation Guidelines

### 3.1 When to use @NonNull

Use `@NonNull` for:

1. **Return values that never return null:**

```java
/**
 * Gets the system temp directory.
 * 
 * @return the system temp directory (never null)
 */
@NonNull
public static File getTempDirectory() {
    return new File(getTempDirectoryPath());
}
```

2. **Parameters that must not be null:**

```java
/**
 * Deletes a file.
 * 
 * @param file the file to delete, must not be null
 * @return true if the file was deleted
 */
public static boolean deleteQuietly(@NonNull final File file) {
    // implementation
}
```

### 3.2 When to use @Nullable

Use `@Nullable` for:

1. **Return values that might be null:**

```java
/**
 * Gets a parent file.
 * 
 * @param file the file to query
 * @return the parent file, or null if has no parent
 */
@Nullable
private static File getParentFile(final File file) {
    return file == null ? null : file.getParentFile();
}
```

2. **Parameters that can be null:**

```java
/**
 * Tests whether the file exists.
 * 
 * @param file the file to test, may be null
 * @return true if the file exists, false if not or null
 */
public static boolean exists(@Nullable final File file) {
    return file != null && file.exists();
}
```

### 3.3 Default Nullness

When a parameter or return value lacks an explicit nullness annotation:

- For parameters: Assume `@NonNull` unless Javadoc specifically mentions it can be null
- For return values: Assume `@NonNull` unless Javadoc specifically mentions it can be null

## 4. Annotation Process

### 4.1 Prioritized Classes

Focus on annotating these key classes first:

1. **FileUtils.java** - Core file operations
2. **IOUtils.java** - Core I/O stream operations 
3. **PathUtils.java** - Path manipulations

### 4.2 Annotation Workflow

1. **Add imports:**
   ```java
   import org.checkerframework.checker.nullness.qual.NonNull;
   import org.checkerframework.checker.nullness.qual.Nullable;
   ```

2. **Identify nullness from code patterns:**
   - Methods that check `parameter == null` likely accept `@Nullable` parameters
   - Methods using `Objects.requireNonNull()` require `@NonNull` parameters
   - Methods with names like `getXXX()` typically return `@NonNull` values
   - Methods with Javadoc saying "returns null if..." should have `@Nullable` return types

3. **Annotate method signatures:**
   - Start with return types
   - Then annotate parameters
   - Document in Javadoc

4. **Run scripts/apply-nullness-annotations.bat (or .sh)** to identify candidates for annotation.

### 4.3 Scripts

The project includes scripts to help identify nullness patterns:

- `scripts/apply-nullness-annotations.sh` - For Unix/Linux/macOS
- `scripts/apply-nullness-annotations.bat` - For Windows

These scripts find:
- Methods likely to return non-null values
- Parameters that have null checks

## 5. Special Cases

### 5.1 Resource Variables

For resource initialization patterns:

```java
InputStream in = null;
try {
    in = new FileInputStream(file);
    // Use in...
} finally {
    IOUtils.closeQuietly(in);
}
```

The Nullness Checker may flag warnings about `in` being potentially null. Use `@SuppressWarnings`:

```java
@SuppressWarnings("nullness:initialization.field.uninitialized")
private void copyStreams(File srcFile, File destFile) {
    // Implementation with resource pattern
}
```

Better yet, refactor to use try-with-resources when possible:

```java
try (InputStream in = new FileInputStream(file)) {
    // Use in...
}
```

### 5.2 Conditional Nullness

For methods where nullness depends on a condition, use more advanced annotations:

```java
@EnsuresNonNullIf(expression = "getFile()", result = true)
public boolean fileExists() {
    return getFile() != null && getFile().exists();
}
```

## 6. Troubleshooting

### 6.1 Compatibility Issues

If you encounter compatibility issues running the Checker Framework:

1. Comment out the annotation processor temporarily:
   ```xml
   <!--
   <annotationProcessors>
     <annotationProcessor>org.checkerframework.checker.nullness.NullnessChecker</annotationProcessor>
   </annotationProcessors>
   -->
   ```

2. Try using JDK 8 for compatibility

3. Use module system arguments (for JDK 9+):
   ```
   --add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
   --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
   --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
   ```

### 6.2 False Positives

Common sources of false positives:

1. **Resource initialization patterns** - Use `@SuppressWarnings` or refactor
2. **Complex flow logic** - Consider adding explicit null checks
3. **Optional returns** - Consider using `java.util.Optional` instead

## 7. Testing

After adding annotations:

1. Run unit tests to ensure functionality is not affected
2. Create a test class like `NullnessAnnotationTest` to verify annotation behavior

## 8. Future Work

As Commons IO evolves:

1. Add annotations incrementally with each release
2. Focus on public API first, then extend to implementation details
3. Consider using Java's `java.util.Optional` for new APIs that may not return values
4. Document nullness expectations in Javadoc consistently

## 9. Resources

- [Checker Framework Manual](https://checkerframework.org/manual/)
- [Checker Framework Tutorial](https://checkerframework.org/tutorial/)
- [JDK Annotations](https://github.com/typetools/jdk) 