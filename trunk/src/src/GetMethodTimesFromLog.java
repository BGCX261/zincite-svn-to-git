import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.FileUtilities;
import clicheUtils.SiteConstants;

public class GetMethodTimesFromLog {

	/**
	 * Reads a JSON format log file and calculates the times for dao methods.
	 * Writes to outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 * @throws IOException
	 */

	private static final String NET_DAO = "GOrder.Daos.";
	private static final String JAVA_DAO = "Dao";

	/* .net times one to left from java */
	private long getTime(String s) throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		// Date date = dateFormat.parse(s.substring(11, 19));
		Date date = dateFormat.parse(s.substring(12, 20));
		long milliseconds = date.getTime();
		// milliseconds += Integer.parseInt(s.substring(20, 23));
		milliseconds += Integer.parseInt(s.substring(21, 24));

		return milliseconds;
	}

	@Command
	public String times(final String fileName) throws Exception {

		Map<String, Long> methodStartTimes = new HashMap<String, Long>();
		try {
			List<String> list = FileUtilities.readFileIntoList(FileUtilities
					.lastParameter(fileName));

			List<String> outList = new ArrayList<String>();

			String className = "";

			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);

				if (s.indexOf("[") > 0 && s.indexOf("]") > 0) {

					className = s.substring(1 + s.indexOf("["), s.indexOf("]"));
					int startOfJSON = s.indexOf("{");
					if (startOfJSON > -1) {

						String jsonString = s.substring(startOfJSON);

						try {
							JSONObject json = new JSONObject(jsonString);

							if ("enter".equals(json.getString("step"))) {
								methodStartTimes.put(
										className + ":"
												+ json.getString("method"),
										getTime(s));
							}
							if ("leave".equals(json.getString("step"))) {
								Long storedStartTime = methodStartTimes
										.get(className + ":"
												+ json.getString("method"));
								if (storedStartTime == null) {
									System.out.println("methods dont match "
											+ json.getString("method") + " : "
											+ s);
									continue;
								}
								long endTime = getTime(s);
								outList.add(className + ","
										+ json.getString("method") + ","
										+ (endTime - storedStartTime));
								methodStartTimes.remove(className + ":"
										+ json.getString("method"));
							}
						} catch (JSONException ex) {
							System.out.println("Failed to parse: " + s);
						}
					}
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

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("times filename");
		ShellFactory.createConsoleShell("hello", null,
				new GetMethodTimesFromLog()).commandLoop();
	}

}
