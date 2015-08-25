package clicheUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.MethodDetail;

public class JavaSoup {

	public static List<MethodDetail> findMethods(final String fileName)
			throws IOException {

		List<MethodDetail> result = new ArrayList<MethodDetail>();

		List<String> list = FileUtilities.readFileIntoList(FileUtilities
				.lastParameter(fileName));

		boolean withinMethod = false;

		int levelInMethod = -1;
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);

			// ignore comments
			if (s.startsWith("//")) {
				// ignore
			} else if (s.contains("package ")) {
				// ignore

			} else if (s.contains("public class ")) {
				SourceUtilities.extractClassName(s);
				// ignore

			} else if (!withinMethod
					&& s.contains("(")
					&& !s.contains("=")
					&& (s.contains("private ") || s.contains("public ") || s
							.contains("protected "))) {

				MethodDetail methodDetail = new MethodDetail();

				methodDetail.setName(SourceUtilities.extractMethodName(s));
				methodDetail.setProtectionLevel(SourceUtilities
						.extractProtectionLevel(s));
				methodDetail
						.setReturnType(SourceUtilities.extractReturnType(s));

				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < 5; j++) {
					sb.append(s);
					if (!s.contains("{")) {
						i++;
						s = list.get(i);
					} else {
						break;
					}
				}

				methodDetail.setParameters(SourceUtilities.extractParameters(sb
						.toString()));

				// do not write constructor
				if (methodDetail.getName() != null
						&& !methodDetail.getName().isEmpty()) {
					result.add(methodDetail);
				}

				withinMethod = true;
				levelInMethod = 1;
			}
			// break out return
			else if (withinMethod && levelInMethod > 1 && s.contains("return ")) {
				// ignore
			} // normal return
			else if (withinMethod && levelInMethod == 1
					&& s.contains("return ")) {
				withinMethod = false;
				// no level change
			} else if (withinMethod && s.contains("{") && s.contains("}")) {
				// ignore
			} else if (withinMethod && s.contains("{")) {
				levelInMethod++;
				// implied return
			} else if (withinMethod && s.contains("}") && levelInMethod == 1) {
				withinMethod = false;
			} else if (withinMethod && s.contains("}") && levelInMethod > 1) {
				levelInMethod--;
			} else {
				// ignore
			}

		}

		return result;
	}

	public static List<String> findConstants(final String fileName)
			throws IOException {

		List<String> result = new ArrayList<String>();

		List<String> list = FileUtilities.readFileIntoList(FileUtilities
				.lastParameter(fileName));

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			boolean endOfLogicalLine = false;
			// ignore comments
			if (s.startsWith("//")) {
				// ignore
			} else if (s.contains(" final ") && s.contains(" static ") && s.contains(" String ")) {
				String multipleLines = "";
				do {
					int j = s.indexOf('"');
					int k = s.lastIndexOf('"');
					if (j > -1 && k > -1) {
						multipleLines += s.substring(j + 1, k);
					}
					if (s.contains(";")) {
						endOfLogicalLine = true;
					} else {
						i++;
						s = list.get(i);
					}
				} while (!endOfLogicalLine && i < list.size());
				result.add(multipleLines);
			} else {
				// ignore
			}

		}

		return result;
	}
}
