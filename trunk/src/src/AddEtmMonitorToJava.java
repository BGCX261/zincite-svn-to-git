
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;
import clicheUtils.SourceUtilities;

public class AddEtmMonitorToJava {

	private static final String LINE_A = "private static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();\n";

	private static final String LINE_B_START = "EtmPoint point = etmMonitor.createPoint(\"";

	private static final String LINE_B_END = "\");\n";

	private static final String LINE_C = "point.collect();\n";

	/**
	 * Reads a java file and adds EtmMonitor code to all methods Writes to
	 * outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@Command
	public String etm(final String fileName) {
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));

			List<String> outList = new ArrayList<String>();

			boolean withinMethod = false;

			String className = null;

			int levelInMethod = -1;
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);

				// ignore comments
				if (s.startsWith("//")) {
					outList.add(s);
				} else if (s.contains("package ")) {
					outList.add(s);
					outList.add("import etm.core.configuration.EtmManager;\n");
					outList.add("import etm.core.monitor.EtmMonitor;\n");
					outList.add("import etm.core.monitor.EtmPoint;\n");

				} else if (s.contains("public class ")) {
					className = SourceUtilities.extractClassName(s);
					outList.add(s);
					outList.add(LINE_A);

				} else if (!withinMethod
						&& s.contains("(")
						&& !s.contains("=")
						&& (s.contains("private ") || s.contains("public ") || s
								.contains("protected "))) {
					outList.add(s);
					String methodName = SourceUtilities.extractMethodName(s);
					if (!s.contains("{")) {
						i++;
						s = list.get(i);
						outList.add(s);
					}
					outList.add(LINE_B_START + className + ":" + methodName
							+ LINE_B_END);
					withinMethod = true;
					levelInMethod = 1;
				}
				// break out return
				else if (withinMethod && levelInMethod > 1
						&& s.contains("return ")) {
					outList.add(LINE_C);
					outList.add(s);
				} // normal return
				else if (withinMethod && levelInMethod == 1
						&& s.contains("return ")) {
					outList.add(LINE_C);
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
					outList.add(LINE_C);
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
		System.out.println("etm filename");
		ShellFactory.createConsoleShell("hello", null,
				new AddEtmMonitorToJava()).commandLoop();
	}

}
