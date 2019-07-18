import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.file.Files;


public class TestJunit {

	private final String errMsg = "java.lang.ArrayIndexOutOfBoundsException";
	private final String testResultFile = "my_min_failing_test_file.txt";
	private final String pathToTestFiles = "TestFiles/";
	private final String pathToInputs = pathToTestFiles + "Inputs/";
	private final String pathToExpectedFilesLine = pathToTestFiles + "Expected/line/";
	private final String pathToExpectedFilesChar = pathToTestFiles + "Expected/char/";

	
	@Test
	/**
	 * A single JUnit test to test all files.
	 */
	public void testAllFiles() {

		// Uncomment to generate new test files
		// generateTestFiles();

		// Get test files
		List<String> filesToTest = getTestFiles();

		System.out.println("Testing " + filesToTest.size() + " files");

		for(String file: filesToTest) {
			DeltaDebug dd = new DeltaDebug();
			System.out.println(file);
			dd.deltaDebug(true, "java SecretCoder", pathToInputs + file, errMsg, testResultFile);
			assertFalse(diffFiles(pathToExpectedFilesChar + file, testResultFile));

			dd.deltaDebug(false, "java SecretCoder", pathToInputs + file, errMsg, testResultFile);
			assertFalse(diffFiles(pathToExpectedFilesLine + file, testResultFile));
		}

		File file = new File(testResultFile); 
		file.delete();
	}

	/**
	 * Generates files of varying length for testing
	 * @return
	 */
	private List<String> generateTestFiles() {

		List<String> files = new ArrayList<String>();
		
		// test lines of length 1-1000
		int maxLineLength = 10;

		// test files up to 10000 lines
		int maxLineCount = 10;

		for(int lineLength = 1; lineLength <= maxLineLength; lineLength++) {
			for(int lineCount = 1; lineCount <= maxLineCount; lineCount++) {
				 files.add(generateTestFilesHelper(lineLength, lineCount));
			}
		}

		// Also generate 1 "big" file
		// long_failing_text.txt was 43 lines with an avg of 217 characters per line
		generateTestFilesHelper(1000,1000);

		return files;
	}

	/**
	 * Generates 3 test files with a given line length and line count. A random bad char
	 * will we place at the beginning of one file, randomly in the middle of one, and at
	 * the end of one.
	 * @param lineLength
	 * @param lineCount
	 * @return A list of the 3 files
	 */
	private String generateTestFilesHelper(int lineLength, int lineCount) {
		ArrayList<String> newFile = new ArrayList<String>();
		for(int i = 0; i < lineCount; i++) {
			newFile.add(generateGoodLine(lineLength));
		}

		String fileName = "testfile_" + lineLength + "_" + lineCount;

		Random r = new Random();

		String badLine = generateBadLine(lineLength, pathToExpectedFilesChar + fileName);

		ArrayList<String> badLineAL = new ArrayList<String>();
		badLineAL.add(badLine);
		newFile.set(r.nextInt(lineCount), badLine);

		writeToFile(pathToExpectedFilesLine + fileName, badLineAL);
		writeToFile(pathToInputs + fileName, newFile);
		return fileName;
	}

	/**
	 * Generates a single good line (only alphanumberic characters)
	 * @param lineLength max length of the line
	 * @return The good line
	 */
	private String generateGoodLine(int lineLength) {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();

		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		int randomLineLength = r.nextInt(lineLength) + 1;	// random num 1 through line length inclusive

		for(int i = 0; i < randomLineLength; i++) {
			sb.append(AB.charAt(r.nextInt(AB.length())));
		}
		return sb.toString();
	}

	/**
	 * Generates a bad line (a good line with a single bad character randomly). Also write
	 * the bad char to the expectedCharPath
	 * @param lineLength
	 * @param expectedCharPath The path to the expected characaters file
	 * @return A bad line
	 */
	private String generateBadLine(int lineLength, String expectedCharPath) {
		String[] badChars = {"¡","™","£","¢","∞","§","¶","•","ª","º","–","≠","œ","∑","´","®","†","¥","¨","ˆ","ø","π","“","‘","«","å","ß","∂","ƒ","©","˙","∆","˚","¬","…","æ","Ω","≈","ç","√","∫","˜","µ","≤","≥","÷"};
		Random r = new Random();
		String randomBadChar = badChars[r.nextInt(badChars.length)];
		String line = generateGoodLine(lineLength);


		ArrayList<String> badChar = new ArrayList<String>();
		badChar.add(randomBadChar);
		writeToFile(expectedCharPath, badChar);

		int randomSpot = r.nextInt(line.length() + 1);
		if(randomSpot == line.length()) {
			// put bad char at the end
			return line + randomBadChar;
		} else {
			String first = line.substring(0, randomSpot);
			String second = line.substring(randomSpot);
			return first + randomBadChar + second;
		}
	}

	/**
	 * Gets a list of all the test files
	 * @return a list of all the test files
	 */
	private List<String> getTestFiles() {
		// run tests for line
		File folder = new File(pathToInputs);
		File[] listOfFiles = folder.listFiles();

		List<String> fileNames = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileNames.add(listOfFiles[i].getName());
			}
		}
		return fileNames;
	}

	/**
	 * Uses the diff command to check if two files are different
	 * @param file1
	 * @param file2
	 * @return true if different
	 */
	public static boolean diffFiles(String file1, String file2) {
		String s = null;
		try{ 	
				Process p = Runtime.getRuntime().exec("diff " + file1 + " " + file2);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				boolean returnValTemp = false;
				while ((s=stdInput.readLine())!=null)
				{
					System.out.println(s);
					returnValTemp = true;
				}
				if (returnValTemp) {
					return true;
				}
		}
		catch (IOException e) {
            System.out.println("failed to run");
			System.exit(-1);
			return false;
		}
		return false;
	}

	/**
	 * Copied from DeltaDebug template file
	 * @param file - file to write to
	 * @param ArrayList<String> - this is just a placeholder example, you can use whatever data structure you want  
	 */
	public void writeToFile(String file, ArrayList<String> list)
	{
		Path out = Paths.get(file);
		try {
			Files.write(out,list,Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
