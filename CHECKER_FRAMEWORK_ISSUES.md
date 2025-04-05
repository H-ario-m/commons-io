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

# Checker Framework Compatibility Issues

This document details the issues encountered when attempting to run the Checker Framework's Nullness Checker on the Apache Commons IO codebase.

## 1. Compatibility Error

When attempting to compile the project with the Checker Framework enabled, we encountered the following error:

```
java.lang.IllegalAccessError: class org.checkerframework.javacutil.AbstractTypeProcessor cannot access class com.sun.tools.javac.processing.JavacProcessingEnvironment (in module jdk.compiler) because module jdk.compiler does not export com.sun.tools.javac.processing to unnamed module
```

This error indicates a compatibility issue between the Checker Framework version 3.49.2 and the JDK being used. The Checker Framework relies on internal JDK APIs that may not be accessible in newer JDK versions due to the module system encapsulation.

## 2. Root Cause Analysis

The error occurs because:

1. The Checker Framework uses internal JDK APIs from the `com.sun.tools.javac` package
2. In JDK 9 and later, these APIs are encapsulated in the `jdk.compiler` module
3. This module doesn't export the required packages to unnamed modules (like our application)

## 3. Attempted Solutions

### 3.1. Add JVM Arguments to Open Module

We attempted to add the following arguments to the Maven compiler plugin:

```xml
<compilerArgs>
  <arg>--add-exports</arg>
  <arg>jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
  <arg>--add-exports</arg>
  <arg>jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
  <arg>--add-exports</arg>
  <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
</compilerArgs>
```

This approach did not resolve the issue completely as there are multiple internal JDK dependencies.

### 3.2. Use an Earlier Checker Framework Version

We tried using Checker Framework version 3.10.0, which is compatible with JDK 8, but this resulted in other dependency conflicts.

### 3.3. Use JDK 8

Running with JDK 8 might be compatible with the Checker Framework, but the Commons IO project is configured to support newer JDK versions with features not available in JDK 8.

## 4. Manual Analysis Approach

Due to these compatibility issues, we opted for a manual analysis approach:

1. We added the Checker Framework qualifier dependency to use the annotations:
   ```xml
   <dependency>
     <groupId>org.checkerframework</groupId>
     <artifactId>checker-qual</artifactId>
     <version>3.49.2</version>
   </dependency>
   ```

2. We manually added nullness annotations to key classes based on:
   - Existing null checks in the code
   - Javadoc documentation of nullness
   - Method behavior and general null handling patterns

3. We documented the nullness issues we found through manual code review

## 5. Recommended Long-Term Solutions

To fully integrate the Checker Framework with Apache Commons IO in the future:

1. **Use compatible versions**: Identify a combination of JDK version and Checker Framework version that are compatible

2. **Run with custom JDK**: Set up a dedicated build profile that uses a compatible JDK specifically for running the nullness checks

3. **Containerized checking**: Run the nullness checking in a Docker container with a compatible environment

4. **Checker Framework enhancement**: Collaborate with the Checker Framework team to update the tool to work with newer JDK versions

## 6. Additional Resources

- [Checker Framework compatibility documentation](https://checkerframework.org/manual/#java-8-9-10)
- [JEP 261: Module System](https://openjdk.java.net/jeps/261) (for understanding Java module system constraints)
- [Maven Compiler Plugin Configuration](https://maven.apache.org/plugins/maven-compiler-plugin/examples/add-compiler-argument.html) 