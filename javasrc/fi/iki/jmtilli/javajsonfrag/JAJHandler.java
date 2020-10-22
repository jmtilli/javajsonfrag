package fi.iki.jmtilli.javajsonfrag;
/*
 * Java API for JSON handler.
 *
 * Each encountered object has a dictKey field. If the object is not
 * encountered immediately within a dict, the dictKey field is null.
 * An object not immediately within a dict can be either immediately
 * within an array, or be the whole JSON document itself.
 *
 * Examples:
 *
 * The following document:
 * null
 *
 * results in the following call:
 * JAJHandler.handleNull(null);
 *
 * The following document:
 * [true, false]
 *
 * results in the following calls:
 * JAJHandler.startArray(null); // it is the whole document, not within dict
 * JAJHandler.handleBoolean(null, true); // the bool is within array not dict
 * JAJHandler.handleBoolean(null, false); // the bool is within array not dict
 * JAJHandler.endArray(null); // it is the whole document, not within dict
 *
 * The following document:
 * {"array": [2.3]}
 *
 * results in the following calls:
 * JAJHandler.startDict(null); // it is the whole document, not within dict
 * JAJHandler.startArray("array"); // is within dict
 * JAJHandler.handleNumber(null, 2.3); // the number is within array not dict
 * JAJHandler.endArray("array"); // is within dict
 * JAJHandler.endDict(null); // it is the whole document, not within dict
 *
 * The following document:
 * {"key": "value"}
 *
 * results in the following calls:
 * JAJHandler.startDict(null); // it is the whole document, not within dict
 * JAJHandler.handleString("key", "value"); // is within dict
 * JAJHandler.endDict(null); // it is the whole document, not within dict
 */
public interface JAJHandler {
	public void startDict(String dictKey);
	public void endDict(String dictKey);
	public void startArray(String dictKey);
	public void endArray(String dictKey);
	public void handleNull(String dictKey);
	public void handleString(String dictKey, String s);
	public void handleNumber(String dictKey, double d);
	public void handleBoolean(String dictKey, boolean b);
};
