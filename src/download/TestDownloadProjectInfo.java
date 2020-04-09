package download;

import utils.GithubClient;

public class TestDownloadProjectInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputUrl=" https://api.github.com/repos/a466350665/smart";
		String outputFile="/users/hungphan/Desktop/sampleProjectInfo.txt";
		GithubClient gitClient = new GithubClient();
		try {
			gitClient.downloadJsonDataOfProject(inputUrl, outputFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
