package fi.iki.jmtilli.javajsonfrag;
import java.util.*;
public class JsonArray extends JsonObject {
	public final ArrayList<JsonObject> values =
		new ArrayList<JsonObject>();

	public static enum NullMode {
		NO_NULL,
		NULL,
	};
	public final int getSize()
	{
		return values.size();
	}
	public final JsonObject getObject(int index, NullMode nullMode)
	{
		if (index < 0)
		{
			throw new JsonException("negative index: " + index);
		}
		if (index >= values.size())
		{
			throw new JsonException("out of bounds: " + index);
		}
		JsonObject result = values.get(index);
		if (result == null)
		{
			if (nullMode == NullMode.NULL)
			{
				return null;
			}
			else
			{
				throw new JsonException("null value for index: " + index);
			}
		}
		return result;
	}
	public final JsonObject getNotNull(int index)
	{
		return getObject(index, NullMode.NO_NULL);
	}
	public final boolean getBooleanNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + index);
		}
		return ((JsonBoolean)obj).b;
	}
	public final boolean getBoolean(int index, boolean default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + index);
		}
		return ((JsonBoolean)obj).b;
	}
	public final Boolean getBooleanObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonBoolean))
		{
			throw new JsonException("not a boolean: " + index);
		}
		return ((JsonBoolean)obj).b;
	}
	public final String getStringNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + index);
		}
		return ((JsonString)obj).s;
	}
	public final String getString(int index, String default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + index);
		}
		return ((JsonString)obj).s;
	}
	public final String getStringObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonString))
		{
			throw new JsonException("not a string: " + index);
		}
		return ((JsonString)obj).s;
	}
	public final JsonDict getDictObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonDict))
		{
			throw new JsonException("not a dict: " + index);
		}
		return (JsonDict)obj;
	}
	public final JsonDict getDictNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonDict))
		{
			throw new JsonException("not a dict: " + index);
		}
		return (JsonDict)obj;
	}
	public final JsonArray getArrayObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonArray))
		{
			throw new JsonException("not an array: " + index);
		}
		return (JsonArray)obj;
	}
	public final JsonArray getArrayNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonArray))
		{
			throw new JsonException("not an array: " + index);
		}
		return (JsonArray)obj;
	}

	public final double getNumberDouble(int index, double default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return ((JsonNumber)obj).d;
	}
	public final Double getNumberDoubleObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return Double.valueOf(((JsonNumber)obj).d);
	}
	public final double getNumberDoubleNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return ((JsonNumber)obj).d;
	}

	public final float getNumberFloat(int index, float default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return (float)((JsonNumber)obj).d;
	}
	public final Float getNumberFloatObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return Float.valueOf((float)((JsonNumber)obj).d);
	}
	public final float getNumberFloatNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		return (float)((JsonNumber)obj).d;
	}

	public final byte getNumberByte(int index, byte default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + index);
		}
		return val;
	}
	public final Byte getNumberByteObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + index);
		}
		return Byte.valueOf(val);
	}
	public final byte getNumberByteNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		byte val = (byte)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a byte: " + index);
		}
		return val;
	}


	public final short getNumberShort(int index, short default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + index);
		}
		return val;
	}
	public final Short getNumberShortObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + index);
		}
		return Short.valueOf(val);
	}
	public final short getNumberShortNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		short val = (short)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a short: " + index);
		}
		return val;
	}

	public final int getNumberInt(int index, int default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + index);
		}
		return val;
	}
	public final Integer getNumberIntObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + index);
		}
		return Integer.valueOf(val);
	}
	public final int getNumberIntNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		int val = (int)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a int: " + index);
		}
		return val;
	}

	public final long getNumberLong(int index, long default_value, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return default_value;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + index);
		}
		return val;
	}
	public final Long getNumberLongObject(int index, NullMode nullMode)
	{
		JsonObject obj = getObject(index, nullMode);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + index);
		}
		return Long.valueOf(val);
	}
	public final long getNumberLongNotNull(int index)
	{
		JsonObject obj = getObject(index, NullMode.NO_NULL);
		if (!(obj instanceof JsonNumber))
		{
			throw new JsonException("not a number: " + index);
		}
		long val = (long)((JsonNumber)obj).d;
		if ((double)val != ((JsonNumber)obj).d)
		{
			throw new JsonException("not a long: " + index);
		}
		return val;
	}
};
