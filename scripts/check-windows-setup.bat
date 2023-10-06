@echo off

:: check if java command works
where /q java
if errorlevel 1 (
    echo Java not installed and/or JAVA_HOME variable not set.
    echo.
    pause
    exit
)

:: sniff out java version
java --version | findstr /N /C:" 17." > nul
if errorlevel 1 (
    echo JDK version is incorrect.
    echo Java 17 is required but found:
    echo.
    java --version
    echo.
    pause
    exit
)

echo JDK version 17 is installed.
echo.

set settingsFile="%homedrive%%homepath%\.m2\settings.xml"

:: check if settings file exists
if not exist "%settingsFile%" (
    echo settings.xml file is missing.
    echo Make sure it is located at %settingsFile%
    echo.
    pause
    exit
)

:: verify content in settings file
findstr /C:"<id>utilihive-sdk</id>" "%settingsFile%" > nul
if errorlevel 1 (
    echo settings.xml is missing server element.
    echo Make sure utilihive-sdk server is configured with username and password.
    echo.
    pause
    exit
)

echo settings.xml file is correct.
echo.

set projectFile="%~dp0\..\.idea\misc.xml"

:: check local project file
findstr /R /C:"project-jdk-name=.*17" "%projectFile%" > nul
if errorlevel 1 (
    echo Project SDK is incorrect.
    echo Please change SDK in IntelliJ under File ^> Project Structure
    echo.
    pause
    exit
)

echo Project SDK is set correctly.
echo.
pause
