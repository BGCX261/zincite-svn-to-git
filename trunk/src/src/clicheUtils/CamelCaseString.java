package clicheUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to camelcase a string of lowercase words with no space separation.
 * 
 * It uses dictionary.txt, a file of site specific words to break the string
 * into separate words. It searches from the end of the input string, looking
 * for the longest match first.
 * 
 * dictionary.txt is just a list of words, one word per line. Order is
 * irrelevant.
 * 
 * The class must be constructed, it reads the dictionary in the constructor.
 */
public class CamelCaseString {

	private static class MyComparator implements Comparator<String> {
		public int compare(String o1, String o2) {
			if (o1.length() > o2.length()) {
				return 1;
			} else if (o1.length() < o2.length()) {
				return -1;
			} else {
				return o1.compareTo(o2);
			}
		}
	}

	private List<String> dictionary;

	public CamelCaseString() throws IOException {

		this.dictionary = FileUtilities
				.readFileIntoList(SiteConstants.PROJECT_DIRECTORY
						+ "dictionary.txt");

		Collections.sort(this.dictionary, new MyComparator());

	}
	private String capitalizeFirstLetter(String s) {
		char[] stringArray = s.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}
	private String extractToken(String s, List<String> tokens) {

		// System.out.println("Entering with " + s + " : " + tokens.size());
		String result = s;

		if (s.length() < 3) {
			return result;
		}

		if (!Character.isLetter(s.charAt(s.length() - 1))) {
			tokens.add(String.valueOf(s.charAt(s.length() - 1)));
			result = s.substring(0, s.length() - 1);
		} else {
			boolean dictionaryWordFound = false;
			for (int i = this.dictionary.size() - 1; i > -1; i--) {
				String word = this.dictionary.get(i);
				if (word.length() <= result.length() && result.endsWith(word)) {
					tokens.add(word);
					result = result.substring(0, s.length() - word.length());
					dictionaryWordFound = true;
					break;
				}
			}
			if (!dictionaryWordFound) {
				tokens.add(result);
				return result;
			}
		}
		if (s.length() > 2) {
			result = extractToken(result, tokens);
		}

		return result;
	}
	public String toCamelCase(String s) {

		List<String> tokens = new ArrayList<String>();

		if (s == null || s.length() < 3) {
			// cannot be parsed
			return s;
		}
		extractToken(s.toLowerCase(), tokens);

		StringBuilder result = new StringBuilder();
		boolean firstWord = true;
		for (int i = tokens.size() - 1; i > -1; i--) {
			String token = tokens.get(i);
			if (firstWord) {
				firstWord = false;
			} else {
				token = capitalizeFirstLetter(token);
			}
			result.append(token);
		}
		return result.toString();
	}

}
