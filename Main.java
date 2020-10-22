import java.io.*;

public class Main {
	public static void main(String[] args) throws Throwable
	{
		if (args.length != 1)
		{
			throw new RuntimeException("usage: java Main f.json");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
		JAJ.parse(br, new JAJHandler() {
			public void startDict(String dictKey)
			{
				System.out.println(dictKey + ": " + "{");
			}
			public void endDict(String dictKey)
			{
				System.out.println(dictKey + ": " + "}");
			}
			public void startArray(String dictKey)
			{
				System.out.println(dictKey + ": " + "[");
			}
			public void endArray(String dictKey)
			{
				System.out.println(dictKey + ": " + "]");
			}
			public void handleNull(String dictKey)
			{
				System.out.println(dictKey + ": " + "null");
			}
			public void handleString(String dictKey, String s)
			{
				System.out.println(dictKey + ": " + '"' + s + '"');
			}
			public void handleNumber(String dictKey, double d)
			{
				System.out.println(dictKey + ": " + d);
			}
			public void handleBoolean(String dictKey, boolean b)
			{
				System.out.println(dictKey + ": " + b);
			}
		});
	}
};
