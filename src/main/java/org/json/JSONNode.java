package org.json;

/**
 * Represents a node in a JSON structure with path information.
 * This allows for traversal and manipulation of JSON nodes during streaming operations.
 */
public class JSONNode {
    private final String path;
    private final String key;
    private final Object value;
    private final JSONObject parent;

    /**
     * Creates a new JSONNode with the specified path, key, and value.
     *
     * @param path The path to this node in the JSON structure
     * @param key The key of this node
     * @param value The value of this node
     * @param parent The parent JSONObject, or null if this is a root node
     */
    public JSONNode(String path, String key, Object value, JSONObject parent) {
        this.path = path;
        this.key = key;
        this.value = value;
        this.parent = parent;
    }

    /**
     * Returns the path to this node.
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the key of this node.
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value of this node.
     *
     * @return The value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the parent JSONObject of this node.
     * 
     * @return The parent JSONObject or null if this is a root node
     */
    public JSONObject getParent() {
        return parent;
    }

    /**
     * Updates the value of this node in its parent.
     * Has no effect if parent is null.
     * 
     * @param newValue The new value to set
     * @return true if the update was successful, false otherwise
     */
    public boolean updateValue(Object newValue) {
        if (parent != null) {
            parent.put(key, newValue);
            return true;
        }
        return false;
    }

    /**
     * Checks if this node's value is a leaf (not a JSONObject or JSONArray).
     * 
     * @return true if this node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        return !(value instanceof JSONObject || value instanceof JSONArray);
    }

    /**
     * Checks if this node's value is a JSONObject.
     * 
     * @return true if this node's value is a JSONObject, false otherwise
     */
    public boolean isObject() {
        return value instanceof JSONObject;
    }

    /**
     * Checks if this node's value is a JSONArray.
     * 
     * @return true if this node's value is a JSONArray, false otherwise
     */
    public boolean isArray() {
        return value instanceof JSONArray;
    }

    /**
     * Gets the value as a String.
     * 
     * @return The string value, or null if the value is not a String
     */
    public String getStringValue() {
        return value instanceof String ? (String) value : null;
    }

    /**
     * Gets the value as an int.
     * 
     * @return The int value
     * @throws JSONException if the value is not a number
     */
    public int getIntValue() {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new JSONException("Value is not a number: " + value);
    }

    /**
     * Gets the value as a double.
     * 
     * @return The double value
     * @throws JSONException if the value is not a number
     */
    public double getDoubleValue() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new JSONException("Value is not a number: " + value);
    }

    /**
     * Gets the value as a boolean.
     * 
     * @return The boolean value
     * @throws JSONException if the value is not a boolean
     */
    public boolean getBooleanValue() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new JSONException("Value is not a boolean: " + value);
    }

    /**
     * Returns a string representation of this JSONNode.
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "JSONNode{" +
                "path='" + path + '\'' +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}