package fi.iki.jmtilli.javajsonfrag;
import java.util.*;
public class JsonDict extends JsonObject {
	public final LinkedHashMap<String, JsonObject> values =
		new LinkedHashMap<String, JsonObject>();
	public static enum NullMode {
		NO_NULL,
		MISSING_IS_NULL,
		NONEXISTENT_IS_NULL,
		BOTH_ARE_NULL,
	};
	public final JsonObject getObject(String key, NullMode nullMode)
	{
		if (!values.containsKey(key))
		{
			if (nullMode == NullMode.NONEXISTENT_IS_NULL || nullMode == NullMode.BOTH_ARE_NULL)
			{
				return null;
			}
			else
			{
				throw new JsonException("nonexistent key: " + key);
			}
		}
		JsonObject result = values.get(key);
		if (result == null)
		{
			if (nullMode == NullMode.MISSING_IS_NULL || nullMode == NullMode.BOTH_ARE_NULL)
			{
				return null;
			}
			else
			{
				throw new JsonException("null value for key: " + key);
			}
		}
		return result;
	}
	public final JsonObject getNotNull(String key)
	{
		return getObject(key, NullMode.NO_NULL);
	}
	public final boolean getBooleanNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + key);
		}
		return ((JsonBoolean)obj).b;
	}
	public final boolean getBoolean(String key, boolean default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + key);
		}
		return ((JsonBoolean)obj).b;
	}
	public final Boolean getBooleanObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + key);
		}
		return ((JsonBoolean)obj).b;
	}
	public final String getStringNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + key);
		}
		return ((JsonString)obj).s;
	}
	public final String getString(String key, String default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + key);
		}
		return ((JsonString)obj).s;
	}
	public final String getStringObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + key);
		}
		return ((JsonString)obj).s;
	}
	public final JsonDict getDictObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonDict))
		{
			throw new JsonException("not a dict: " + key);
		}
		return (JsonDict)obj;
	}
	public final JsonDict getDictNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonDict))
		{
			throw new JsonException("not a dict: " + key);
		}
		return (JsonDict)obj;
	}
	public final JsonArray getArrayObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonArray))
		{
			throw new JsonException("not an array: " + key);
		}
		return (JsonArray)obj;
	}
	public final JsonArray getArrayNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonArray))
		{
			throw new JsonException("not an array: " + key);
		}
		return (JsonArray)obj;
	}

	public final double getNumberDouble(String key, double default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return ((JsonNumber)obj).d;
	}
	public final Double getNumberDoubleObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return Double.valueOf(((JsonNumber)obj).d);
	}
	public final double getNumberDoubleNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return ((JsonNumber)obj).d;
	}

	public final float getNumberFloat(String key, float default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return (float)((JsonNumber)obj).d;
	}
	public final Float getNumberFloatObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return Float.valueOf((float)((JsonNumber)obj).d);
	}
	public final float getNumberFloatNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		return (float)((JsonNumber)obj).d;
	}

	public final byte getNumberByte(String key, byte default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + key);
		}
		return val;
	}
	public final Byte getNumberByteObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + key);
		}
		return Byte.valueOf(val);
	}
	public final byte getNumberByteNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + key);
		}
		return val;
	}


	public final short getNumberShort(String key, short default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + key);
		}
		return val;
	}
	public final Short getNumberShortObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + key);
		}
		return Short.valueOf(val);
	}
	public final short getNumberShortNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + key);
		}
		return val;
	}

	public final int getNumberInt(String key, int default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + key);
		}
		return val;
	}
	public final Integer getNumberIntObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + key);
		}
		return Integer.valueOf(val);
	}
	public final int getNumberIntNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + key);
		}
		return val;
	}

	public final long getNumberLong(String key, long default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + key);
		}
		return val;
	}
	public final Long getNumberLongObject(String key, NullMode nullMode)
	{
		JsonObject obj = getObject(key, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + key);
		}
		return Long.valueOf(val);
	}
	public final long getNumberLongNotNull(String key)
	{
		JsonObject obj = getObject(key, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + key);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + key);
		}
		return val;
	}
};
