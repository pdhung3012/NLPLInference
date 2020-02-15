package analysis;

import utils.FileIO;

public class ExtractLinkAndOnlineSite {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fop="/Users/hungphan/Desktop/evalMethodInvocation/";
		String fpRaw=fop+"raw.txt";
		String fpLocalList=fop+"local.txt";
		String fpOnlineList=fop+"online.txt";
		
		String strTemplateGit="G:\\gitExtractedST2\\";
		String strTemplateRegex="G:/gitExtractedST2/";
		String[] arrContent=FileIO.readStringFromFile(fpRaw).split("\n");
		
		StringBuilder sbLocal=new StringBuilder();
		for(int i=0;i<arrContent.length;i++) {
			if(arrContent[i].trim().startsWith(strTemplateGit)) {
				sbLocal.append(arrContent[i].trim().split("\t")[0].trim()+"\n");
			}
		}
		FileIO.writeStringToFile(sbLocal.toString(), fpLocalList);
		
		sbLocal=new StringBuilder();
		String[] arrLocalLinks=FileIO.readStringFromFile(fpLocalList).split("\n");
		for(int i=0;i<arrLocalLinks.length;i++) {
			String item=arrLocalLinks[i].trim().replaceAll("\\\\", "/").replaceFirst(strTemplateRegex, "");
			String username=item.split("/")[0].split("-")[0];
			String repo=item.split("/")[0].replaceFirst(username+"-", "");
			String otherPath=item.replaceFirst(username+"-"+repo+"/","");
			String zipDir=otherPath.split("/")[0];
			String branchPath="blob/"+zipDir.split("-")[1];
			String encodePath=otherPath.replaceFirst(zipDir+"/", "");
			String url="https://github.com/"+username+"/"+repo+"/"+branchPath+"/"+encodePath;
			sbLocal.append(url+"\n");
		}
		FileIO.writeStringToFile(sbLocal.toString(), fpOnlineList);
		
	}

}
