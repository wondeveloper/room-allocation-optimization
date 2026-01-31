#!/bin/bash

echo "Building Spring Boot application..."
./gradlew build

if [ $? -eq 0 ]; then
    echo "Build successful! Starting application..."
    ./gradlew bootRun
else
    echo "Build failed! Application will not start."
    exit 1
fi