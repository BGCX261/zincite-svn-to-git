package clicheUtils;

import java.util.LinkedHashMap;

public class SourceUtilities {

	private SourceUtilities() {
		// cannot instantiate
	}

	public static String extractClassName(String s) {
		String result = "";
		int i = s.indexOf("public class ");

		for (int j = i + 13; j < s.length(); j++) {
			if (s.charAt(j) == ' ' || s.charAt(j) == '{') {
				break;
			}
			result += s.charAt(j);
		}
		return result;
	}

	public static String extractMethodName(String s) {
		String result = s;

		result = result.replaceFirst("private ", "");
		result = result.replaceFirst("public ", "");
		result = result.replaceFirst("static ", "");
		result = result.replaceFirst("final ", "");
		result = result.replaceFirst("synchronized ", "");
		result = result.replaceFirst("protected ", "");
		result = result.replaceFirst("<[A-Za-z0-9, ]*>", "");
		result = result.trim();
		int i = result.indexOf(' ');
		if (i > -1) {
			result = result.substring(i);
		}
		int j = result.indexOf('(');
		if (j > -1) {
			result = result.substring(1, j);
		}
		return result;
	}

	public static String extractReturnType(String s) {
		String result = s;

		result = result.replaceFirst("private ", "");
		result = result.replaceFirst("public ", "");
		result = result.replaceFirst("static ", "");
		result = result.replaceFirst("final ", "");
		result = result.replaceFirst("synchronized ", "");
		result = result.replaceFirst("protected ", "");
		result = result.trim();
		int i = result.indexOf(' ');
		int j = result.indexOf('<');

		int k;
		if (i < 0) {
			k = j;
		} else if (j < 0) {
			k = i;
		} else {
			k = i < j ? i : j;
		}
		if (k > 0) {
			result = result.substring(0, k);
		} else {
			result = null;
		}
		return result;
	}

	public static String extractProtectionLevel(String s) {
		if (s.contains("private ")) {
			return "private";
		} else if (s.contains("public ")) {
			return "public";
		} else if (s.contains("protected ")) {
			return "protected";
		} else {
			return null;
		}
	}

	public static LinkedHashMap<String, String> extractParameters(String s) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

		int i = s.indexOf('(');
		int j = s.lastIndexOf(')');
		if (i == -1 || j == -1) {
			return result;
		}
		String temp = s.substring(i + 1, j);

		// remove annotations
		temp = temp.replaceAll("@[a-zA-Z]+\\(\"[a-zA-Z]+\"\\)","");
		// replace generics
		temp = temp.replaceAll("<[a-zA-Z0-9,\\s]*>", " ");

		temp = temp.replaceAll("final ", "");
		temp = temp.replaceAll("extends ", "");
		temp = temp.replaceAll("\\? ", "");
		temp = temp.replaceAll(",", " ");

		temp = temp.replaceAll("[\\s]+", " ");
		// remove leading space
		temp = temp.replaceAll("^[\\s]", "");

		String[] typesAndNames = temp.split(" ");

		if (typesAndNames.length > 1) {
			for (int k = 0; k < typesAndNames.length; k = k + 2) {

				result.put(typesAndNames[k + 1], typesAndNames[k]);

			}
		}
		return result;
	}
}
