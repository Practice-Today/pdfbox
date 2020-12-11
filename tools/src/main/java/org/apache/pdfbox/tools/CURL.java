package org.apache.pdfbox.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CURL {

	static Map<String,String> jarArgs = new HashMap<String,String>(); // command-line arguments that appear before '--'
	static List<String> curlArgs = new ArrayList<String>();
	static File outputFile;
	static String curlCommand = "";
	
	/**
	 * Command line arguments may contain a "--", in which case arguments before the "--" 
	 * go to the CURL operation itself, and those following go to the `curl` command.
	 * 
	 * The results of the `curl` command are written to a file named `curl_response_*.xml`
	 *   (where * is a randomly generated unique number).
	 *   
	 * CURL operation options include:
	 * 		--printArgs : print these arguments into a file named `curl_response_*.args.xml`
	 * 
	 * @param args curl's (and CURL's) command line arguments
	 * 
	 */
	static void main(String[] args) throws Exception {
		if (args.length < 1)
			usage(args);
		
		String[] tempDirs = { "/tmp", "C:\\ptoday_files\\cookies", "C:\\TEMP" };
		File TEMP_DIR = null;
		for (String tempDir : tempDirs) {
			File dir_ = new File(tempDir);
			if (dir_.isDirectory()) {
				TEMP_DIR = dir_;
				break;
			}
		}

		outputFile = File.createTempFile("curl_response_", ".xml", TEMP_DIR);
		curlCommand = parseCommandLine(args, outputFile);

		if (jarArgs.containsKey("--printArgs")||jarArgs.containsKey("-p"))
			printDefaultResult(null);

		int exitCode = 0;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(curlCommand.split(" "));
			// processBuilder.inheritIO();
			Process process = processBuilder.start();
			process.waitFor();
			exitCode = process.exitValue();
			process.destroy();
			// Absurdly, Windows curl returns 6=CURLE_COULDNT_RESOLVE_HOST when successful
			if (System.getProperty("os.name") == "Mac OS X" && exitCode != 0)
				System.err.println("ERROR RC:" + String.valueOf(exitCode));
		}
		catch (Exception ex) {
			System.err.println(ex);
			printDefaultResult(ex);
		}

		System.out.println(outputFile);
		System.exit(exitCode);
	}


	// Parse command-line options, set jarCommands if there is a '--' in them.
	private static String parseCommandLine(String[] args, File outputFile) {
		boolean foundDashDash = false;
		List<String> jarArgsList = new ArrayList<String>();
		
		for (String arg: args) {
			if (arg.equals("--"))
				foundDashDash = true;
			else 
				if (foundDashDash)
					curlArgs.add(arg);
				else
					jarArgsList.add(arg);
		}
		
		if (!foundDashDash) {
			curlArgs = jarArgsList;
		} else {
			// Copy List<> jarArgsList to Map<> jarArgs
			for (String arg: jarArgsList) {
				String[] nameValue = arg.split("=");
				if (nameValue.length == 1)
					jarArgs.put(nameValue[0], "");
				else
					jarArgs.put(nameValue[0],nameValue[1]);
			}
		}
		
		String curlCommand = "curl -k --output " + outputFile + " " + String.join(" ", curlArgs);
		return curlCommand;
	}
	

	private static void printDefaultResult(Exception exception) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(outputFile.getCanonicalPath()
					.replace(".xml", (exception==null)?".args.xml":".error.xml")));
			out.print(stringDefaultResult(exception));
			out.close();
		} catch (Exception ex) {
			System.err.print(ex);
		}
	}
	private static String stringDefaultResult(Exception ex) {
		String jarName = Version.getJarName(CURL.class).replace(".jar", "").replace(".", "-");
		String error = (ex != null)?"true":"false";
		
		String dflt = "<?xml version=\"1.0\"?>\n"
				+ "<response>\n"
				+ "  <"+jarName+">\n"
				+ "    <error>"+error+"</error>\n"
				+ "    <message>"+curlCommand+"</message>\n"
				+ "    <args>\n";
		for (String arg: curlArgs) {
			dflt += "      <arg>"+arg+"</arg>\n";
		}
		dflt += "    </args>\n"
			  + "    <jarArgs>\n";
		for (String nam: jarArgs.keySet()) {
			dflt += "      <jarArg name='"+nam+"' val='"+jarArgs.get(nam)+"' />\n";
		}
		dflt += "    </jarArgs>\n";
		if (ex != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			dflt += "    <exception>"+sw.toString()+"</exception>\n";
		}
		dflt += "  </"+jarName+">\n"
			  + "</response>\n";
		return dflt;
	}
	
	private static void usage(String args[]) {
		String crlf = System.getProperty("line.separator");
		String message = "Usage: java -jar " + Version.getJarName(Convert.class) + " "
				+ Version.getClassName(MethodHandles.lookup()) + " <parameters to a `curl` command>" + crlf;
		System.err.println(message);
		System.exit(1);
	}

}
