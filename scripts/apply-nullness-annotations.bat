@echo off
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements.  See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance with
REM the License.  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

REM This script helps identify potential locations for nullness annotations in Commons IO
REM It's a Windows version of the apply-nullness-annotations.sh script

echo Apply Nullness Annotations Script
echo ==================================

REM Define base directory
set BASE_DIR=%CD%
set SRC_DIR=%BASE_DIR%\src\main\java\org\apache\commons\io

REM Check if we're in the correct directory
if not exist "%SRC_DIR%" (
    echo Error: Script must be run from the root of the commons-io project
    exit /b 1
)

REM Create backup directory
set BACKUP_DIR=%BASE_DIR%\nullness-backups
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo Processing FileUtils.java...
copy "%SRC_DIR%\FileUtils.java" "%BACKUP_DIR%\FileUtils.java.bak"
findstr /n "public static .* get[A-Z]" "%SRC_DIR%\FileUtils.java" > "%TEMP%\nonnull_methods_FileUtils.txt"
findstr /n "if .* == null" "%SRC_DIR%\FileUtils.java" > "%TEMP%\nullable_params_FileUtils.txt"
echo   See potential @NonNull return methods in %TEMP%\nonnull_methods_FileUtils.txt
echo   See potential @Nullable parameters in %TEMP%\nullable_params_FileUtils.txt

echo Processing IOUtils.java...
copy "%SRC_DIR%\IOUtils.java" "%BACKUP_DIR%\IOUtils.java.bak"
findstr /n "public static .* get[A-Z]" "%SRC_DIR%\IOUtils.java" > "%TEMP%\nonnull_methods_IOUtils.txt"
findstr /n "if .* == null" "%SRC_DIR%\IOUtils.java" > "%TEMP%\nullable_params_IOUtils.txt"
echo   See potential @NonNull return methods in %TEMP%\nonnull_methods_IOUtils.txt
echo   See potential @Nullable parameters in %TEMP%\nullable_params_IOUtils.txt

echo Processing PathUtils.java...
copy "%SRC_DIR%\PathUtils.java" "%BACKUP_DIR%\PathUtils.java.bak"
findstr /n "public static .* get[A-Z]" "%SRC_DIR%\PathUtils.java" > "%TEMP%\nonnull_methods_PathUtils.txt"
findstr /n "if .* == null" "%SRC_DIR%\PathUtils.java" > "%TEMP%\nullable_params_PathUtils.txt"
echo   See potential @NonNull return methods in %TEMP%\nonnull_methods_PathUtils.txt
echo   See potential @Nullable parameters in %TEMP%\nullable_params_PathUtils.txt

echo.
echo Next steps:
echo 1. Review the potential annotation locations
echo 2. Manually add @NonNull and @Nullable annotations to methods and parameters
echo 3. Run tests to verify the annotations don't cause issues
echo.
echo Note: Due to JDK compatibility issues, the Checker Framework processor
echo cannot be run directly. Use manual annotation and code review instead. 