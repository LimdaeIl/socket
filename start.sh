echo "Build Project"

./gradlew claen build

echo "Start Server"

cd ./build/libs
java -jar