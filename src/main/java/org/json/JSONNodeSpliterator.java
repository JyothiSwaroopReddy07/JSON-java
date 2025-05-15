package org.json;

import java.util.*;
import java.util.function.Consumer;

/**
 * A fixed implementation of JSONNodeSpliterator that prevents infinite recursion.
 * This version uses a more conservative approach to avoid cycles.
 */
public class JSONNodeSpliterator implements Spliterator<JSONNode> {
    private final LinkedList<JSONNode> queue = new LinkedList<>();
    private final boolean recursive;
    private final boolean leavesOnly;
    private final Set<Object> visitedObjects = new HashSet<>();
    
    /**
     * Creates a new JSONNodeSpliterator with the given parameters.
     * 
     * @param jsonObject The root JSON object
     * @param path The current path
     * @param recursive Whether to traverse recursively
     * @param leavesOnly Whether to only return leaf nodes
     */
    public JSONNodeSpliterator(JSONObject jsonObject, String path, boolean recursive, boolean leavesOnly) {
        this.recursive = recursive;
        this.leavesOnly = leavesOnly;
        
        // Collect all nodes first to avoid issues with modification during iteration
        collectNodes(jsonObject, path, jsonObject);
    }
    
    /**
     * Collects all nodes from a JSONObject recursively, preventing infinite loops.
     */
    private void collectNodes(JSONObject obj, String path, JSONObject parent) {
        if (visitedObjects.contains(obj)) {
            return; // Prevent infinite recursion on circular references
        }
        visitedObjects.add(obj);
        
        // Convert keySet to array to avoid ConcurrentModificationException
        String[] keys = obj.keySet().toArray(new String[0]);
        
        for (String key : keys) {
            try {
                Object value = obj.get(key);
                String newPath = path.isEmpty() ? key : path + "/" + key;
                JSONNode node = new JSONNode(newPath, key, value, parent);
                
                // Add node if it's a leaf or if we're not in leaf-only mode
                if (!leavesOnly || node.isLeaf()) {
                    queue.offer(node);
                }
                
                // Recurse if needed and the value is a complex object
                if (recursive && value != null) {
                    if (value instanceof JSONObject && !visitedObjects.contains(value)) {
                        collectNodes((JSONObject) value, newPath, (JSONObject) value);
                    } else if (value instanceof JSONArray) {
                        collectArrayNodes((JSONArray) value, newPath, parent);
                    }
                }
            } catch (Exception e) {
                // Skip problematic keys
                System.err.println("Error processing key '" + key + "': " + e.getMessage());
            }
        }
    }
    
    /**
     * Collects nodes from a JSONArray, handling nested objects and arrays.
     */
    private void collectArrayNodes(JSONArray array, String path, JSONObject parent) {
        for (int i = 0; i < array.length(); i++) {
            try {
                Object value = array.get(i);
                String indexPath = path + "[" + i + "]";
                
                if (value instanceof JSONObject && !visitedObjects.contains(value)) {
                    collectNodes((JSONObject) value, indexPath, (JSONObject) value);
                } else if (value instanceof JSONArray) {
                    collectArrayNodes((JSONArray) value, indexPath, parent);
                } else {
                    // Primitive values in arrays
                    JSONNode node = new JSONNode(indexPath, String.valueOf(i), value, parent);
                    if (!leavesOnly || node.isLeaf()) {
                        queue.offer(node);
                    }
                }
            } catch (Exception e) {
                // Skip problematic array elements
                System.err.println("Error processing array element at index " + i + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public boolean tryAdvance(Consumer<? super JSONNode> action) {
        if (queue.isEmpty()) {
            return false;
        }
        
        JSONNode node = queue.poll();
        action.accept(node);
        return true;
    }
    
    @Override
    public Spliterator<JSONNode> trySplit() {
        return null; // No parallel processing
    }
    
    @Override
    public long estimateSize() {
        return queue.size();
    }
    
    @Override
    public int characteristics() {
        return NONNULL | IMMUTABLE | SIZED;
    }
}