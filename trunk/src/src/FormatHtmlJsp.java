import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.HtmlEntity;
import clicheUtils.SiteConstants;
import clicheUtils.TagIdentifier;
import clicheUtils.TagIdentifier.TagType;

public class FormatHtmlJsp {

	/**
	 * Reads a jsp file in DTD HTML and formats
	 * it. Writes to outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@Command
	public String format(final String fileName) {
		return format(fileName, false);
	}
	
	@Command
	public String fo(final String fileName) {
		return format(fileName, false);
	}

	@Command
	public String debug(final String fileName) {
		return format(fileName, true);
	}

	private String format(final String fileName, boolean debug) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<HtmlEntity> htmlEntities = new ArrayList<HtmlEntity>();
			List<String> outList = new ArrayList<String>();
			StringBuilder stringBuilder = new StringBuilder();

			boolean withinJava = false;
			boolean withinJavaInline = false;
			boolean withinJavaScript = false;
			boolean withinCSS = false;

			for (String s : list) {

				// return linefeed removed by readFileIntoList
				s = s + SiteConstants.LINE_FEED;
				boolean withinLiteral = false;

				for (int i = 0; i < s.length(); i++) {
					TagType tagType = TagIdentifier.recognizeTag(s, i);
					if (s.charAt(i) == '"') {
						withinLiteral = !withinLiteral;
						stringBuilder.append(s.charAt(i));

					} else if (tagType == TagType.TEXTAREA && !withinLiteral
							&& !withinJavaScript && !withinCSS && !withinJava) {
						for (int j = i; j < s.length(); j++) {
							// write entire line to output
							stringBuilder.append(s.charAt(j));
						}
						htmlEntities.add(new HtmlEntity(stringBuilder
								.toString()));
						stringBuilder = new StringBuilder();
						break;
					} else if (tagType == TagType.OPEN_JAVA && !withinLiteral
							&& !withinJavaScript && !withinCSS) {
						withinJava = true;
						stringBuilder.append(s.charAt(i));

					} else if (tagType == TagType.OPEN_JAVA_INLINE
							&& !withinLiteral && !withinJavaScript
							&& !withinCSS) {
						withinJavaInline = true;
						stringBuilder.append(s.charAt(i));

					} else if (tagType == TagType.OPEN_JS && !withinLiteral
							&& !withinJava && !withinJavaInline && !withinCSS) {
						withinJavaScript = true;
						stringBuilder.append(s.charAt(i));

					} else if (tagType == TagType.OPEN_CSS && !withinLiteral
							&& !withinJava && !withinJavaInline
							&& !withinJavaScript) {
						withinCSS = true;
						stringBuilder.append(s.charAt(i));

					} else if (tagType == TagType.CLOSE_JS && withinJavaScript) {

						stringBuilder.append(s.charAt(i));
						i++; // <
						stringBuilder.append(s.charAt(i));
						i++; // /
						stringBuilder.append(s.charAt(i));
						i++; // s
						stringBuilder.append(s.charAt(i));
						i++; // c
						stringBuilder.append(s.charAt(i));
						i++; // r
						stringBuilder.append(s.charAt(i));
						i++; // i
						stringBuilder.append(s.charAt(i));
						i++; // p
						stringBuilder.append(s.charAt(i));
						i++; // t
						stringBuilder.append(s.charAt(i));
						i++; // >
						stringBuilder.append(s.charAt(i));
						htmlEntities.add(new HtmlEntity(stringBuilder
								.toString()));
						withinLiteral = false;
						withinJavaScript = false;
						stringBuilder = new StringBuilder();

					} else if (tagType == TagType.CLOSE_CSS && withinCSS) {
						stringBuilder.append(s.charAt(i));
						i++; // <
						stringBuilder.append(s.charAt(i));
						i++; // /
						stringBuilder.append(s.charAt(i));
						i++; // s
						stringBuilder.append(s.charAt(i));
						i++; // t
						stringBuilder.append(s.charAt(i));
						i++; // y
						stringBuilder.append(s.charAt(i));
						i++; // l
						stringBuilder.append(s.charAt(i));
						i++; // e
						stringBuilder.append(s.charAt(i));
						i++; // >
						stringBuilder.append(s.charAt(i));
						htmlEntities.add(new HtmlEntity(stringBuilder
								.toString()));
						withinLiteral = false;
						withinCSS = false;
						stringBuilder = new StringBuilder();

					} else if (s.charAt(i) == '<' && !withinLiteral
							&& !withinJava && !withinJavaInline
							&& !withinJavaScript && !withinCSS) {
						// check if entity exists
						if (stringBuilder.toString().trim().length() > 0) {
							htmlEntities.add(new HtmlEntity(stringBuilder
									.toString()));
							withinLiteral = false;
							withinJava = false;
						}
						stringBuilder = new StringBuilder();
						stringBuilder.append(s.charAt(i));

					} else if (s.charAt(i) == '>' && !withinLiteral
							&& !withinJava && !withinJavaInline
							&& !withinJavaScript && !withinCSS) {

						stringBuilder.append(s.charAt(i));
						htmlEntities.add(new HtmlEntity(stringBuilder
								.toString()));
						stringBuilder = new StringBuilder();

					} else if (tagType == TagType.CLOSE_ANY_JAVA && withinJava) {
						stringBuilder.append(s.charAt(i));
						htmlEntities.add(new HtmlEntity(stringBuilder
								.toString()));
						withinJava = false;
						stringBuilder = new StringBuilder();

					} else if (tagType == TagType.CLOSE_ANY_JAVA
							&& withinJavaInline) {
						stringBuilder.append(s.charAt(i));
						withinJavaInline = false;

					} else {
						stringBuilder.append(s.charAt(i));
					}

				}

			}
			if (stringBuilder.toString().trim().length() > 0) {
				System.out.println("Failure at ");
				System.out.println(stringBuilder.toString());
			}
			// check for empty tags
			boolean startTag = false;
			for (HtmlEntity htmlEntity : htmlEntities) {

				if (htmlEntity.getTagType() == 'E' && startTag && !"div".equals(htmlEntity.getTagName())) {
					System.out.println("Empty tag at level " + htmlEntity);					
				}
				if (htmlEntity.getTagType() == 'S') {
					startTag = true;
				} else {
					startTag = false;
				}
					
			}
			
			Map<Integer, HtmlEntity> map = new HashMap<Integer, HtmlEntity>();
			int level = -1;
			for (HtmlEntity htmlEntity : htmlEntities) {

				if (htmlEntity.getTagType() == 'S') {
					level++;
					map.put(level, htmlEntity);
				}

				StringBuilder spacing = new StringBuilder();
				for (int i = 0; i < level; i++) {
					spacing.append(SiteConstants.SPACING_FOR_LEVEL);
				}
				if (htmlEntity.getTagType() == 'M' && level >= 0) {
					spacing.append(SiteConstants.SPACING_FOR_LEVEL);
				}
				if (debug) {
					outList.add(String.format("%02d", level) + htmlEntity.getTagType() + spacing
							+ htmlEntity.toString());
				} else {
					outList.add(spacing + htmlEntity.toString());
				}
				if (htmlEntity.getTagType() == 'E') {

					HtmlEntity h = map.get(level);
					if (h == null
							|| !h.getTagName().equals(htmlEntity.getTagName())) {
						System.out.println("Unmatched tag at level " + level
								+ " " + h + " " + htmlEntity);
						if (debug) {
							outList.add("// " + "Unmatched tag at level "
									+ level + " " + h + " " + htmlEntity);
						}
					}
					level--;
				}

			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("format filename");
		System.out.println("fo filename");
		System.out.println("debug filename");
		System.out.println("exit");
		ShellFactory.createConsoleShell("hello", null, new FormatHtmlJsp())
				.commandLoop();
	}

}
