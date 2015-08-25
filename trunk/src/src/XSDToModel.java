
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import clicheUtils.CamelCaseString;
import clicheUtils.SiteConstants;
import clicheUtils.FileUtilities;

public class XSDToModel {

	private CamelCaseString camelCaseString;
	
	/**
	 * Creates java properties from the XSD file. Writes to outputDirectory out.txt
	 * @throws IOException 
	 *  
	 */
	@Command
	public String create(String fileName, String elementname) throws IOException {
		
		this.camelCaseString = new CamelCaseString();
		
		try {
			String fileContents = FileUtilities
					.readFileIntoString(FileUtilities.lastParameter(fileName));

			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new StringReader(fileContents));
			Iterator iterator = doc.getDescendants();

			StringBuilder sb = new StringBuilder();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof Element) {
					Element element = (Element) object;
					if (elementname.equals(element.getAttributeValue("name"))) {
						Iterator iterator2 = element.getDescendants();
						while (iterator2.hasNext()) {
							Object object2 = iterator2.next();
							if (object2 instanceof Element) {
								Element element2 = (Element) object2;
								if (element2.getAttributeValue("name") != null) {
									sb.append(writeElement(element2));
								}
							}
						}
						break;
					}
				}
			}

			FileUtilities.writeFileFromString(SiteConstants.OUTPUT_DIRECTORY + "out.txt", sb.toString());

		} catch (IOException e) {
			e.printStackTrace();
			return "Failure";
		} catch (JDOMException e) {
			e.printStackTrace();
			return "Failure";
		}
		return "Success";
	}

	public static void main(String[] params) throws IOException {
		System.out.println("Available commands:");
		System.out.println("create filename elementname");
		ShellFactory.createConsoleShell("hello", null, new XSDToModel())
				.commandLoop();
	}
	private String writeElement(Element element) {
		StringBuilder result = new StringBuilder();
		String type = element.getAttributeValue("type");
		if (type == null) {
			type = "String";
		} else {
			type = type.substring(4);
			if ("date".equals(type)) {
				type = "Date";
			}
		}
		result.append("private ");
		result.append(type);
		result.append(' ');
		result.append(this.camelCaseString.toCamelCase( element.getAttributeValue("name")));
		result.append(";\r\n");
		return result.toString();
	}
}
