package download;

import java.util.HashSet;
import java.util.LinkedHashSet;

import utils.FileIO;
import consts.PathConstanct;

public class CombineStatTypeTenfoldProjects {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpTotalSTProject=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"overlapInST.txt";
		String fpSupplyProject=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"supplyProjects.txt";
		String fpRequiredProject=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"requiredProjects.txt";
		String fpStillMissProject=PathConstanct.PATH_PROJECT_TRAIN_TEST_NAME+"stillMissProjects.txt";
		String[] arrTotalSTProject=FileIO.readStringFromFile(fpTotalSTProject).split("\n");
		String[] arrSupplyProject=FileIO.readStringFromFile(fpSupplyProject).split("\n");
		HashSet<String> setSupplyProject=new LinkedHashSet<String>();
		for(int i=0;i<arrSupplyProject.length;i++){
			setSupplyProject.add(arrSupplyProject[i]);
		}
		
		String strContent="";
		String strMiss="";
		for(int i=0;i<arrTotalSTProject.length;i++){
			String[] arrItemSt=arrTotalSTProject[i].split("\t");
			if(arrItemSt[1].equals("false")){
				if(setSupplyProject.contains(arrItemSt[0])){
					strContent+=arrItemSt[0]+"\n";
				} else{
					strMiss+=arrItemSt[0]+"\n";
				}
			} else{
				strContent+=arrItemSt[0]+"\n";
			}
		}
		FileIO.writeStringToFile(strContent, fpRequiredProject);
		FileIO.writeStringToFile(strMiss, fpStillMissProject);
		
	}

}
