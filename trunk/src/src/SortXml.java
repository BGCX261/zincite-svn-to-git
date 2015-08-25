
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.SiteConstants;

public class SortXml {

	/**
	 * Sorts an XML file by property name. Writes to outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 */
	private static String propertyName;
	@Command
	public String sort(String fileName, String pPropertyName) {
		
		propertyName = pPropertyName;
		
		try {
			Document d = Jsoup
					.parse(new File(FileUtilities.lastParameter(fileName)), "UTF-8");

			sortChildren(d);

			FileUtilities.writeFileFromString(SiteConstants.OUTPUT_DIRECTORY + "out.txt", d.body()
					.children().toString());

		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("sort filename propertyname");
		ShellFactory.createConsoleShell("hello", null, new SortXml())
				.commandLoop();
	}

	private static void sortChildren(Element e) {

		List<Element> el = e.children();
		for (Element ee : el) {
			ee.remove();
			sortChildren(ee);
		}

		Collections.sort(el, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				return e1.attr(propertyName).compareTo(e2.attr(propertyName));
			}
		});

		for (Element ee : el) {
			e.appendChild(ee);
		}
	}
}
