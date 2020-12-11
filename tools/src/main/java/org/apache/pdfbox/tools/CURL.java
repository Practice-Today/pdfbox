package org.apache.pdfbox.tools;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class CURL {

	/**
	 *
	 * @param args curl's command line arguments
	 * 
	 */
	static void main(String[] args) throws Exception {
		if (args.length < 1)
			usage(args);

		String[] tempDirs = { "/tmp", "C:\\ptoday_files\\cookies\\" };
		File TEMP_DIR = null;
		for (String tempDir : tempDirs) {
			File dir_ = new File(tempDir);
			if (dir_.isDirectory()) {
				TEMP_DIR = dir_;
				break;
			}
		}

		File outputFile = File.createTempFile("curl_response_", ".xml", TEMP_DIR);
		String curlCommand = "curl -k --output " + outputFile + " " + String.join(" ", args);

		ProcessBuilder processBuilder = new ProcessBuilder(curlCommand.split(" "));
		// processBuilder.inheritIO();
		Process process = processBuilder.start();
		process.waitFor();
		int exitCode = process.exitValue();
		process.destroy();

		if (exitCode != 0)
			System.err.println("ERROR RC:" + String.valueOf(exitCode));
		System.out.println(outputFile);
		System.exit(exitCode);
	}

	private static void usage(String args[]) {
		String crlf = System.getProperty("line.separator");
		String message = "Usage: java -jar " + Version.getJarName(Convert.class) + " "
				+ Version.getClassName(MethodHandles.lookup()) + " <parameters to a `curl` command>" + crlf;
		System.err.println(message);
		System.exit(1);
	}

}
