import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.doorgroup.portalshared.utilities.DataFormatter;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;

/**
 * Reads a text file in and formats it. Writes to outputDirectory out.txt
 * 
 * @param fileName
 * @return
 * @throws IOException
 */

public class ManipulateText {

	/**
	 * Sorts all words on each separate line . Writes to outputDirectory out.txt
	 * 
	 */
	@Command
	public String sortline(String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();
			for (String s : list) {
				String[] words = s.split(" ");
				List<String> wordsList = Arrays.asList(words);
				Collections.sort(wordsList);
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < words.length; i++) {
					stringBuilder.append(words[i]);
					if (i < words.length - 1) {
						stringBuilder.append(' ');
					}
				}

				outList.add(stringBuilder.toString());
			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * amends all lines like a b c. to first b c middle a last. i.e. breaks line
	 * on first space.
	 * 
	 */
	@Command
	public String swapfirst(final String fileName, String first, String middle,
			String last) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();

			for (String s : list) {

				String trimmedString = s.trim();
				String outString = trimmedString;
				int index = trimmedString.indexOf(' ');
				if (index > -1) {
					outString = first + trimmedString.substring(index) + middle
							+ trimmedString.substring(0, index) + last;

				}

				outList.add(outString);

			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * amends all lines like a b c. to first c middle a b last. i.e. breaks line
	 * on last space.
	 * 
	 */
	@Command
	public String swaplast(final String fileName, String first, String middle,
			String last) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();

			for (String s : list) {

				String trimmedString = s.trim();
				String outString = trimmedString;
				int index = trimmedString.lastIndexOf(' ');

				if (index > -1) {
					outString = first + trimmedString.substring(index) + middle
							+ trimmedString.substring(0, index) + last;

				}

				outList.add(outString);

			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * takes list of variables and concatenates names for a debug line eg
	 * private String yyy; private String zzz; becomes yyy + " : " + zzz
	 * 
	 */
	@Command
	public String debugline(final String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();

			for (String s : list) {
				String trimmedString = s.trim().replaceAll(";", "")
						.replaceAll("\\s+", "~");
				String[] parts = trimmedString.split("~");
				outList.add(parts[2]);
			}
			String outString = "";
			int i = 0;
			for (String s : outList) {
				i++;
				outString += s;
				if (i < outList.size()) {
					outString += " + \" : \" + ";
				}
			}
			System.out.println(outString);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * changes lines like a.equals("b") to "b".equals(a).
	 * 
	 */
	@Command
	public String swapeq(final String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();

			Pattern pattern = Pattern
					.compile("([^(]+)[(]([!]{0,1})([a-zA-Z_]+)[.]equals[(]\"([A-Za-z0-9_]+)\"[)](.+)");

			for (String s : list) {

				String outString = s;

				Matcher matcher = pattern.matcher(s);
				boolean matchFound = matcher.find();

				if (matchFound) {
					System.out.println("match " + s);
					outString = matcher.group(1) + '(' + matcher.group(2) + '"'
							+ matcher.group(4) + '"' + ".equals("
							+ matcher.group(3) + ")" + matcher.group(5);
				}
				outList.add(outString);

			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * swap equal on a single line changes 1 line like a.equals("b") to
	 * "b".equals(a).
	 * 
	 */
	@Command
	public String se(final String line) {

		Pattern pattern = Pattern
				.compile("([a-zA-Z_]+)[.]equals[(]\"([A-Za-z0-9_]+)\"[)]");

		String outString = line;

		Matcher matcher = pattern.matcher(line);
		boolean matchFound = matcher.find();

		if (matchFound) {
			outString = "\"" + matcher.group(2) + '"' + ".equals("
					+ matcher.group(1) + ")";
			System.out.println(outString);
		} else {
			System.out.println("no match<<<" + outString + ">>>");
		}

		return "Success";
	}

	/*
	 * Takes a string in format @inUserName char(11), @inCustNo char(8),
	 * 
	 * @inPartNo char(15) Builds necessary dot net lines for nhibernate method
	 */
	@Command
	public String storedproc(final String line) {
		try {
			List<String> outList = new ArrayList<String>();

			String[] fields = line.split(",");

			List<String> methodStatement = new ArrayList<String>();
			List<String> debugStatement = new ArrayList<String>();
			List<String> sql = new ArrayList<String>();
			List<String> setStatements = new ArrayList<String>();
			List<String> configStatements = new ArrayList<String>();
			for (int i = 0; i < fields.length; i++) {
				String[] parts = fields[i].trim().split(" ");
				String name = parts[0].substring(1);
				String type = "string";
				if (parts[1].contains("int")) {
					type = "int";
				}
				methodStatement.add(type + " " + name + ',');
				debugStatement.add(name + " + \" : \" + ");
				sql.add("@" + name + " =:" + name + ", ");
				configStatements.add(":" + name + ", ");
				if ("int".equals(type)) {
					setStatements.add("query.SetInt32(\"" + name + "\", "
							+ name + ");\r\n");
				} else {
					setStatements.add("query.SetString(\"" + name + "\", "
							+ name + ");\r\n");
				}
			}

			outList.addAll(methodStatement);
			outList.add("\r\n");
			outList.addAll(debugStatement);
			outList.add("\r\n");
			outList.addAll(sql);
			outList.add("\r\n");
			outList.addAll(setStatements);
			outList.add("\r\n");
			outList.addAll(configStatements);
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, false);

		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	/**
	 * Sorts all words on each separate line . Writes to outputDirectory out.txt
	 * 
	 */
	@Command
	public String vb(String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));
			List<String> outList = new ArrayList<String>();
			for (String s : list) {
				s += " ";
				String sConverted = "";
				// dim
				if (s.contains(" Dim ")) {
					String[] words = s.split("[ ]+");
					String type = words[4].toLowerCase();
					type = type.replace("integer", "int");
					type = type.replace("date", "Date");
					type = type.replace("string", "String");
					sConverted = type + " " + words[2] + ";";
				} else if (s.contains(" ElseIf ")) {
					sConverted = s.replaceFirst("ElseIf ", "} else if (")
							.replaceFirst("Then", ") {");
				} else if (s.contains(" End If")) {
					sConverted = s.replaceFirst("End If", "}");
				} else if (s.contains(" If ")) {
					sConverted = s.replaceFirst("If ", "if (").replaceFirst(
							"Then", ") {");
				} else if (s.contains(" Else ")) {
					sConverted = s.replaceFirst("Else ", "} else {");
				} else if (s.contains(" For ")) {
					sConverted = s.replaceFirst("For ", "for (");
					sConverted += ") {";
				} else if (s.contains(" Next ")) {
					sConverted = s.replaceFirst("Next ", "}");
				} else if (s.contains(" While ")) {
					sConverted = s.replaceFirst("While ", "while (");
					sConverted += ") {";
				} else if (s.contains(" Wend ")) {
					sConverted = s.replaceFirst("Wend ", "}");
				} else if (DataFormatter.hasValue(s)) {
					sConverted = s + ";";
				} else {
					sConverted = s;
				}

				if (sConverted.contains(" Not ")
						|| sConverted.contains("(Not ")) {
					sConverted = sConverted.replaceFirst("Not ", "!");
				}
				if (sConverted.contains(" And ")) {
					sConverted = sConverted.replaceFirst("And ", "&& ");
				}
				if (sConverted.contains(" Or ")) {
					sConverted = sConverted.replaceFirst("Or ", "!! ");
				}

				sConverted = sConverted.replaceFirst(" ;", ";");
				outList.add(sConverted);
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
		System.out.println("swapfirst filename first middle last");
		System.out.println("swaplast filename first middle last");
		System.out.println("swapeq filename");
		System.out.println("sortline filename");
		System.out.println("se code_snippet");
		System.out.println("storedproc code_snippet");
		System.out.println("vb filename");		
		ShellFactory.createConsoleShell("hello", null, new ManipulateText())
				.commandLoop();
	}

}
