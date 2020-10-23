package fi.iki.jmtilli.javajsonfrag;

public abstract class AbstractJAJHandler implements JAJHandler {
	public void startDict(String dictKey)
	{
	}
	public void endDict(String dictKey)
	{
	}
	public void startArray(String dictKey)
	{
	}
	public void endArray(String dictKey)
	{
	}
	public void handleNull(String dictKey)
	{
	}
	public void handleString(String dictKey, String s)
	{
	}
	public void handleNumber(String dictKey, double d)
	{
	}
	public void handleBoolean(String dictKey, boolean b)
	{
	}
};
