package clicheUtils;

import java.util.Arrays;
import java.util.List;

public class HtmlEntity {

    private final List<String> UNCLOSED_TAGS = Arrays.asList("img", "br", "input", "link", "meta",
	    "hr");

    private String entity;

    private char tagType;

    public HtmlEntity(final String stringToParse) {

	StringBuilder stringBuilder = new StringBuilder();
	String trimmedEntity = stringToParse.trim();

	if (trimmedEntity.charAt(0) != '<') {
	    this.tagType = 'M';
	} else if (trimmedEntity.charAt(1) == '%') {
	    this.tagType = 'J';
	} else if (trimmedEntity.charAt(1) == '!') {
	    this.tagType = 'J';
	} else if (trimmedEntity.charAt(1) == 's' && trimmedEntity.charAt(2) == 'c') {
	    this.tagType = 'J';
	} else if (trimmedEntity.charAt(1) == 's' && trimmedEntity.charAt(2) == 't' && trimmedEntity.charAt(3) == 'y') {
	    this.tagType = 'J';
	} else if (trimmedEntity.charAt(1) == '/') {
	    this.tagType = 'E';
	} else if (trimmedEntity.charAt(trimmedEntity.length() - 2) == '/') {
	    this.tagType = 'M';
	} else {
	    String tagName = findTagName(trimmedEntity);
	    if ("textarea".equals(tagName)) {
		    // textarea - do not format	- line feeds are significant	
		    this.entity = trimmedEntity;
		    return;	    	
	    }
	    if (this.UNCLOSED_TAGS.contains(tagName)) {
		this.tagType = 'M';
	    } else {
		this.tagType = 'S';
	    }
	}
	if (this.tagType == 'J') {
	    // java code - do not format
	    this.entity = trimmedEntity;
	    return;
	}
	for (int i = 0; i < trimmedEntity.length(); i++) {
	    if (trimmedEntity.charAt(i) == '\t') {
		stringBuilder.append(' ');
	    } else if (trimmedEntity.charAt(i) != '\n' && trimmedEntity.charAt(i) != '\r') {
		stringBuilder.append(trimmedEntity.charAt(i));
	    }
	}
	String tabsRemovedEntity = stringBuilder.toString();
	stringBuilder = new StringBuilder();
	boolean withinLiteral = false;
	for (int i = 0; i < tabsRemovedEntity.length(); i++) {

	    if (tabsRemovedEntity.charAt(i) == '"') {
		withinLiteral = !withinLiteral;
		stringBuilder.append(tabsRemovedEntity.charAt(i));
	    } else if (withinLiteral) {
		stringBuilder.append(tabsRemovedEntity.charAt(i));
	    } else if (i > 0 && tabsRemovedEntity.charAt(i) == ' '
		    && tabsRemovedEntity.charAt(i - 1) == ' ') {
		// drop character
	    } else {
		stringBuilder.append(tabsRemovedEntity.charAt(i));
	    }
	}

	this.entity = stringBuilder.toString();

    }

    @Override
    public String toString() {
	return this.entity;
    }

    public final char getTagType() {
	return this.tagType;
    }

    public final String getTagName() {
	if (this.tagType != 'S' && this.tagType != 'E') {
	    return null;
	}
	if (this.tagType == 'S') {
	    if (this.entity.contains(" ")) {
		return this.entity.substring(1, this.entity.indexOf(' '));
	    }

	    return this.entity.substring(1, this.entity.length() - 1);

	}
	// E type tag
	if (this.entity.contains(" ")) {
	    return this.entity.substring(2, this.entity.indexOf(' '));
	}

	return this.entity.substring(2, this.entity.length() - 1);
    }

    private String findTagName(String s) {
	StringBuilder result = new StringBuilder();
	int index = 1;
	while (index < s.length()) {
	    char ch = s.charAt(index++);
	    if (ch == ' ' || ch == '>') {
		break;
	    }
	    result.append(ch);

	}
	return result.toString();
    }
}
