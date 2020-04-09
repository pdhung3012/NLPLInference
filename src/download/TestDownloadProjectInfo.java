package download;

import utils.GithubClient;

public class TestDownloadProjectInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputUrl=" https://api.github.com/repos/a466350665/smart";
		String outputFile="/users/hungphan/Desktop/sampleProjectInfo.txt";
		GithubClient gitClient = new GithubClient();
		for(int i=0;i<10000;i++) {
			System.out.println(i);
			try {
				gitClient.downloadJsonDataOfProject(inputUrl, outputFile);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
