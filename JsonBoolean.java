public class JsonBoolean extends JsonObject {
	public final boolean b;
	private JsonBoolean(boolean b)
	{
		this.b = b;
	}
	public static final JsonBoolean TRUE = new JsonBoolean(true);
	public static final JsonBoolean FALSE = new JsonBoolean(false);
};
