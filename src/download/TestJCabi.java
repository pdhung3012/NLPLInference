package download;

import java.io.IOException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;

import consts.GithubConfig;

public class TestJCabi {

	 public static void main(String[] args) {
		    Github github = new RtGithub(".. "+GithubConfig.accessTokens+" ..");
		    Repo repo = github.repos().get(
		        new Coordinates.Simple("atinfo/at.info-knowledge-base")
		    );
		    try {
				System.out.println(repo.commits().json());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		    Issue issue = repo.issues().create("How are you?", "Please tell me...");
//		    issue.comments().post("My first comment!");
		  }

}
