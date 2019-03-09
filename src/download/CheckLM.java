package download;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import consts.PathConstanct;

public class CheckLM {

	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
	public static void readFile(String fpFile){
		int num=0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fpFile), StandardCharsets.US_ASCII)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
//		    	System.out.println(line);
		    	num++;
		    	if(num<=100) {
		    		System.out.println(num+": "+line);
		    	}
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("num "+num);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpLM=PathConstanct.PATH_PROJECT_STATTYPE_DATA+"training.s-t.A3";
		String fp2=PathConstanct.PATH_PROJECT_STATTYPE_DATA+"train.t";
		readFile(fp2);
		readFile(fpLM);
	}

}
