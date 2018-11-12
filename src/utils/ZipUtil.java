package utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	public static boolean extractZipFileToFolder(String inputZipFile,String outputFolder){
		// TODO Auto-generated method stub
				 //Open the file
				boolean isExtractSuccess=false;
		        try(ZipFile file = new ZipFile(inputZipFile))
		        {
		            FileSystem fileSystem = FileSystems.getDefault();
		            //Get file entries
		            Enumeration<? extends ZipEntry> entries = file.entries();
		             
		            //We will unzip files in this folder
		            String uncompressedDirectory = outputFolder;
		            Files.createDirectory(fileSystem.getPath(uncompressedDirectory));
		             
		            //Iterate over entries
		            while (entries.hasMoreElements())
		            {
		                ZipEntry entry = entries.nextElement();
		                //If directory then create a new directory in uncompressed folder
		                if (entry.isDirectory())
		                {
		                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
		                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
		                }
		                //Else create the file
		                else
		                {
		                    InputStream is = file.getInputStream(entry);
		                    BufferedInputStream bis = new BufferedInputStream(is);
		                    String uncompressedFileName = uncompressedDirectory + entry.getName();
		                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
		                    Files.createFile(uncompressedFilePath);
		                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
		                    while (bis.available() > 0)
		                    {
		                        fileOutput.write(bis.read());
		                    }
		                    fileOutput.close();
		                    System.out.println("Written :" + entry.getName());
		                    isExtractSuccess=true;
		                }
		            }
		        }
		        catch(IOException e)
		        {
		            e.printStackTrace();
		        }
		        return isExtractSuccess;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 //Open the file
        try(ZipFile file = new ZipFile("files.zip"))
        {
            FileSystem fileSystem = FileSystems.getDefault();
            //Get file entries
            Enumeration<? extends ZipEntry> entries = file.entries();
             
            //We will unzip files in this folder
            String uncompressedDirectory = "uncompressed/";
            Files.createDirectory(fileSystem.getPath(uncompressedDirectory));
             
            //Iterate over entries
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory())
                {
                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
                }
                //Else create the file
                else
                {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = uncompressedDirectory + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0)
                    {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
                    System.out.println("Written :" + entry.getName());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
	}

}
