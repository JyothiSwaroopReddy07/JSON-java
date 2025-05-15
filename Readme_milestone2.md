1. Import the `Milestone2` folder in IntelliJ.
2. The project is a Maven project, so make sure that you open it as a Maven project in IntelliJ and make sure that you have `Java 8` or later, and Maven installed as well.
3. Run these commands
```
mvn clean
mvn compile
mvn test -Dtest="org.json.junit.XMLTest#testPathExtraction"
mvn test -Dtest="org.json.junit.XMLTest#testTrailingSlash"
mvn test -Dtest="org.json.junit.XMLTest#testEmptyPath"
mvn test -Dtest="org.json.junit.XMLTest#testPathNotFound"
mvn test -Dtest="org.json.junit.XMLTest#testReplaceEntireDocument"
mvn test -Dtest="org.json.junit.XMLTest#testReplaceNestedElement"
mvn test -Dtest="org.json.junit.XMLTest#testReplaceDeepNestedElement"
```
4. The file changes are in `Milestone2/src/main/java/org/json/XML.java`, `Milestone2/src/main/java/org/json/JSONPointer.java` and `Milestone2/src/test/java/org/json/junit/XMLTest.java`. The changes are present between comments `// milestone 2 changes start` and `// milestone 2 changes end` in all the three files.