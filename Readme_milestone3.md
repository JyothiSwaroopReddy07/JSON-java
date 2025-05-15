
# Milestone 3

## Overview

This milestone adds support for **key transformation during XML parsing** and introduces **streaming utilities for JSONObject traversal**. It generalizes the transformation logic to be reusable with any transformation function provided by client code.


## Build and Test Commands

Run the following commands in the terminal to build and test the project:

```bash
mvn clean
mvn compile
mvn test -Dtest="org.json.junit.XMLTest#testSimpleJSONObjectStream"
mvn test -Dtest="org.json.junit.XMLTest#testNestedJSONObjectStream"
mvn test -Dtest="org.json.junit.XMLTest#testJSONArrayStream"
mvn test -Dtest="org.json.junit.XMLTest#testLeafStreamOnly"
mvn test -Dtest="org.json.junit.XMLTest#testFlatStreamOnly"
mvn test -Dtest="org.json.junit.XMLTest#testComplexXML"
```

## Changes Overview

### Files Modified:

* `Milestone3/src/main/java/org/json/XML.java`
* `Milestone3/src/main/java/org/json/JSONNode.java`
* `Milestone3/src/main/java/org/json/JSONNodeSpliterator.java`
* `Milestone3/src/main/java/org/json/JSONObject.java`
* `Milestone3/src/test/java/org/json/junit/XMLTest.java`



## Features Implemented

* ✅ **Key Transformer Functionality**:

  * Added overloaded method: `XML.toJSONObject(Reader reader, KeyTransformer keyTransformer)`
  * Allows clients to define custom key transformations applied during XML parsing (e.g., prefixing, renaming, reversing keys).

* ✅ **Streaming JSONNode API**:

  * Added `JSONNode` class to represent nodes with path info.
  * Implemented `JSONNodeSpliterator` for safe, recursive traversal.
  * Added methods in `JSONObject`:

    * `toStream()` — All nodes.
    * `toLeafStream()` — Only leaf nodes.
    * `toFlatStream()` — Only top-level nodes.

* ✅ **Unit Tests Added**:

  * Streaming tests for simple, nested, and array JSONObjects.
  * Tests for leaf-only and flat-only streams.
  * XML parsing tests with identity key transformation applied.
  * Functional tests to extract and process nested XML data.

## Performance Note

Unlike Milestone 1, where key transformations were applied after parsing, this implementation applies transformations **during parsing**. This reduces:

* Redundant traversal passes.
* Memory overhead for large XML structures.
* Complexity for client code, as the transformation is handled internally.

---