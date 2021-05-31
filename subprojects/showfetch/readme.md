# Description
Simple microservice/application that can fetch Finnish TV-show data.

# Test build
./gradlew :showfetch:build

# Build and connect to cloud
./gradlew :showfetch:build -Penv=cloud_mysql

# Run
java -jar build/libs/showfetch.jar