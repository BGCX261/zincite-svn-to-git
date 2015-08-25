
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;


public class FindStringInProperty {

	/**
	 * 
	 * This allows you to find a string or regular expression within a specific
	 * property in all files in SOURCE_DIRECTORY.
	 * 
	 */
	@Command
	public String find(String expression, String property, String tag) {
		try {
			File dataDir = new File(SiteConstants.SOURCE_DIRECTORY);
			String[] suffix = {"jsp"};

			index(dataDir, suffix, expression, property, tag);

		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("find expression property tag");
		ShellFactory.createConsoleShell("hello", null,
				new FindStringInProperty()).commandLoop();
	}

	private void index(File dataDir, String[] suffix, String expression,
			String property, String tag) throws Exception {

		indexDirectory(dataDir, suffix, expression, property, tag);

	}

	private void indexDirectory(File dataDir, String[] suffix,
			String expression, String property, String tag) throws IOException {

		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(f, suffix, expression, property, tag);
			} else {
				indexFileWithIndexWriter(f, suffix, expression, property, tag);
			}
		}

	}

	private void indexFileWithIndexWriter(File f, String[] suffix,
			String expression, String property, String tag) throws IOException {

		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
			return;
		}
		if (suffix != null) {
			boolean suffixMatched = false;
			for (String s : suffix) {
				if (f.getName().endsWith(s)) {
					suffixMatched = true;
				}
			}
			if (!suffixMatched) {
				return;
			}
		}
		int j = f.getCanonicalPath().lastIndexOf("\\");

		// Compile and use regular expression
		Pattern pattern = Pattern.compile(expression);

		org.jsoup.nodes.Document d = Jsoup.parse(f, "UTF-8");

		Elements tags = d.getElementsByTag(tag);

		for (Element element : tags) {
			if (element.attr(property) != null) {

				Matcher matcher = pattern.matcher(element.attr(property));
				boolean matchFound = matcher.find();

				if (matchFound) {
					System.out.println("Expression in file "
							+ f.getCanonicalPath().substring(j + 1) + " : "
							+ element.attr(property));
				}
			}
		}

	}
}
