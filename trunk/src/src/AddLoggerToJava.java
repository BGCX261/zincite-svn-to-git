import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.MethodDetail;
import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.JavaSoup;
import clicheUtils.SiteConstants;
import clicheUtils.SourceUtilities;

public class AddLoggerToJava {

	private static final String LINE_A_START = "\tprivate static final Logger LOGGER = Logger.getLogger(";

	private static final String LINE_A_END = ".class);\n";

	private static final String LINE_B_START = "\t\tLOGGER.info(String.format(\"{method='";

	private static final String LINE_B_END = "));";

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

			List<MethodDetail> methodDetails = JavaSoup.findMethods(fileName);

			List<String> outList = new ArrayList<String>();

			boolean withinMethod = false;

			MethodDetail methodDetail = null;

			int levelInMethod = -1;
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);

				// ignore comments
				if (s.startsWith("//")) {
					outList.add(s);
				} else if (s.contains("package ")) {
					outList.add(s);
					outList.add("import org.apache.log4j.Logger;\n");

				} else if (s.contains("public class ")) {
					String className = SourceUtilities.extractClassName(s);
					outList.add(s);
					outList.add(LINE_A_START + className + LINE_A_END);

				} else if (!withinMethod
						&& s.contains("(")
						&& !s.contains("=")
						&& !s.contains(" get")
						&& !s.contains(" set")
						&& ( s.contains("private ") ||  s
								.contains("public ") || s
								.contains("protected "))) {
					outList.add(s);
					String methodName = SourceUtilities.extractMethodName(s);
					for (int j = 0; j < 5; j++) {
						if (!s.contains("{")) {
							i++;
							s = list.get(i);
							outList.add(s);
						}
					}
					methodDetail = getDetailsForMethod(methodName,
							methodDetails);

					outList.add(buildEnterString(methodDetail));

					withinMethod = true;
					levelInMethod = 1;
				}
				// break out return
				else if (withinMethod && levelInMethod > 1
						&& (s.contains("return ") || s.contains("return;"))) {
					outList.add(buildReturnString(methodDetail,s));
					outList.add(s);
				} // normal return
				else if (withinMethod && levelInMethod == 1
						&& (s.contains("return ") || s.contains("return;"))) {
					outList.add(buildReturnString(methodDetail,s));
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
					outList.add(buildReturnString(methodDetail,s));
					outList.add(s);
					withinMethod = false;
				} else if (withinMethod && s.contains("}") && levelInMethod > 1) {
					levelInMethod--;
					outList.add(s);
				} else {
					outList.add(s);
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

	private MethodDetail getDetailsForMethod(String methodName,
			List<MethodDetail> methodDetails) {

		MethodDetail result = null;

		for (MethodDetail methodDetail : methodDetails) {
			if (methodName.equals(methodDetail.getName())) {
				result = methodDetail;
				break;
			}

		}
		return result;

	}

	private String buildEnterString(MethodDetail methodDetail) {

		StringBuffer result = new StringBuffer();

		if (methodDetail == null) {
			return result.toString();
		}
		result.append(LINE_B_START + methodDetail.getName()
				+ "'; step='enter'; ");

		if (methodDetail.getParameters().size() > 0) {
			for (Map.Entry<String, String> entry : methodDetail.getParameters()
					.entrySet()) {
				if ("int".equalsIgnoreCase(entry.getValue())
						|| "Integer".equalsIgnoreCase(entry.getValue())
						|| "long".equalsIgnoreCase(entry.getValue())
						|| "float".equalsIgnoreCase(entry.getValue())) {
					result.append(entry.getKey() + "='%d'; ");
				} else {
					result.append(entry.getKey() + "='%s'; ");
				}
			}
			result.append("}\"");
			for (Map.Entry<String, String> entry : methodDetail.getParameters()
					.entrySet()) {
				result.append(",");
				result.append(entry.getKey());
			}

		} else {
			result.append("}\"");
		}
		result.append(LINE_B_END);

		return result.toString();
	}

	private String buildReturnString(MethodDetail methodDetail,
			String returnStatement) {

		StringBuffer result = new StringBuffer();
		if (methodDetail == null) {
			return result.toString();
		}
		result.append(LINE_B_START + methodDetail.getName()
				+ "'; step='leave'; ");
		if (!returnStatement.contains(" result")) {
			result.append("}\"");
		} else if (methodDetail.getReturnType().equals("void")) {
			result.append("}\"");
		} else if (methodDetail.getReturnType().contains("List")
				|| methodDetail.getReturnType().contains("Map")
				|| methodDetail.getReturnType().contains("Vector")
				|| methodDetail.getReturnType().contains("Collection")) {
			result.append("result='%d'; ");
			result.append("}\"");
			result.append(",result.size() ");
		} else if (methodDetail.getReturnType().equals("int")) {
			result.append("result='%d'; ");
			result.append("}\"");
			result.append(",result ");
		} else {
			result.append("result='%s'; ");
			result.append("}\"");
			result.append(",result ");
		}

		result.append(LINE_B_END);

		return result.toString();
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("add filename");
		System.out.println("exit");
		ShellFactory.createConsoleShell("hello", null, new AddLoggerToJava())
				.commandLoop();
	}

}
