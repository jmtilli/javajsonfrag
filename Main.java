import java.io.*;
import java.util.*;

public class Main {
	public static class Customer {
		public int customerId;
		public String name;
		public int accountCount;
		public double totalBalance;
	};
	public static void main(String[] args) throws Throwable
	{
		HashMap<Integer, Customer> customers =
			new HashMap<Integer, Customer>();
		if (args.length != 1)
		{
			throw new RuntimeException("usage: java Main f.json");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
		JAJ.parse(br, new JsonFragmentHandler() {
			public void startJsonDict(String dictKey)
			{
				if (is("customers", (String)null))
				{
					super.startFragmentCollection();
				}
			}
			public void endJsonDict(String dictKey, JsonObject js)
			{
				if (is("customers", (String)null))
				{
					Customer c = new Customer();
					JsonDict dict = (JsonDict)js;
					c.customerId = (int)((JsonNumber)dict.values.get("id")).d;
					c.accountCount = (int)((JsonNumber)dict.values.get("accountCount")).d;
					c.totalBalance = ((JsonNumber)dict.values.get("totalBalance")).d;
					c.name = ((JsonString)dict.values.get("name")).s;
					customers.put(c.customerId, c);
				}
			}
		});
		System.out.println(customers.get(1).name);
		System.out.println(customers.get(2).name);
	}
};
