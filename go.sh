#!/bin/bash
echo "Note: When using $0, use absolute pathnames for your files"
cd dist
java -jar productMatcher.jar $*

cd ..

