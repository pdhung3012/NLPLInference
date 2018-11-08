package utils;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import consts.GithubConfig;

import javax.validation.constraints.NotNull;

import java.io.File;
import java.net.URL;

public class GithubClient {

	/**
	 * @param githubRemoteUrl
	 *            Remote git http url which ends with .git.
	 * @param accessToken
	 *            Personal access token.
	 * @param branchName
	 *            Name of the branch which should be downloaded
	 * @param destinationDir
	 *            Destination directory where the downloaded files should be
	 *            present.
	 * @return
	 * @throws Exception
	 */
	public boolean downloadRepoContent(@NotNull String githubRemoteUrl,
			@NotNull String accessToken, @NotNull String branchName,
			@NotNull String destinationDir) throws Exception {
		// String githubSourceUrl, String accessToken
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
				GithubConfig.username, GithubConfig.password);
		URL fileUrl = new URL("file://" + destinationDir);
		File destinationFile = FileUtils.toFile(fileUrl);
		// delete any existing file
		FileUtils.deleteDirectory(destinationFile);
		Git.cloneRepository().setURI(githubRemoteUrl).setBranch(branchName)
				.setDirectory(destinationFile)
				.setCredentialsProvider(credentialsProvider).call();
		if (destinationFile.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
