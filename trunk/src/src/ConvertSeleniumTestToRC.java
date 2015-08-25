import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.SiteConstants;

public class ConvertSeleniumTestToRC {

	/**
	 * This allows you to convert selenium tests in HTML format to tests in
	 * selenium remote control format. Unmatched commands are reported, as not
	 * every possible command has been converted.
	 * 
	 * All files in directory are converted, and merged into one output file
	 * OUTPUT_DIRECTORY out.txt. Filenames are written as comments in the output
	 * file.
	 * 
	 * To ignore certain files, put their names as regular expressions in IGNORE_FILES.
	 */

	private List<String> IGNORE_FILES = Arrays.asList("TestSuite",
			"manual-step");
	@Command
	public String convert(String directory) {
		try {
			File dataDir = new File(FileUtilities.lastParameter(directory));
			String[] suffix = {"html"};

			List<String> outList = new ArrayList<String>();

			index(dataDir, suffix, outList);

			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
					+ "out.txt", outList, true);

		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("convert full-path-to-directory");
		ShellFactory.createConsoleShell("hello", null,
				new ConvertSeleniumTestToRC()).commandLoop();
	}

	private void index(File dataDir, String[] suffix, List<String> outList)
			throws Exception {

		indexDirectory(dataDir, suffix, outList);

	}

	private void indexDirectory(File dataDir, String[] suffix,
			List<String> outList) throws IOException {

		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(f, suffix, outList);
			} else {
				indexFileWithIndexWriter(f, suffix, outList);
			}
		}

	}

	private void indexFileWithIndexWriter(File f, String[] suffix,
			List<String> outList) throws IOException {

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

		for (String s : this.IGNORE_FILES) {
			Pattern pattern = Pattern.compile(s);
			Matcher matcher = pattern.matcher(f.getCanonicalPath());
			if (matcher.find()) {
				System.out.println("ignore " + f.getCanonicalPath());
				return;
			}
		}

		int j = f.getCanonicalPath().lastIndexOf("\\");

		outList.add("// " + f.getCanonicalPath().substring(j + 1));

		org.jsoup.nodes.Document d = Jsoup.parse(f, "UTF-8");

		Elements trs = d.getElementsByTag("tr");

		for (Element tr : trs) {

			Elements tds = tr.getElementsByTag("td");

			if ("open".equals(tds.get(0).text())) {

				outList.add("this.selenium.open(\"" + tds.get(1).text()
						+ "\");");
				outList.add("this.selenium.waitForPageToLoad(\"30000\");");

			} else if ("verifyTextPresent".equals(tds.get(0).text())) {

				outList.add("assertTrue(this.selenium.isTextPresent(\""
						+ tds.get(1).text() + "\"));");
			} else if ("runScript".equals(tds.get(0).text())) {

				outList.add("this.selenium.runScript(\"" + tds.get(1).text()
						+ "\");");
			} else if ("type".equals(tds.get(0).text())) {

				outList.add("this.selenium.type(\"" + tds.get(1).text()
						+ "\", \"" + tds.get(2).text() + "\");");
			} else if ("select".equals(tds.get(0).text())) {

				outList.add("this.selenium.select(\"" + tds.get(1).text()
						+ "\", \"" + tds.get(2).text() + "\");");
			} else if ("verifyValue".equals(tds.get(0).text())) {

				outList.add("assertEquals(this.selenium.getValue(\""
						+ tds.get(1).text() + "\"), \"" + tds.get(2).text()
						+ "\");");
			} else if ("submit".equals(tds.get(0).text())) {

				outList.add("this.selenium.submit(\"" + tds.get(1).text()
						+ "\");");
			} else if ("goBack".equals(tds.get(0).text())) {

				outList.add("this.selenium.goBack();");
				outList.add("this.selenium.waitForPageToLoad(\"10000\");");
			} else if ("assertAlert".equals(tds.get(0).text())) {

				outList.add("assertTrue(this.selenium.isAlertPresent());");
				outList.add("this.selenium.getAlert();");
				outList.add("Thread.sleep(5000);");
			} else if ("assertConfirmation".equals(tds.get(0).text())) {

				outList
						.add("assertTrue(this.selenium.isConfirmationPresent());");
				outList.add("this.selenium.getConfirmation();");	
				outList.add("Thread.sleep(5000);");				
			} else if ("setTimeout".equals(tds.get(0).text())) {

				outList.add("this.selenium.setTimeout(\"" + tds.get(1).text()
						+ "\");");
			} else if ("pause".equals(tds.get(0).text())) {

				outList.add("Thread.sleep(" + tds.get(1).text() + ");");
			} else if ("assertTitle".equals(tds.get(0).text())) {

				outList.add("assertEquals(this.selenium.getTitle(), \""
						+ tds.get(1).text() + "\");");

			} else if ("clickAndWait".equals(tds.get(0).text())) {

				outList.add("this.selenium.click(\"" + tds.get(1).text()
						+ "\");");
				outList.add("this.selenium.waitForPageToLoad(\"30000\");");
			} else if ("selectAndWait".equals(tds.get(0).text())) {

				outList.add("this.selenium.select(\"" + tds.get(1).text()
						+ "\",\"" + tds.get(2).text() + "\");");
				outList.add("this.selenium.waitForPageToLoad(\"30000\");");
			} else if ("click".equals(tds.get(0).text())) {

				outList.add("this.selenium.click(\"" + tds.get(1).text()
						+ "\");");
			} else if (Character.isDigit(tds.get(0).text().substring(0, 1)
					.charAt(0))) {
				// ignore title
			} else {
				System.out.println("Unmatched " + tds.get(0).text() + " "
						+ f.getCanonicalPath());
			}
		}

	}
}
