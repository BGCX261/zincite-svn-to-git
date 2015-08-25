
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;

/**
 * This utility checks the contents of two different tags and reports any differences
 * If a tag exists more than once, the first occurrence is used
 * If either tag does not exist, that is reported also.
 * 
 * @author EvansNW
 *
 */
public class CheckTagIntegrity {

	@Command
	public String check(String tag1, String tag2) {
		try {
			File dataDir = new File(SiteConstants.SOURCE_DIRECTORY);
			String[] suffix = {"jsp"};

			index(dataDir, suffix, tag1, tag2);
			
		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}



	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("check tag1 tag2");
		ShellFactory.createConsoleShell("hello", null, new CheckTagIntegrity())
				.commandLoop();
	}

	private void index(File dataDir, String[] suffix, String tag1, String tag2) throws Exception {

		indexDirectory(dataDir, suffix, tag1, tag2);

	}

	private void indexDirectory(File dataDir, String[] suffix, String tag1, String tag2)
			throws IOException {

		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(f, suffix, tag1, tag2);
			} else {
				indexFileWithIndexWriter(f, suffix, tag1, tag2);
			}
		}

	}

	private void indexFileWithIndexWriter(File f, String[] suffix, String tag1, String tag2)
			throws IOException {

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
		org.jsoup.nodes.Document d = Jsoup.parse(f, "UTF-8");

		Elements tag1Elements = d.getElementsByTag(tag1);

		String tag1Content = null;
		for (Element element : tag1Elements) {
			tag1Content = element.text();
			break;
		}
		Elements tag2Elements = d.getElementsByTag(tag2);

		String tag2Content = null;
		for (Element element : tag2Elements) {
			tag2Content = element.text();
			break;
		}
		if (tag1Content == null) {
			System.out.println("No " + tag1 + " for "
					+ f.getCanonicalPath());
		} else if (tag2Content == null) {
			System.out.println("No " + tag2 + " for "
					+ f.getCanonicalPath());
		} else if (!tag2Content.equals(tag1Content)) {
			System.out.println("No match for "
					+ f.getCanonicalPath());
		}

	}
}
