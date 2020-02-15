package analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import utils.FileIO;

public class CollectCodeSnippet {
	public static void main(String[] args) {
		String fpLocationInput="C:\\evalMethodInvocation\\local.txt";
		String fopCodeOutput="C:\\evalMethodInvocation\\code\\";
		
		new File(fopCodeOutput).mkdir();
		
		String[] arrContent=FileIO.readStringFromFile(fpLocationInput).split("\n");
		for(int i=0;i<arrContent.length;i++) {
			String numberFormat=String.format("%03d" , i+1);
			String strFile=arrContent[i].trim();
			String strDest=fopCodeOutput+numberFormat+".java";
			try {
				Files.copy(new File(strFile).toPath(), new File(strDest).toPath(),
				        StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
