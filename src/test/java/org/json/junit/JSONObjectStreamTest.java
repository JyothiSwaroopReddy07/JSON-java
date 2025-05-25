package org.json.junit;

import org.json.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Unit tests that exercise the new JSONObject.toStream() API.
 */
public class JSONObjectStreamTest {

    /**
     * XML sample taken from the usage example in the task description.
     */
    private static final String BOOKS_XML =
        "<Books>" +
            "<book><title>AAA</title><author>ASmith</author><price>10.0</price></book>" +
            "<book><title>BBB</title><author>BSmith</author><price>20.0</price></book>" +
        "</Books>";


    /**
     * Helper JSON with mixed primitives for numeric filtering.
     */
    private static JSONObject makeMixedJson() {
        // {"a":1,"b":{"c":2},"arr":[3,4]}
        JSONObject obj = new JSONObject();
        obj.put("a", 1);
        obj.put("b", new JSONObject().put("c", 2));
        obj.put("arr", new JSONArray().put(3).put(4));
        return obj;
    }

    /* ------------------------------------------------------------------ */

    @Test
    public void forEachVisitsAllKeys() {
        JSONObject src = makeMixedJson();

        List<String> keys = new ArrayList<>();
        src.toStream().forEach(node -> keys.add(node.getKey()));

        // Expected keys: a, b, c, arr, 0, 1  (order not guaranteed)
        List<String> expected = Arrays.asList("a", "b", "c", "arr", "0", "1");
        Collections.sort(keys);
        Collections.sort(expected);
        assertEquals("Stream should expose every node key", expected, keys);
    }

    @Test
    public void mapExtractsTitlesFromXml() {
        JSONObject obj = XML.toJSONObject(BOOKS_XML);

        List<String> titles = obj.toStream()
                                 .filter(n -> "title".equals(n.getKey()))
                                 .map(JSONNode::getStringValue)
                                 .collect(Collectors.toList());

        assertEquals(Arrays.asList("AAA", "BBB"), titles);
    }

    @Test
    public void filterByNumericValue() {
        JSONObject src = makeMixedJson();

        List<JSONNode> numericNodes = src.toStream()
                                         .filter(n -> n.getValue() instanceof Number)
                                         .collect(Collectors.toList());

        // we know 1,2,3,4 are present → four numeric nodes
        assertEquals(4, numericNodes.size());

        // Confirm one of them is the "c" key with value 2
        boolean foundC2 = numericNodes.stream()
                                      .anyMatch(n -> "c".equals(n.getKey()) && (Integer) n.getValue() == 2);
        assertTrue(foundC2);
    }

    /** Build {"price":3,"nested":{"price":5},"arr":[{"price":7},9]} */
    private static JSONObject makePriceJson() {
        JSONObject root = new JSONObject().put("price", 3);

        JSONObject nested = new JSONObject().put("price", 5);
        root.put("nested", nested);

        JSONArray arr = new JSONArray();
        arr.put(new JSONObject().put("price", 7));
        arr.put(9);
        root.put("arr", arr);

        return root;
    }

    /* ------------------------------------------------------------------ */
    /*  Chaining demonstrations                                           */
    /* ------------------------------------------------------------------ */

    /**
     * filter  -> map  -> collect
     */
    @Test
    public void filterMapCollectTitles() {
        JSONObject obj = XML.toJSONObject(BOOKS_XML);

        List<String> titles = obj.toStream()
                                 .filter(n -> "title".equals(n.getKey()))
                                 .map(JSONNode::getStringValue)
                                 .collect(Collectors.toList());

        assertEquals(Arrays.asList("AAA", "BBB"), titles);
    }

    /**
     * filter  -> forEach  (mutating numeric values)  -> verify JSON mutated
     */
    @Test
    public void filterForEachUpdatePrices() {
        JSONObject obj = makePriceJson();

        // Double every numeric "price"
        obj.toStream()
           .filter(n -> "price".equals(n.getKey()) && n.getValue() instanceof Number)
           .forEach(n -> {
               double newVal = ((Number) n.getValue()).doubleValue() * 2;
               n.updateValue(newVal);
           });

        assertEquals(6.0,  obj.getDouble("price"),        0.0001);
        assertEquals(10.0, obj.getJSONObject("nested").getDouble("price"), 0.0001);
        assertEquals(14.0, obj.getJSONArray("arr").getJSONObject(0).getDouble("price"), 0.0001);
    }

    /**
     * Chaining based on path predicates: extract keys of nodes that belong
     * to the *second* <book> (index 1) element.
     */
    @Test
    public void pathBasedFilterCollectKeys() {
        JSONObject obj = XML.toJSONObject(BOOKS_XML);

        List<String> keys = obj.toStream()
                               .filter(n -> n.getPath().startsWith("Books/book[1]"))
                               .map(JSONNode::getKey)
                               .collect(Collectors.toList());

        // Expect keys: title, author, price  (order not guaranteed)
        assertTrue(keys.containsAll(Arrays.asList("title", "author", "price")));
        assertEquals(3, keys.size());
    }

    /**
     * map  -> filter  -> reduce   (concatenate all leaf paths containing 'price')
     */
    @Test
    public void mapFilterReducePaths() {
        JSONObject obj = makePriceJson();

        String joined = obj.toStream()
                           .map(JSONNode::getPath)
                           .filter(p -> p.endsWith("price"))
                           .reduce((p1, p2) -> p1 + ";" + p2)
                           .orElse("");

        // Should contain three semicolon-separated paths
        String[] parts = joined.split(";");
        assertEquals(3, parts.length);
        assertTrue(joined.contains("price")); // basic sanity
    }
}