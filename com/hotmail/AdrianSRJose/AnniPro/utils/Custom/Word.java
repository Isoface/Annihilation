package com.hotmail.AdrianSRJose.AnniPro.utils.Custom;

import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public final class Word implements java.io.Serializable, Comparable<String>, CharSequence {
	/** The empty string. */
	public static final String EMPTY = "";
	
	/**
	 * OS-dependent line separator, defaults to {@code "\n"} if the system property
	 * {@code ""line.separator"} cannot be read.
	 */
	public static final String LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator",
			"\n");
	
	/** The value is used for character storage. */
	private final char value[];

	/** The offset is the first index of the storage that is used. */
	private final int offset;

	/** The count is the number of characters in the String. */
	private final int count;

	/** Cache the hash code for the string */
	private int hash; // Default to 0

	/** use serialVersionUID from JDK 1.0.2 for interoperability */
	private static final long serialVersionUID = -6849794470754667710L;

	/**
	 * Class String is special cased within the Serialization Stream Protocol.
	 *
	 * A String instance is written initially into an ObjectOutputStream in the
	 * following format:
	 * 
	 * <pre>
	 *      <code>TC_STRING</code> (utf String)
	 * </pre>
	 * 
	 * The String is written by method <code>DataOutput.writeUTF</code>. A new
	 * handle is generated to refer to all future references to the string instance
	 * within the stream.
	 */
	private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];

	/**
	 * Initializes a newly created {@code String} object so that it represents an
	 * empty character sequence. Note that use of this constructor is unnecessary
	 * since Strings are immutable.
	 */
	public Word() {
		this.offset = 0;
		this.count = 0;
		this.value = new char[0];
	}

	public Word(char value[]) {
		int size = value.length;
		this.offset = 0;
		this.count = size;
		this.value = Arrays.copyOf(value, size);
	}
	
	public Word(String string) {
		this(string.toCharArray());
	}

	@Override
	public char charAt(int index) {
		return toString().charAt(index);
	}

	@Override
	public int length() {
		return toString().length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return toString().subSequence(start, end);
	}

	@Override
	public int compareTo(String other) {
		return toString().compareTo(other);
	}

	@Override
	public String toString() {
		return new String(value);
	}

	public static boolean isWord(Object obj) {
		return obj instanceof String || obj instanceof Word;
	}
	
	/**
	 * Returns a double quoted string.
	 * 
	 * @param str
	 *            a String
	 * @return {@code "str"}
	 */
	public static String dquote(final String str) {
		return Chars.DQUOTE + str + Chars.DQUOTE;
	}
	
	/**
	 * Checks if a String is blank. A blank string is one that is {@code null},
	 * empty, or when trimmed using {@link String#trim()} is empty.
	 *
	 * @param s
	 *            the String to check, may be {@code null}
	 * @return {@code true} if the String is {@code null}, empty, or trims to empty.
	 */
	public static boolean isBlank(final String s) {
		return s == null || s.trim().isEmpty();
	}
	
	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * Strings.isEmpty(null)      = true
	 * Strings.isEmpty("")        = true
	 * Strings.isEmpty(" ")       = false
	 * Strings.isEmpty("bob")     = false
	 * Strings.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * CharSequence. That functionality is available in isBlank().
	 * </p>
	 *
	 * <p>
	 * Copied from Apache Commons Lang
	 * org.apache.commons.lang3.StringUtils.isEmpty(CharSequence)
	 * </p>
	 *
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
	
	/**
	 * Checks if a String is not blank. The opposite of {@link #isBlank(String)}.
	 *
	 * @param s
	 *            the String to check, may be {@code null}
	 * @return {@code true} if the String is non-{@code null} and has content after
	 *         being trimmed.
	 */
	public static boolean isNotBlank(final String s) {
		return !isBlank(s);
	}

	/**
	 * <p>
	 * Checks if a CharSequence is not empty ("") and not null.
	 * </p>
	 *
	 * <pre>
	 * Strings.isNotEmpty(null)      = false
	 * Strings.isNotEmpty("")        = false
	 * Strings.isNotEmpty(" ")       = true
	 * Strings.isNotEmpty("bob")     = true
	 * Strings.isNotEmpty("  bob  ") = true
	 * </pre>
	 *
	 * <p>
	 * Copied from Apache Commons Lang
	 * org.apache.commons.lang3.StringUtils.isNotEmpty(CharSequence)
	 * </p>
	 *
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is not empty and not null
	 */
	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	/**
	 * Returns a quoted string.
	 * 
	 * @param str
	 *            a String
	 * @return {@code 'str'}
	 */
	public static String quote(final String str) {
		return Chars.QUOTE + str + Chars.QUOTE;
	}

	/**
	 * Shorthand for {@code str.toUpperCase(Locale.ROOT);}
	 * 
	 * @param str
	 *            The string to upper case.
	 * @return a new string
	 * @see String#toLowerCase(Locale)
	 */
	public String toRootUpperCase(final String str) {
		return str.toUpperCase(Locale.ROOT);
	}

	/**
	 * <p>
	 * Removes control characters (char &lt;= 32) from both ends of this String
	 * returning {@code null} if the String is empty ("") after the trim or if it is
	 * {@code null}.
	 *
	 * <p>
	 * The String is trimmed using {@link String#trim()}. Trim removes start and end
	 * characters &lt;= 32.
	 * </p>
	 *
	 * <pre>
	 * Strings.trimToNull(null)          = null
	 * Strings.trimToNull("")            = null
	 * Strings.trimToNull("     ")       = null
	 * Strings.trimToNull("abc")         = "abc"
	 * Strings.trimToNull("    abc    ") = "abc"
	 * </pre>
	 *
	 * <p>
	 * Copied from Apache Commons Lang
	 * org.apache.commons.lang3.StringUtils.trimToNull(String)
	 * </p>
	 *
	 * @param str
	 *            the String to be trimmed, may be null
	 * @return the trimmed String, {@code null} if only chars &lt;= 32, empty or
	 *         null String input
	 */
	public static String trimToNull(final String str) {
		final String ts = str == null ? null : str.trim();
		return isEmpty(ts) ? null : ts;
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterable} into a single String
	 * containing the provided elements.
	 * </p>
	 *
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings
	 * within the iteration are represented by empty strings.
	 * </p>
	 *
	 * @param iterable
	 *            the {@code Iterable} providing the values to join together, may be
	 *            null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 */
	public static String join(final Iterable<?> iterable, final char separator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator);
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterator} into a single String
	 * containing the provided elements.
	 * </p>
	 *
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings
	 * within the iteration are represented by empty strings.
	 * </p>
	 *
	 * @param iterator
	 *            the {@code Iterator} of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 */
	public static String join(final Iterator<?> iterator, final char separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return Objects.toString(first, EMPTY);
		}

		// two or more elements
		final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			buf.append(separator);
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}

		return buf.toString();
	}
}