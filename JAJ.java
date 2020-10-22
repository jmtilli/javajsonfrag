import java.util.*;
import java.io.*;

public class JAJ {
	private static enum Expect {
		FIRST_KEY, KEY, COLON, COMMA, FIRST_VALUE, VALUE
	};
	private static class StackElement {
		private static enum Type {
			DICT, ARRAY, FIRST
		};
		String key;
		final Type type;
		private static StackElement newFirst()
		{
			return new StackElement(Type.FIRST);
		}
		private static StackElement newArray()
		{
			return new StackElement(Type.ARRAY);
		}
		private static StackElement newDict()
		{
			return new StackElement(Type.DICT);
		}
		private StackElement(Type type) {
			this.key = null;
			this.type = type;
		}
	};
	private static boolean isHexDig(char ch)
	{
		if (ch == '0')
		{
			return true;
		}
		if (ch == '1')
		{
			return true;
		}
		if (ch == '2')
		{
			return true;
		}
		if (ch == '3')
		{
			return true;
		}
		if (ch == '4')
		{
			return true;
		}
		if (ch == '5')
		{
			return true;
		}
		if (ch == '6')
		{
			return true;
		}
		if (ch == '7')
		{
			return true;
		}
		if (ch == '8')
		{
			return true;
		}
		if (ch == '9')
		{
			return true;
		}
		if (ch == 'A')
		{
			return true;
		}
		if (ch == 'B')
		{
			return true;
		}
		if (ch == 'C')
		{
			return true;
		}
		if (ch == 'D')
		{
			return true;
		}
		if (ch == 'E')
		{
			return true;
		}
		if (ch == 'F')
		{
			return true;
		}
		if (ch == 'a')
		{
			return true;
		}
		if (ch == 'b')
		{
			return true;
		}
		if (ch == 'c')
		{
			return true;
		}
		if (ch == 'd')
		{
			return true;
		}
		if (ch == 'e')
		{
			return true;
		}
		if (ch == 'f')
		{
			return true;
		}
		return false;
	}
	public static void parse(BufferedReader r, JAJHandler handler) throws IOException
	{
		ArrayList<StackElement> lastDictKeys = new ArrayList<StackElement>();
		Enum expect = Expect.VALUE;
		lastDictKeys.add(StackElement.newFirst());
		for (;;)
		{
			int chi;
			chi = r.read();
			if (chi < 0)
			{
				throw new RuntimeException("invalid EOF");
			}
			char ch = (char)chi;
			if (ch == '\u0020' || ch == '\n' || ch == '\r' || ch == '\u0009')
			{
				continue;
			}
			if (expect == Expect.COMMA && ch != ']' && ch != '}')
			{
				if (ch != ',')
				{
					throw new RuntimeException("expected comma");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				if (key != null)
				{
					expect = Expect.KEY;
				}
				else
				{
					expect = Expect.VALUE;
				}
				continue;
			}
			if (expect == Expect.COLON)
			{
				if (ch != ':')
				{
					throw new RuntimeException("expected colon");
				}
				expect = Expect.VALUE;
				continue;
			}
			if ((expect == Expect.VALUE || expect == Expect.FIRST_VALUE) && ch == '[')
			{
				handler.startArray(lastDictKeys.get(lastDictKeys.size()-1).key);
				lastDictKeys.add(StackElement.newArray());
				expect = Expect.FIRST_VALUE;
				continue;
			}
			if ((expect == Expect.COMMA || expect == Expect.FIRST_VALUE) && ch == ']')
			{
				StackElement elem = lastDictKeys.remove(lastDictKeys.size()-1);
				if (elem.type != StackElement.Type.ARRAY)
				{
					throw new RuntimeException("invalid end of array");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.endArray(key);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			if ((expect == Expect.FIRST_VALUE || expect == Expect.VALUE) && ch == '{')
			{
				handler.startDict(lastDictKeys.get(lastDictKeys.size()-1).key);
				lastDictKeys.add(StackElement.newDict());
				expect = Expect.FIRST_KEY;
				continue;
			}
			if ((expect == Expect.COMMA || expect == Expect.FIRST_KEY) && ch == '}')
			{
				// FIXME need to separate FIRST_KEY
				StackElement elem = lastDictKeys.remove(lastDictKeys.size()-1);
				if (elem.type != StackElement.Type.DICT)
				{
					throw new RuntimeException("invalid end of dict");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.endDict(key);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			if ((expect == Expect.FIRST_VALUE || expect == Expect.VALUE) && ch == 't')
			{
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'r')
				{
					throw new RuntimeException("not true");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'u')
				{
					throw new RuntimeException("not true");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'e')
				{
					throw new RuntimeException("not true");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.handleBoolean(key, true);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			if ((expect == Expect.FIRST_VALUE || expect == Expect.VALUE) && ch == 'f')
			{
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'a')
				{
					throw new RuntimeException("not false");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'l')
				{
					throw new RuntimeException("not false");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 's')
				{
					throw new RuntimeException("not false");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'e')
				{
					throw new RuntimeException("not false");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.handleBoolean(key, false);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			if ((expect == Expect.FIRST_VALUE || expect == Expect.VALUE) && ch == 'n')
			{
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'u')
				{
					throw new RuntimeException("not null");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'l')
				{
					throw new RuntimeException("not null");
				}
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				if (ch != 'l')
				{
					throw new RuntimeException("not null");
				}
				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.handleNull(key);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			boolean is_value = (expect == Expect.FIRST_VALUE || expect == Expect.VALUE);
			if (is_value && (ch == '0' || ch == '1' ||
			                 ch == '2' || ch == '3' ||
			                 ch == '4' || ch == '5' ||
			                 ch == '6' || ch == '7' ||
			                 ch == '8' || ch == '9' ||
					 ch == '-'))
			{
				boolean is_negative = (ch == '-');
				double num = 0;
				int add_exponent = 0;
				if (ch != '-')
				{
					num = (ch - '0');
				}
				for (;;)
				{
					r.mark(1);
					chi = r.read();
					if (chi < 0)
					{
						ch = 'X';
						break;
						//throw new RuntimeException("invalid EOF"); // FIXME this is allowed
					}
					ch = (char)chi;
					if (ch == '0' || ch == '1' ||
			                    ch == '2' || ch == '3' ||
			                    ch == '4' || ch == '5' ||
			                    ch == '6' || ch == '7' ||
			                    ch == '8' || ch == '9')
					{
						num *= 10;
						num += (ch - '0');
						continue;
					}
					break;
				}
				if (ch == '.')
				{
					double divisor = 1;
					for (;;)
					{
						r.mark(1);
						chi = r.read();
						if (chi < 0)
						{
							throw new RuntimeException("invalid EOF");
						}
						ch = (char)chi;
						if (ch == '0' || ch == '1' ||
						    ch == '2' || ch == '3' ||
						    ch == '4' || ch == '5' ||
						    ch == '6' || ch == '7' ||
						    ch == '8' || ch == '9')
						{
							num *= 10;
							num += (ch - '0');
							add_exponent -= 1;
							continue;
						}
						break;
					}
				}
				if (ch == 'E' || ch == 'e')
				{
					boolean is_exp_negative = false;
					int exponent = 0;
					r.mark(1);
					chi = r.read();
					if (chi < 0)
					{
						throw new RuntimeException("invalid EOF");
					}
					ch = (char)chi;
					if (ch == '+')
					{
						r.mark(1);
						chi = r.read();
						if (chi < 0)
						{
							throw new RuntimeException("invalid EOF");
						}
						ch = (char)chi;
					}
					else if (ch == '-')
					{
						is_exp_negative = true;
						r.mark(1);
						chi = r.read();
						if (chi < 0)
						{
							throw new RuntimeException("invalid EOF");
						}
						ch = (char)chi;
					}
					if (ch != '0' && ch != '1' &&
					    ch != '2' && ch != '3' &&
					    ch != '4' && ch != '5' &&
					    ch != '6' && ch != '7' &&
					    ch != '8' && ch != '9')
					{
						throw new RuntimeException("invalid exponent");
					}
					for (;;)
					{
						exponent *= 10;
						exponent += (ch - '0');
						r.mark(1);
						chi = r.read();
						if (chi < 0)
						{
							break;
							//throw new RuntimeException("invalid EOF"); // FIXME this is allowed
						}
						ch = (char)chi;
						if (ch == '0' || ch == '1' ||
						    ch == '2' || ch == '3' ||
						    ch == '4' || ch == '5' ||
						    ch == '6' || ch == '7' ||
						    ch == '8' || ch == '9')
						{
							continue;
						}
						break;
					}
					num *= Math.pow(10, (is_exp_negative ? (-1) : (1)) * exponent + add_exponent);
				}
				r.reset();

				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.handleNumber(key, is_negative ? (-num) : num);
				if (lastDictKeys.size() <= 1)
				{
					return;
				}
				expect = Expect.COMMA;
				continue;
			}
			boolean is_string_ok = (expect == Expect.FIRST_VALUE || expect == Expect.VALUE || expect == Expect.KEY || expect == Expect.FIRST_KEY);
			if (is_string_ok && ch == '"')
			{
				StringBuilder string = new StringBuilder();
				chi = r.read();
				if (chi < 0)
				{
					throw new RuntimeException("invalid EOF");
				}
				ch = (char)chi;
				while (ch != '"')
				{
					if (ch != '\\' && (ch >= '\u0020' || ch <= '\uFFFF'))
					{
						string.append(ch);
					}
					else if (ch == '\\')
					{
						chi = r.read();
						if (chi < 0)
						{
							throw new RuntimeException("invalid EOF");
						}
						ch = (char)chi;
						if (ch == '"')
						{
							string.append('"');
						}
						else if (ch == '\\')
						{
							string.append('\\');
						}
						else if (ch == '/')
						{
							string.append('/');
						}
						else if (ch == 'b')
						{
							string.append('\b');
						}
						else if (ch == 'f')
						{
							string.append('\f');
						}
						else if (ch == 'n')
						{
							string.append('\n');
						}
						else if (ch == 'r')
						{
							string.append('\r');
						}
						else if (ch == 't')
						{
							string.append('\t');
						}
						else if (ch == 'u')
						{
							int ch1 = r.read();
							int ch2 = r.read();
							int ch3 = r.read();
							int ch4 = r.read();
							if (ch1 < 0 ||
							    ch2 < 0 ||
							    ch3 < 0 ||
							    ch4 < 0)
							{
								throw new RuntimeException("unexpected EOF");
							}
							if (!isHexDig((char)ch1))
							{
								throw new RuntimeException("invalid unicode escape");
							}
							if (!isHexDig((char)ch2))
							{
								throw new RuntimeException("invalid unicode escape");
							}
							if (!isHexDig((char)ch3))
							{
								throw new RuntimeException("invalid unicode escape");
							}
							if (!isHexDig((char)ch4))
							{
								throw new RuntimeException("invalid unicode escape");
							}
							StringBuilder b = new StringBuilder();
							b.append((char)ch1);
							b.append((char)ch2);
							b.append((char)ch3);
							b.append((char)ch4);
							int decimal = Integer.parseInt(b.toString(), 16);
							string.append((char)decimal);
						}
						else
						{
							throw new RuntimeException("invalid escape sequence");
						}
					}
					chi = r.read();
					if (chi < 0)
					{
						throw new RuntimeException("invalid EOF");
					}
					ch = (char)chi;
				}
				if (expect == Expect.FIRST_KEY || expect == Expect.KEY)
				{
					lastDictKeys.get(lastDictKeys.size()-1).key = string.toString();
					expect = Expect.COLON;
					continue;
				}
				else
				{
					String key = lastDictKeys.get(lastDictKeys.size()-1).key;
					handler.handleString(key, string.toString());
					if (lastDictKeys.size() <= 1)
					{
						return;
					}
					expect = Expect.COMMA;
					continue;
				}
			}
			throw new RuntimeException("parse error");
		}
	}
};
