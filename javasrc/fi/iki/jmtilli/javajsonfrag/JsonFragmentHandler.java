package fi.iki.jmtilli.javajsonfrag;
import java.util.*;

public class JsonFragmentHandler implements JAJHandler {
	private static class JsonStack {
		private final ArrayList<String> path = new ArrayList<String>();
		public void push(String name)
		{
			path.add(name);
		}
		public void pop(String name)
		{
			String last = path.remove(path.size()-1);
			if (last == null)
			{
				if (name == null)
				{
					return;
				}
				throw new Error("Expected " + name + " got null");
			}
			if (!last.equals(name))
			{
				throw new Error("Expected " + name + " got " + last);
			}
		}
		public boolean is(String... names)
		{
			if (names.length != path.size() - 1)
			{
				return false;
			}
			for (int i = 0; i < names.length; i++)
			{
				if (names[i] == null)
				{
					if (path.get(i+1) != null)
					{
						return false;
					}
				}
				else if (!names[i].equals(path.get(i+1)))
				{
					return false;
				}
			}
			return true;
		}
	}
	private class ConvertToJsonFragmentHandler {
		private ArrayList<JsonObject> objs =
			new ArrayList<JsonObject>();
		private JsonObject obj;
		public final boolean ready()
		{
			return obj != null && objs.isEmpty();
		}
		public final JsonObject getJsonObject()
		{
			return obj;
		}
		public final void startDict(String dictKey)
		{
			JsonDict dict = new JsonDict();
			if (obj == null)
			{
				obj = dict;
			}
			if (!objs.isEmpty())
			{
				JsonObject last = objs.get(objs.size()-1);
				if (last instanceof JsonDict)
				{
					((JsonDict)last).values.put(dictKey, dict);
				}
				else
				{
					((JsonArray)last).values.add(dict);
				}
			}
			objs.add(dict);
		}
		public final void endDict(String dictKey)
		{
			objs.remove(objs.size()-1);
		}
		public final void startArray(String dictKey)
		{
			JsonArray ar = new JsonArray();
			if (obj == null)
			{
				obj = ar;
			}
			if (!objs.isEmpty())
			{
				JsonObject last = objs.get(objs.size()-1);
				if (last instanceof JsonDict)
				{
					((JsonDict)last).values.put(dictKey, ar);
				}
				else
				{
					((JsonArray)last).values.add(ar);
				}
			}
			objs.add(ar);
		}
		public final void endArray(String dictKey)
		{
			objs.remove(objs.size()-1);
		}
		public final void handleNull(String dictKey)
		{
			JsonObject last = objs.get(objs.size()-1);
			if (last instanceof JsonDict)
			{
				((JsonDict)last).values.put(dictKey, null);
			}
			else
			{
				((JsonArray)last).values.add(null);
			}
		}
		public final void handleString(String dictKey, String s)
		{
			JsonObject obj = new JsonString(s);
			JsonObject last = objs.get(objs.size()-1);
			if (last instanceof JsonDict)
			{
				((JsonDict)last).values.put(dictKey, obj);
			}
			else
			{
				((JsonArray)last).values.add(obj);
			}
		}
		public final void handleNumber(String dictKey, double d)
		{
			JsonObject obj = new JsonNumber(d);
			JsonObject last = objs.get(objs.size()-1);
			if (last instanceof JsonDict)
			{
				((JsonDict)last).values.put(dictKey, obj);
			}
			else
			{
				((JsonArray)last).values.add(obj);
			}
		}
		public final void handleBoolean(String dictKey, boolean b)
		{
			JsonObject obj = b ? JsonBoolean.TRUE : JsonBoolean.FALSE;
			JsonObject last = objs.get(objs.size()-1);
			if (last instanceof JsonDict)
			{
				((JsonDict)last).values.put(dictKey, obj);
			}
			else
			{
				((JsonArray)last).values.add(obj);
			}
		}
	};
	private boolean startDictActive = false;
	private boolean startArrayActive = false;
	private ConvertToJsonFragmentHandler h;
	private final JsonStack s = new JsonStack();
	public boolean is(String... names)
	{
		return s.is(names);
	}
	public final void startFragmentCollection()
	{
		if (!startDictActive && !startArrayActive)
		{
			throw new Error("neither dict nor array active");
		}
		if (h != null)
		{
			throw new Error("fragment collection already started");
		}
		h = new ConvertToJsonFragmentHandler();
	}
	// To be overridden
	public void startJsonDict(String dictKey)
	{
	}
	// To be overridden
	public void endJsonDict(String dictKey, JsonObject js)
	{
	}
	// To be overridden
	public void startJsonArray(String dictKey)
	{
	}
	// To be overridden
	public void endJsonArray(String dictKey, JsonObject js)
	{
	}
	// To be overridden
	public void handleJsonNull(String dictKey)
	{
	}
	// To be overridden
	public void handleJsonString(String dictKey, String s)
	{
	}
	// To be overridden
	public void handleJsonNumber(String dictKey, double d)
	{
	}
	// To be overridden
	public void handleJsonBoolean(String dictKey, boolean b)
	{
	}
	public final void startDict(String dictKey)
	{
		s.push(dictKey);
		if (h == null)
		{
			startDictActive = true;
			try {
				startJsonDict(dictKey);
			}
			finally {
				startDictActive = false;
			}
		}
		// Note: h may have changed here
		if (h != null)
		{
			h.startDict(dictKey);
		}
	}
	public final void endDict(String dictKey)
	{
		JsonObject js = null;
		if (h != null)
		{
			h.endDict(dictKey);
			if (h.ready())
			{
				js = h.getJsonObject();
				h = null;
			}
		}
		// Note: h may have changed here
		if (h == null)
		{
			endJsonDict(dictKey, js);
		}
		s.pop(dictKey);
	}
	public final void startArray(String dictKey)
	{
		s.push(dictKey);
		if (h == null)
		{
			startArrayActive = true;
			try {
				startJsonArray(dictKey);
			}
			finally {
				startArrayActive = false;
			}
		}
		// Note: h may have changed here
		if (h != null)
		{
			h.startArray(dictKey);
		}
	}
	public final void endArray(String dictKey)
	{
		JsonObject js = null;
		if (h != null)
		{
			h.endArray(dictKey);
			if (h.ready())
			{
				js = h.getJsonObject();
				h = null;
			}
		}
		// Note: h may have changed here
		if (h == null)
		{
			endJsonArray(dictKey, js);
		}
		s.pop(dictKey);
	}
	public final void handleNull(String dictKey)
	{
		if (h != null)
		{
			h.handleNull(dictKey);
		}
		if (h == null)
		{
			handleJsonNull(dictKey);
		}
	}
	public final void handleString(String dictKey, String s)
	{
		if (h != null)
		{
			h.handleString(dictKey, s);
		}
		if (h == null)
		{
			handleJsonString(dictKey, s);
		}
	}
	public final void handleNumber(String dictKey, double d)
	{
		if (h != null)
		{
			h.handleNumber(dictKey, d);
		}
		if (h == null)
		{
			handleJsonNumber(dictKey, d);
		}
	}
	public final void handleBoolean(String dictKey, boolean b)
	{
		if (h != null)
		{
			h.handleBoolean(dictKey, b);
		}
		if (h == null)
		{
			handleJsonBoolean(dictKey, b);
		}
	}
};
