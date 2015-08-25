package clicheUtils;

public class TagIdentifier {

	public enum TagType {
		OPEN_CSS, CLOSE_CSS, OPEN_JS, CLOSE_JS, OPEN_JAVA, OPEN_JAVA_INLINE, CLOSE_ANY_JAVA, TEXTAREA, NO_MATCH
	};

	public static TagType recognizeTag(String s, int position) {

		int i = position;
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == '%' && (i + 2) <= s.length()
				&& s.charAt(i + 2) != '=') {
			return TagType.OPEN_JAVA;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == '%' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == '=') {
			return TagType.OPEN_JAVA_INLINE;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == 's' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == 'c') {
			return TagType.OPEN_JS;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == 's' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == 't' && (i + 3) <= s.length()
				&& s.charAt(i + 3) == 'y') {
			return TagType.OPEN_CSS;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == '/' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == 's' && (i + 3) <= s.length()
				&& s.charAt(i + 3) == 'c') {
			return TagType.CLOSE_JS;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == '/' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == 's' && (i + 3) <= s.length()
				&& s.charAt(i + 3) == 't' && (i + 4) <= s.length()
				&& s.charAt(i + 4) == 'y') {
			return TagType.CLOSE_CSS;
		}
		if (s.charAt(i) == '<' && (i + 1) <= s.length()
				&& s.charAt(i + 1) == 't' && (i + 2) <= s.length()
				&& s.charAt(i + 2) == 'e' && (i + 3) <= s.length()
				&& s.charAt(i + 3) == 'x') {
			return TagType.TEXTAREA;
		}
		if (s.charAt(i) == '>' && i > 0 && s.charAt(i - 1) == '%') {
			return TagType.CLOSE_ANY_JAVA;
		}
		return TagType.NO_MATCH;
	}
}
