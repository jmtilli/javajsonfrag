package fi.iki.jmtilli.javajsonfrag;
import java.util.*;
import java.io.*;

public class JAJ {
	private static enum Expect {
		FIRST_KEY, KEY, COLON, COMMA, FIRST_VALUE, VALUE
	};
	private static class ATOF {
		private static enum Mode {
			PERIOD_OR_EXPONENT_CHAR,
			MANTISSA_SIGN,
			MANTISSA_FIRST,
			MANTISSA,
			MANTISSA_FRAC_FIRST,
			MANTISSA_FRAC,
			EXPONENT_CHAR,
			EXPONENT_SIGN,
			EXPONENT_FIRST,
			EXPONENT,
			DONE,
		};
		public final StringBuilder b = new StringBuilder();
		private Mode mode = Mode.MANTISSA_SIGN;
		private boolean exponent_offset_set = false;
		private long exponent_offset = 0;
		private long skip_offset = 0;
		private long exponent = 0;
		private boolean expnegative = false;
		private boolean ended = false;

		public void reset()
		{
			b.setLength(0);
			mode = Mode.MANTISSA_SIGN;
			exponent_offset_set = false;
			exponent_offset = 0;
			skip_offset = 0;
			exponent = 0;
			expnegative = false;
			ended = false;
		}

		private void markNegative()
		{
			if (b.length() != 0)
			{
				throw new Error("invalid state");
			}
			b.append('-');
		}

		private void emitDigit(char digit)
		{
			int digitint = (digit - '0');
			if (digitint < 0 || digitint > 9)
			{
				throw new Error("not a digit: " + digit);
			}
			if (digitint == 0 && (b.length() == 0 || (b.length() == 1 && b.charAt(0) == '-')))
			{
				if (exponent_offset_set && exponent_offset < 0)
				{
					exponent_offset--;
				}
				return;
			}
			if (b.length() >= 64 - 6)
			{
				if (!exponent_offset_set)
				{
					if (skip_offset == Long.MAX_VALUE)
					{
						// Uh-oh. Let's hope this never happens.
						// It's a huge honking number unless it has
						// a very negative exponent.
						return;
					}
					skip_offset++;
				}
				return;
			}
			b.append(digit);
			if (b.length() == 1 || (b.length() == 2 && b.charAt(0) == '-'))
			{
				b.append('.');
			}
		}

		private void storePeriod()
		{
			int periodbufsiz;
			if (b.length() == 0 || (b.length() == 1 && b.charAt(0) == '-'))
			{
				exponent_offset = -1;
				exponent_offset_set = true;
				return;
			}
			if (b.charAt(0) == '-')
			{
				periodbufsiz = 3;
			}
			else
			{
				periodbufsiz = 2;
			}
			exponent_offset = b.length() - periodbufsiz + skip_offset;
			exponent_offset_set = true;
		}

		private void setExponent(long exponent)
		{
			exponent_offset += exponent;
		}

		private double getNumber()
		{
			int nch;
			if (!exponent_offset_set)
			{
				storePeriod();
				emitDigit('0');
			}
			if (ended)
			{
				return Double.valueOf(b.toString());
			}
			if (b.length() == 0 || (b.length() == 1 && b.charAt(0) == '-'))
			{
				b.append('0');
				b.append('.');
				b.append('0');
			}
			b.append('e');
			if (exponent_offset > 999)
			{
				exponent_offset = 999;
			}
			else if (exponent_offset < -999)
			{
				exponent_offset = -999;
			}
			b.append("" + exponent_offset);
			ended = true;
			return Double.valueOf(b.toString());
		}

		public double end()
		{
			setExponent(expnegative ? (-exponent) : exponent);
			mode = Mode.DONE;
			exponent = 0;
			return getNumber();
		}

		public boolean feedChar(char ch)
		{
			if (mode == Mode.MANTISSA_SIGN && ch == '+')
			{
				mode = Mode.MANTISSA_FIRST;
				return true;
			}
			if (mode == Mode.MANTISSA_SIGN && ch == '-')
			{
				markNegative();
				mode = Mode.MANTISSA_FIRST;
				return true;
			}
			if (mode == Mode.MANTISSA_SIGN && ch == '.')
			{
				mode = Mode.MANTISSA_FIRST;
			}
			if (mode == Mode.MANTISSA_SIGN &&
			    (ch == '0' || ch == '1' || ch == '2' ||
			     ch == '3' || ch == '4' || ch == '5' ||
			     ch == '6' || ch == '7' || ch == '8' ||
			     ch == '9'))
			{
				mode = Mode.MANTISSA_FIRST;
			}
			if (mode == Mode.MANTISSA_FIRST && ch == '0')
			{
				emitDigit(ch);
				mode = Mode.PERIOD_OR_EXPONENT_CHAR;
				return true;
			}
			if (mode == Mode.PERIOD_OR_EXPONENT_CHAR && (ch == 'e' || ch == 'E'))
			{
				mode = Mode.EXPONENT_CHAR;
			}
			if (mode == Mode.PERIOD_OR_EXPONENT_CHAR && ch == '.')
			{
				mode = Mode.MANTISSA;
			}
			if (mode == Mode.EXPONENT_CHAR)
			{
				if (ch == 'e' || ch == 'E')
				{
					if (!exponent_offset_set)
					{
						storePeriod();
						emitDigit('0');
					}
					mode = Mode.EXPONENT_SIGN;
					return true;
				}
				throw new Error("foo");
			}
			if (mode == Mode.MANTISSA || mode == Mode.MANTISSA_FIRST)
			{
				mode = Mode.MANTISSA;
				if (ch == '0' || ch == '1' || ch == '2' ||
				    ch == '3' || ch == '4' || ch == '5' ||
				    ch == '6' || ch == '7' || ch == '8' ||
				    ch == '9')
				{
					emitDigit(ch);
					return true;
				}
				if (ch == '.')
				{
					storePeriod();
					mode = Mode.MANTISSA_FRAC_FIRST;
					return true;
				}
				if (ch == 'e' || ch == 'E')
				{
					if (!exponent_offset_set)
					{
						storePeriod();
						emitDigit('0');
					}
					mode = Mode.EXPONENT_SIGN;
					return true;
				}
				mode = Mode.DONE;
				return false;
			}
			if (mode == Mode.MANTISSA_FRAC || mode == Mode.MANTISSA_FRAC_FIRST)
			{
				if (ch == '0' || ch == '1' || ch == '2' ||
				    ch == '3' || ch == '4' || ch == '5' ||
				    ch == '6' || ch == '7' || ch == '8' ||
				    ch == '9')
				{
					emitDigit(ch);
					mode = Mode.MANTISSA_FRAC;
					return true;
				}
				if (mode == Mode.MANTISSA_FRAC_FIRST)
				{
					throw new Error("foo");
				}
				if (ch == 'e' || ch == 'E')
				{
					if (!exponent_offset_set)
					{
						storePeriod();
						emitDigit('0');
					}
					mode = Mode.EXPONENT_SIGN;
					return true;
				}
				mode = Mode.DONE;
				return false;
			}
			if (mode == Mode.EXPONENT_SIGN)
			{
				if (ch == '+')
				{
					expnegative = false;
					mode = Mode.EXPONENT_FIRST;
					return true;
				}
				if (ch == '-')
				{
					expnegative = true;
					mode = Mode.EXPONENT_FIRST;
					return true;
				}
				if (ch == '0' || ch == '1' || ch == '2' ||
				    ch == '3' || ch == '4' || ch == '5' ||
				    ch == '6' || ch == '7' || ch == '8' ||
				    ch == '9')
				{
					if (exponent > (Long.MAX_VALUE - 9)/10)
					{
						// prevent overflow
						return true;
					}
					exponent *= 10;
					exponent += (ch - '0');
					mode = Mode.EXPONENT;
					return true;
				}
				throw new Error("junk");
			}
			if (mode == Mode.EXPONENT_FIRST || mode == Mode.EXPONENT)
			{
				if (ch == '0' || ch == '1' || ch == '2' ||
				    ch == '3' || ch == '4' || ch == '5' ||
				    ch == '6' || ch == '7' || ch == '8' ||
				    ch == '9')
				{
					if (exponent > (Long.MAX_VALUE - 9)/10)
					{
						// prevent overflow
						return true;
					}
					exponent *= 10;
					exponent += (ch - '0');
					mode = Mode.EXPONENT;
					return true;
				}
				if (mode == Mode.EXPONENT_FIRST)
				{
					throw new Error("junk");
				}
				mode = Mode.DONE;
				return false;
			}
			throw new Error("junk");
		}
		public void feedString(String s)
		{
			int i;
			for (i = 0; i < s.length(); i++)
			{
				char ch = s.charAt(i);
				if (!feedChar(ch))
				{
					throw new Error("junk at end");
				}
			}
		}
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
		ATOF atof = new ATOF();
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
					throw new RuntimeException("expected comma got " + ch);
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
					 ch == '-')) // FIXME +?
			{
				atof.reset();
				atof.feedChar(ch);
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
					if (!atof.feedChar(ch))
					{
						break;
					}
				}
				r.reset();

				String key = lastDictKeys.get(lastDictKeys.size()-1).key;
				handler.handleNumber(key, atof.end());
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
