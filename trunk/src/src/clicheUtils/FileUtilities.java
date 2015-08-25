package clicheUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtilities {

	private FileUtilities() {
		// cannot instantiate
	}
	public static List<String> readFileIntoList(String fileName)
			throws IOException {
		FileInputStream in = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		List<String> result = new ArrayList<String>();
		String strLine;

		while ((strLine = br.readLine()) != null) {
			result.add(strLine);
		}

		in.close();
		return result;
	}
	public static String readFileIntoString(String fileName) throws IOException {
		FileInputStream in = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		StringBuffer result = new StringBuffer();
		String strLine;

		while ((strLine = br.readLine()) != null) {
			result.append(strLine);
		}

		in.close();
		return result.toString();
	}

	public static void writeFileFromList(String filename, List<String> list,
			boolean addLineFeed) throws IOException {
		java.io.FileWriter fileWriter = new java.io.FileWriter(filename);

		for (String s : list) {

			fileWriter.write(s);
			if (addLineFeed) {
				fileWriter.write(SiteConstants.LINE_FEED);
			}

		}

		fileWriter.flush();

		fileWriter.close();
		return;
	}

	public static void writeFileFromString(String filename, String s)
			throws IOException {
		java.io.FileWriter fileWriter = new java.io.FileWriter(filename);

		fileWriter.write(s);

		fileWriter.flush();

		fileWriter.close();
		return;
	}
	public static boolean createDirectoryIfDoesntExist(String directory) throws Exception {

		boolean result = false;
		
		File dir = new File(directory);

		if (!dir.exists()) {
			result = dir.mkdir();
			if (!result) {
				throw new Exception("directory does not exist and cannot be created");
			}
		}

		return result;
	}
	public static String lastParameter(String parameter) throws IOException {
		if ("-l".equals(parameter)) {
			List<String> list = FileUtilities
					.readFileIntoList(SiteConstants.OUTPUT_DIRECTORY
							+ "parameter.txt");
			System.out.println("-l replaced with " + list.get(0));
			return list.get(0);
		}
		List<String> list = Arrays.asList(parameter);
		FileUtilities.writeFileFromList(SiteConstants.OUTPUT_DIRECTORY
				+ "parameter.txt", list, true);
		return parameter;
	}
}
