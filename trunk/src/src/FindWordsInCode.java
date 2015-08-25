import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.SiteConstants;

public class FindWordsInCode {

	private static Map<String, Integer> map = new HashMap<String, Integer>(
			10000);

	/**
	 * 
	 * This allows you to find all words in files (that match suffix) in specified directory. 
	 * They are written to the out.txt in the output directory to be spell checked by, for example, MS Word
	 * 
	 * Words are divided by any non letter, and by the first capital letter within a string.
	 * For example abc1def becomes abc, def
	 * abcDef become abc, Def
	 * 
	 */
	@Command
	public String parse(String directory) {

		System.out.println("Starting");

		try {
			File dataDir = new File(directory);
			String[] suffix = { "java" };

			index(dataDir, suffix);

			List<String> outList = new ArrayList<String>(10000);
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				outList.add(pairs.getKey() + " " + pairs.getValue());
			}

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
		System.out.println("parse full-path-to-directory");
		ShellFactory.createConsoleShell("hello", null, new FindWordsInCode())
				.commandLoop();
	}

	private void index(File dataDir, String[] suffix) throws Exception {

		indexDirectory(dataDir, suffix);

	}

	private void indexDirectory(File dataDir, String[] suffix)
			throws IOException {

		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(f, suffix);
			} else {
				indexFileWithIndexWriter(f, suffix);
			}
		}

	}

	private void indexFileWithIndexWriter(File f, String[] suffix)
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
		System.out.println(f.getCanonicalPath());
		String fileContents = FileUtilities.readFileIntoString(f
				.getCanonicalPath());

		fileContents = fileContents.replaceAll("[^a-zA-Z]", " ");
		fileContents = fileContents.replaceAll("([a-z])([A-Z])", "$1 $2");
		String[] words = fileContents.split("[ ]+");
		for (int i = 0; i < words.length; i++) {
			storeWord(words[i]);
		}

	}

	private void storeWord(String word) {

		if (map.get(word) == null) {
			map.put(word, 1);
		} else {
			Integer integer = map.get(word);
			integer++;
			map.put(word, integer);
		}
	}
}
