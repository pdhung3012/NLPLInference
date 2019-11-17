package analysis;

import java.io.File;

import utils.FileIO;

public class RemoveCommentsFromCFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopIn = "/Users/hungphan/git/MLFixCErrors/databases/uninitializedCodes/";
		String fopOut = "/Users/hungphan/git/MLFixCErrors/databases/uninitializedCodes_2/";
		new File(fopOut).mkdir();

		File fileIn = new File(fopIn);
		File[] lstIn = fileIn.listFiles();

		for (int i = 0; i < lstIn.length; i++) {
			String fname = lstIn[i].getName();
			if (fname.endsWith(".c")) {
				String strContent = FileIO.readStringFromFile(lstIn[i].getAbsolutePath());
				String[] arrContent = strContent.split("\n");
				String strOutput = "";
				for (int j = 0; j < arrContent.length; j++) {
					if (arrContent[j].trim().startsWith("//")) {

					} else if (arrContent[j].trim().contains("//")) {
						strOutput += arrContent[j].substring(0, arrContent[j].indexOf("//")) + "\n";
					}

					else {
						strOutput += arrContent[j] + "\n";
					}
				}
				FileIO.writeStringToFile(strOutput, fopOut + fname);
				System.out.println(fname);
			}
		}

	}

}
