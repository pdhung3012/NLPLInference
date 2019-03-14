package utils;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MapUtil {

	public static String tryGetLine(BufferedReader br) {
		String line=null;
		try {
			line = br.readLine();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return line;
	}
	
	public HashMap<String,String> getHashMapFromFile(String fp){
		HashMap<String,String> map=new LinkedHashMap<String, String>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fp), StandardCharsets.US_ASCII)) {
		    for (String line = null; (line = tryGetLine(br)) != null;) {
		    	String[] arrItems=line.split("\t");
		    	if(arrItems.length>=2){
		    		map.put(arrItems[0], arrItems[1].trim());
		    	}
		    	
		    }
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
