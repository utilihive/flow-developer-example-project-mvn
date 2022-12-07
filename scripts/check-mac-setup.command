#!/bin/bash

# ensure current directory is correct regardless how script was run
cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

javaCommand=$(command -v java)

# check if java command works
if [ -n "$javaCommand" ]
then
  javaVersion=$(java --version)

  # sniff out the java version
  if [[ $javaVersion == *" 11."* ]]
  then
    echo -e "✅ JDK version 11 is installed.\n"
  else
    echo -e "❌ JDK version is incorrect.\nJava 11 is required but found:\n\n$javaVersion"
    exit
  fi
else
  echo "❌ Java not installed and/or JAVA_HOME variable not set."
  exit
fi

settingsFile="$HOME/.m2/settings.xml"

# check if settings file exists
if [ -f "$settingsFile" ]
then
  settingsContent=$(cat "$settingsFile")
  pattern="<settings>.*<servers>.*<server>.*<id>utilihive-sdk</id>.*<password>[^ ]+</password>"

  # verify xml structure
  if [[ $settingsContent =~ $pattern ]]
  then
    echo -e "✅ settings.xml file is correct.\n"
  else
    echo "❌ settings.xml is missing server element."
    echo "Make sure utilihive-sdk server is configured with username and password."
    exit
  fi
else
  echo -e "❌ settings.xml file is missing.\nMake sure it is located at $settingsFile"
  exit
fi

projectFile="$(dirname "$PWD")/.idea/misc.xml"

# check local project file
if grep -q "project-jdk-name=.*11" "$projectFile"
then
  echo "✅ Project SDK is set correctly."
else
  echo -e "❌ Project SDK is incorrect.\nPlease change SDK in IntelliJ under File > Project Structure"
fi
