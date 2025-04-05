# Implementation Plan for Nullness Annotations in Apache Commons IO

This document outlines a phased approach to fully annotate Apache Commons IO with nullness annotations, making the codebase safer and more self-documenting.

## Phase 1: Setup and Infrastructure

1. **Add Checker Framework Dependencies**
   - Add the Checker Framework qualifiers dependency to the project
   - Configure Maven to run the Nullness Checker during compilation
   - Create a stub file for JDK classes that lack nullness annotations

2. **Create Guidelines**
   - Document annotation standards for the project
   - Establish conventions for handling nullable values
   - Define how to document nullness in Javadoc comments

3. **Set Up CI Pipeline Extensions**
   - Add a verification step to CI that ensures all new code includes nullness annotations
   - Configure build to warn on nullness errors (not fail initially)

## Phase 2: Core Classes Annotation

1. **Annotate Key Utility Classes**
   - FileUtils.java
   - IOUtils.java
   - PathUtils.java

2. **Annotate Input Handling Classes**
   - Input stream and reader classes in the `input` package
   - Filtering classes

3. **Annotate Output Handling Classes**
   - Output stream and writer classes in the `output` package

## Phase 3: API Surface Annotation

1. **Public API First Approach**
   - Annotate all public method signatures
   - Focus on return types first (to ensure users know what to expect)
   - Then annotate parameters (to ensure users know what they can pass)

2. **Update Documentation**
   - Ensure Javadoc is consistent with annotations
   - Add examples that demonstrate null handling

## Phase 4: Implementation Details Annotation

1. **Private Methods and Fields**
   - Annotate private and protected methods
   - Annotate fields with appropriate nullness qualifiers

2. **Test Code Annotation**
   - Annotate test classes to ensure tests reflect correct nullness expectations
   - Add tests that specifically verify null handling behavior

## Phase 5: Analysis and Refinement

1. **Run Full Nullness Analysis**
   - Enable strict checking and fix all warnings
   - Document legitimate suppressions

2. **Performance Impact Assessment**
   - Measure any runtime performance impact
   - Optimize runtime checks if necessary

3. **API Review**
   - Review for consistency in null handling across similar methods
   - Consider API improvements for methods with inconsistent null handling

## Phase 6: Release and Documentation

1. **Update Release Notes**
   - Document the nullness verification capabilities
   - Highlight any changes in behavior related to null handling

2. **User Documentation**
   - Update user guide with information about nullness guarantees
   - Provide migration guide for users who may be affected by stricter null checking

3. **Contribution Guidelines**
   - Update contribution guidelines to include nullness annotation requirements

## Timeline

- **Phase 1**: 1-2 weeks
- **Phase 2**: 2-3 weeks
- **Phase 3**: 3-4 weeks
- **Phase 4**: 2-3 weeks
- **Phase 5**: 1-2 weeks
- **Phase 6**: 1 week

Total estimated time: 10-15 weeks

## Metrics to Track

1. **Coverage**
   - Percentage of classes with nullness annotations
   - Percentage of public API with nullness annotations

2. **Quality**
   - Number of nullness-related warnings
   - Number of legitimate suppressions

3. **User Impact**
   - Number of backward compatibility issues identified
   - User feedback on annotation clarity

## Conclusion

By following this phased approach, Apache Commons IO can gradually incorporate nullness annotations without disrupting existing users. This will improve code quality, make the API more self-documenting, and prevent NullPointerExceptions for users of the library. 