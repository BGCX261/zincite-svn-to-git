
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;
import clicheUtils.SourceUtilities;

public class AddLoggerToDotnet {

	private static final String LINE_A_START = "\tprivate static readonly ILog log = LogManager.GetLogger(typeof(";

	private static final String LINE_A_END = "));\n";

	private static final String LINE_B_START = "\t\tlog.Info(\"Enter ";

	private static final String LINE_B_END = "\");\n";

	private static final String LINE_C_START = "\t\tlog.Info(\"Leave ";

	private static final String LINE_C_END = "\");\n";

	/**
	 * Reads a java file and adds logging code to all methods Writes to
	 * outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@Command
	public String add(final String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));

			List<String> outList = new ArrayList<String>();

			boolean withinMethod = false;
			boolean addedUsing = false;
			String methodName = "";

			int levelInMethod = -1;
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);

				// ignore comments
				if (s.startsWith("//")) {
					outList.add(s);
				} else if (s.contains("using ") && !addedUsing) {
					addedUsing = true;
					outList.add(s);
					outList.add("using log4net;\n");

				} else if (s.contains("public class ")) {
					String className = SourceUtilities.extractClassName(s);
					outList.add(s);
					outList.add(LINE_A_START + className + LINE_A_END);

				} else if (!withinMethod
						&& s.contains("(")
						&& !s.contains("=")
						&& (s.contains("private ") || s.contains("public ") || s
								.contains("protected "))) {
					outList.add(s);
					methodName = SourceUtilities.extractMethodName(s);
					for (int j = 0; j < 5; j++) {
						if (!s.contains("{")) {
							i++;
							s = list.get(i);
							outList.add(s);
						}
					}
					outList.add(LINE_B_START + methodName + LINE_B_END);
					withinMethod = true;
					levelInMethod = 1;
				}
				// break out return
				else if (withinMethod && levelInMethod > 1
						&& s.contains("return ")) {
					outList.add(LINE_C_START + methodName + LINE_C_END);
					outList.add(s);
				} // normal return
				else if (withinMethod && levelInMethod == 1
						&& s.contains("return ")) {
					outList.add(LINE_C_START + methodName + LINE_C_END);
					outList.add(s);
					withinMethod = false;
					// no level change
				} else if (withinMethod && s.contains("{") && s.contains("}")) {
					outList.add(s);
				} else if (withinMethod && s.contains("{")) {
					levelInMethod++;
					outList.add(s);
					// implied return
				} else if (withinMethod && s.contains("}")
						&& levelInMethod == 1) {
					outList.add(LINE_C_START + methodName + LINE_C_END);
					outList.add(s);
					withinMethod = false;
				} else if (withinMethod && s.contains("}") && levelInMethod > 1) {
					levelInMethod--;
					outList.add(s);
				} else {
					outList.add(s);
				}

			}
			FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY + "out.txt", outList, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("add filename");
		System.out.println("exit");
		ShellFactory.createConsoleShell("hello", null, new AddLoggerToDotnet())
				.commandLoop();
	}




}
