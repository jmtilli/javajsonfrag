# JavaJsonFrag: a powerful combined tree-based and event-based parser for JSON

Typically, JSON is parsed by a tree-based parser unlike XML that can be parsed by a tree-based parser or an event-based parser. Event-based parsers are fast and have a low memory footprint, but a drawback is that it is cumbersome to write the required event handlers. Tree-based parsers make the code easier to write, to understand and to maintain but have a large memory footprint as a drawback. Sometimes, JSON is used for huge files such as database dumps that would be preferably parsed by event-based parsing, or so it would appear at a glance, because a tree-based parser cannot hold the whole parse tree in memory at the same time, if the file is huge.

## Example application: customers in a major bank

Let us consider an example application: a listing of a customers in a major bank that has 30 million customers. The test file is in the following format:

```
{
  "customers": [
    {
      "id": 1,
      "name": "Clark Henson",
      "accountCount": 1,
      "totalBalance": 5085.96
    },
    {
      "id": 2,
      "name": "Elnora Ericson",
      "accountCount": 3,
      "totalBalance": 3910.11
    },
    ...
  ]
}
```

The example format requires about 100 bytes per customer plus customer name length. If we assume an average customer name is 15 characters long, the required storage is about 115 bytes per customer. For 30 million customers, this is 3.5 gigabytes. In the example, the file is read to the following structure:

```
public class Customer {
  public int customerId;
  public String name;
  public int accountCount;
  public double totalBalance;
};
```

## Java API for JSON (JAJ)

For XML, there is Simple API for XML (SAX). However, for JSON the usual parse
methods read the whole data into memory at once, not supporting event-driven
parsing. Thus, we provide Java API for JSON (JAJ) to provide the possibility
for event-driven parsing. It is faster and less memory-hungry than the "read
all at once" parsing methods, but it is cumbersome.

A JAJ-based parser is implemented here:

```
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.util.*;
import fi.iki.jmtilli.javajsonfrag.*;
public class ParseJAJ {
  public static void main(String[] args) throws Throwable
  {
    final HashMap<Integer, Customer> customers =
      new HashMap<Integer, Customer>();
    String f = "customers.json.gz";
    InputStream is = new GZIPInputStream(new FileInputStream(f));
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    JAJ.parse(br, new AbstractJAJHandler() {
      Customer c;
      ArrayList<String> context = new ArrayList<String>();
      public void startArray(String dictKey)
      {
        context.add(dictKey);
      }
      public void endArray(String dictKey)
      {
        context.remove(context.size()-1);
      }
      public void startDict(String dictKey)
      {
        context.add(dictKey);
        if (context.size() == 3 &&
            context.get(0) == null &&
            context.get(1).equals("customers") &&
            context.get(2) == null)
        {
          c = new Customer();
        }
      }
      public void endDict(String dictKey)
      {
        context.remove(context.size()-1);
      }
      public void handleNumber(String dictKey, double d)
      {
        if (dictKey.equals("id"))
        {
          c.customerId = (int)d;
          customers.put(c.customerId, c);
        }
        if (dictKey.equals("accountCount"))
        {
          c.accountCount = (int)d;
        }
        if (dictKey.equals("totalBalance"))
        {
          c.totalBalance = d;
        }
      }
      public void handleString(String dictKey, String s)
      {
        if (dictKey.equals("name"))
        {
          c.name = s;
        }
      }
    });
  }
};
```

It can be seen that the parser is quite cumbersome and the code to construct a customer is scattered to two different places. Yet it is fast and has a low memory footprint.

## Parser with the new library

What if we could combine the benefits of the JAJ-based approach with the benefits of the "read whole parse tree into memory" based approach? A parse tree fragment for a single customer dictionary is small enough to be kept in memory. This is what the new library is about. Here is the code to parse the customer file with the new library:

```
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.util.*;
import fi.iki.jmtilli.javajsonfrag.*;
public class ParseCombo {
  public static void main(String[] args) throws Throwable
  {
    final HashMap<Integer, Customer> customers =
            new HashMap<Integer, Customer>();
    String f = "customers.json.gz";
    InputStream is = new GZIPInputStream(new FileInputStream(f));
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    JAJ.parse(br, new JsonFragmentHandler() {
      public void startJsonDict(String dictKey)
      {
        if (is("customers", (String)null))
        {
          super.startFragmentCollection();
        }
      }
      public void endJsonDict(String dictKey, JsonDict js)
      {
        if (is("customers", (String)null))
        {
          Customer c = new Customer();
          c.customerId = js.getNumberIntNotNull("id");
          c.accountCount = js.getNumberIntNotNull("accountCount");
          c.totalBalance = js.getNumberDoubleNotNull("totalBalance");
          c.name = js.getStringNotNull("name");
          customers.put(c.customerId, c);
        }
      }
    });
  }
};
```

Note how the code is significantly more simple than for the JAJ-based approach. Performance is close to the JAJ-based approach, and memory consumption is essentially the same as for JAJ.

Of course, the new library supports getting the whole parse tree in memory:

```
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.util.*;
import fi.iki.jmtilli.javajsonfrag.*;
public class ParseWhole {
  public static void main(String[] args) throws Throwable {
    final HashMap<Integer, Customer> customers =
      new HashMap<Integer, Customer>();
    String f = "customers.json.gz";
    InputStream is = new GZIPInputStream(new FileInputStream(f));
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    JsonDict dict = (JsonDict)JsonFragmentHandler.parseWhole(br);
    JsonArray ar = dict.getArrayNotNull("customers");
    for (int i = 0; i < ar.getSize(); i++) {
      JsonDict js = ar.getDictNotNull(i);
      Customer c = new Customer();
      c.customerId = js.getNumberIntNotNull("id");
      c.accountCount = js.getNumberIntNotNull("accountCount");
      c.totalBalance = js.getNumberDoubleNotNull("totalBalance");
      c.name = js.getStringNotNull("name");
      customers.put(c.customerId, c);
    }
  }
};
```

## License

All of the material related to JavaJsonFrag is licensed under the following MIT
license:

Copyright (C) 2020 Juha-Matti Tilli

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
