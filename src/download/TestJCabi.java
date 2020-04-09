package download;

import java.io.IOException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;

public class TestJCabi {

	 public static void main(String[] args) {
		    Github github = new RtGithub("0f4a319567c6c07d5980c4e14f429a5330e61d2c");
		    Repo repo = github.repos().get(
		        new Coordinates.Simple("a466350665/smart")
		    );
		    try {
				System.out.println(repo.json());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		    Issue issue = repo.issues().create("How are you?", "Please tell me...");
//		    issue.comments().post("My first comment!");
		  }

}
