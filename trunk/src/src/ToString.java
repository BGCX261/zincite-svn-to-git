
import java.io.IOException;
import java.lang.reflect.Field;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;

public class ToString {

	/**
	 * Writes toString method for class . Writes to outputDirectory out.txt
	 * 
	 * @param fileName
	 * @return
	 */
	@Command
	public String generate(String className) {
		StringBuffer sb = new StringBuffer(5000);

		try {

			Class targetClass = Class.forName(className);

			if (!targetClass.isPrimitive() && targetClass != String.class) {
				Field fields[] = targetClass.getDeclaredFields();
				// Retrieving the super class
				Class cSuper = targetClass.getSuperclass();
				// Buffer Construction
				sb.append("StringBuffer buffer = new StringBuffer(500);");

				// Super class's toString()
				if (cSuper != null && cSuper != Object.class) {
					sb.append("buffer.append(super.toString());");
				}

				for (int j = 0; j < fields.length; j++) {
					// Append Field name
					sb.append("buffer.append(\"" + fields[j].getName()
							+ " = \");");
					// Check for a primitive or string
					if (fields[j].getType().isPrimitive()
							|| fields[j].getType() == String.class) {
						// Append the primitive field value
						sb.append("buffer.append(this." + fields[j].getName()
								+ ");");
					} else {
						/*
						 * It is NOT a primitive field so this requires a check
						 * for the NULL value for the aggregated object
						 */
						sb.append("if ( this." + fields[j].getName()
								+ "!= null )");
						sb.append("buffer.append(this." + fields[j].getName()
								+ ".toString());");
						sb.append("else buffer.append(\"value is null\"); ");
					} 
				} 
				sb.append("return  buffer.toString();");
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
		System.out.println("generate fullyQualifiedClassname");
		ShellFactory.createConsoleShell("hello", null, new ToString())
				.commandLoop();
	}

}
