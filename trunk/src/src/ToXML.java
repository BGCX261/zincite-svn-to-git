
import java.io.IOException;
import java.lang.reflect.Field;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;

public class ToXML {

	/**
	 * Writes toXML method for class . Writes to outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 */
	@Command
	public String generate(String className, String elementName) {
		StringBuffer sb = new StringBuffer();

		try {

			Class targetClass = Class.forName(className);

			if (!targetClass.isPrimitive() && targetClass != String.class) {
				Field fields[] = targetClass.getDeclaredFields();

				sb.append("Element " + elementName + " = new Element(\""
						+ elementName + "\");");

				for (int j = 0; j < fields.length; j++) {
					// Check for a primitive
					if (fields[j].getType().isPrimitive()) {
						sb.append("if ( this." + fields[j].getName()
								+ "!= 0 ){");
						sb.append("Element " + fields[j].getName()
								+ " = new Element(\""
								+ fields[j].getName().toLowerCase() + "\"); ");
						sb.append(fields[j].getName()
								+ ".setText(String.valueOf(this."
								+ fields[j].getName() + "));");
						sb.append(elementName + ".addContent("
								+ fields[j].getName() + ");");
						sb.append('}');						
					} else {
						/*
						 * It is NOT a primitive field so this requires a check
						 * for the NULL value for the aggregated object
						 */
						sb.append("if ( DataFormatter.hasValue(this." + fields[j].getName()
								+ ") ){");
						sb.append("Element " + fields[j].getName()
								+ " = new Element(\""
								+ fields[j].getName().toLowerCase() + "\"); ");
						if (fields[j].getType() == java.util.Date.class) {
							sb.append("SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");");
							sb.append(fields[j].getName() + ".setText(dateFormat.format(this."
									+ fields[j].getName() + "));");
						} else {
							sb.append(fields[j].getName() + ".setText(this."
									+ fields[j].getName() + ");");
						}
						sb.append(elementName + ".addContent("
								+ fields[j].getName() + ");");
						sb.append('}');
					}
				}
				sb.append("return " + elementName + ";");
			}
			FileUtilities.writeFileFromString(SiteConstants.OUTPUT_DIRECTORY + "out.txt", sb.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found in the class path");
			return "Failure";

		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("generate fullyQualifiedClassname elementName");
		ShellFactory.createConsoleShell("hello", null, new ToXML())
				.commandLoop();
	}

}
