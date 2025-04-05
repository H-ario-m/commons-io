#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script helps apply nullness annotations to key classes in Commons IO
# It uses grep to find methods that should be annotated with @NonNull or @Nullable

echo "Apply Nullness Annotations Script"
echo "=================================="

# Define base directory
BASE_DIR="$(pwd)"
SRC_DIR="$BASE_DIR/src/main/java/org/apache/commons/io"

# Check if we're in the correct directory
if [ ! -d "$SRC_DIR" ]; then
    echo "Error: Script must be run from the root of the commons-io project"
    exit 1
fi

# Create backup directory
BACKUP_DIR="$BASE_DIR/nullness-backups"
mkdir -p "$BACKUP_DIR"

# Function to add import statements
add_imports() {
    local file="$1"
    # Check if imports already exist
    if ! grep -q "import org.checkerframework.checker.nullness.qual.NonNull;" "$file"; then
        # Add after the last import statement
        sed -i '/^import/!b;:a;n;/^import/ba;i import org.checkerframework.checker.nullness.qual.NonNull;' "$file"
    fi
    if ! grep -q "import org.checkerframework.checker.nullness.qual.Nullable;" "$file"; then
        # Add after the NonNull import or after the last import
        if grep -q "import org.checkerframework.checker.nullness.qual.NonNull;" "$file"; then
            sed -i '/import org.checkerframework.checker.nullness.qual.NonNull;/a import org.checkerframework.checker.nullness.qual.Nullable;' "$file"
        else
            sed -i '/^import/!b;:a;n;/^import/ba;i import org.checkerframework.checker.nullness.qual.Nullable;' "$file"
        fi
    fi
}

# Function to process a file
process_file() {
    local file="$1"
    local basename=$(basename "$file")
    
    echo "Processing $basename..."
    
    # Create backup
    cp "$file" "$BACKUP_DIR/$basename.bak"
    
    # Add imports
    add_imports "$file"
    
    # Find methods likely to return non-null values
    echo "  Finding @NonNull return methods..."
    grep -E "public static [A-Za-z0-9<>]+ get[A-Z]" "$file" | grep -v "@Nullable" | grep -v "Optional<" > /tmp/nonnull_methods.txt
    
    # Find methods with null-checking parameters
    echo "  Finding @Nullable parameters..."
    grep -E "if *\([a-zA-Z0-9]+ *== *null\)" "$file" > /tmp/nullable_params.txt
    
    # Show summary
    echo "  Found $(wc -l < /tmp/nonnull_methods.txt) potential @NonNull return methods"
    echo "  Found $(wc -l < /tmp/nullable_params.txt) potential @Nullable parameters"
    
    echo "  See detailed findings in /tmp/nonnull_methods.txt and /tmp/nullable_params.txt"
    echo "  Remember to manually review before applying annotations!"
}

# Process key files
KEY_FILES=(
    "$SRC_DIR/FileUtils.java"
    "$SRC_DIR/IOUtils.java"
    "$SRC_DIR/PathUtils.java"
)

for file in "${KEY_FILES[@]}"; do
    if [ -f "$file" ]; then
        process_file "$file"
    else
        echo "Warning: $file not found"
    fi
done

echo ""
echo "Next steps:"
echo "1. Review the potential annotation locations"
echo "2. Manually add @NonNull and @Nullable annotations to methods and parameters"
echo "3. Run tests to verify the annotations don't cause issues"
echo ""
echo "Note: Due to JDK compatibility issues, the Checker Framework processor"
echo "cannot be run directly. Use manual annotation and code review instead." 