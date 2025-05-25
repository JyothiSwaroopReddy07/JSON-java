````
# JSON-java – Streaming Extension

This branch augments the **JSON-java** library (`org.json`) with first-class **Java 8 Stream** traversal of any `JSONObject`.  
All changes are additive and fully backward-compatible.

---

## ✨ What’s New?

| Item | File | Purpose |
|------|------|---------|
| **`JSONNode`** | `src/main/java/org/json/JSONNode.java` | Light record exposing `path`, `key`, `value`, `parent` plus helpers such as `isLeaf()` & `updateValue()`. |
| **`JSONNodeSpliterator`** | `src/main/java/org/json/JSONNodeSpliterator.java` | Depth-first spliterator that walks the entire object/array tree. |
| **`JSONObject.toStream()`** | `src/main/java/org/json/JSONObject.java` | New public API &mdash; returns `Stream<JSONNode>` over every node in the structure. |
| **JUnit suites** | `src/test/java/org/json/junit/*` | Tests validating traversal, chaining, mutation, path correctness, leaf-only traversal, etc. |

---

## 🔧 Quick Usage

```java
String xml =
    "<Books>" +
      "<book><title>AAA</title><author>ASmith</author><price>10.0</price></book>" +
      "<book><title>BBB</title><author>BSmith</author><price>20.0</price></book>" +
    "</Books>";

JSONObject obj = XML.toJSONObject(xml);

// 1  Print every path/value pair
obj.toStream().forEach(n ->
    System.out.println(n.getPath() + " = " + n.getValue()));

// 2  Collect all <title> values
List<String> titles = obj.toStream()
                         .filter(n -> "title".equals(n.getKey()))
                         .map(JSONNode::getStringValue)
                         .collect(Collectors.toList());

// 3  Raise all <price> values by 10 %
obj.toStream()
   .filter(n -> "price".equals(n.getKey()) && n.getValue() instanceof Number)
   .forEach(n -> {
       double v = ((Number) n.getValue()).doubleValue();
       n.updateValue(v * 1.10);
   });
````

Each `JSONNode` carries a JSON-pointer-style path (e.g. `Books/book[1]/price`) so you can easily scope operations.

---

## 🛠 Build & Test

Requires **Java 8+** and Maven 3.

```bash
mvn clean test
```

New test classes:

* **`JSONObjectStreamTest`** – full traversal, title extraction, numeric filtering.
* **`JSONObjectStreamAdditionalTest`** – node mutation, array-index paths, leaf-only counting.
* **`JSONObjectStreamChainingTest`** – chained operations (`filter→map→collect`, `forEach`, `reduce`) and path-based scoping.

All original JSON-java tests still pass.

---

## 📚 Design Highlights

* **Depth-first traversal** across objects *and* arrays.
* **Lazy emission** — nodes stream one-by-one (no large in-memory collection).
* **Cycle safety** via `IdentityHashMap` to avoid infinite recursion.
* **No breaking changes** — only adds new types/methods.

---

## ➕ Future Ideas

* `toLeafStream()` for leaf-only traversal.
* `toParallelStream()` wrapper (thread-safe spliterator).
* Helper filters for JSON-Pointer / regex path matching.


```
```
