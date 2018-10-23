package parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import consts.GithubConfig;
import parser.MetadataCacher;
//import util.Config;
import utils.FileIO;

public class GetGithubRepoForLiveAPI {
//	final static String[] keywords = new String[]{"android.", "com.google.gwt.", "org.hibernate.", "org.joda.time.", "com.thoughtworks.xstream."};
	final static String[] keywords = new String[]{"org.apache.","android.", "com.google.gwt.", "org.hibernate.", "org.joda.time.", "com.thoughtworks.xstream."};
	
	public static void main(String[] args) {
		getLists();
//		readLists();
	}

	private static void readLists() {
		for (String keyword : keywords) {
			HashMap<String, Integer> counts = new HashMap<>();
			String content = utils.FileIO.readStringFromFile("G:/gitTrans/repos-5stars-50commits-lib-" + keyword + ".csv");
			Scanner sc = new Scanner(content);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] parts = line.split(",");
				String name = parts[0];
				int count = Integer.parseInt(parts[1]);
				counts.put(name, count);
			}
			sc.close();
		}
		
	}

	private static void getLists() {
		for (int i = 0; i < keywords.length; i++) {
			final int ii = i;
			final String keyword = keywords[i];
			new Thread(new Runnable() {
				@Override
				public void run() {
					PrintStream ps = null;
					try {
						ps = new PrintStream(new FileOutputStream("T:/github/repos-5stars-50commits-lib-" + keyword + ".csv"));
					} catch (FileNotFoundException e) {
						System.err.println(e.getMessage());
						return;
					}
					int current = ii;
					String listContent = FileIO.readStringFromFile("T:/github/repos-5stars-50commits.csv");
					Scanner sc = new Scanner(listContent);
					while (sc.hasNextLine()) {
						String name = sc.nextLine();
						String url = "https://api.github.com/search/code?q=import%20" + keyword + "+in:file+repo:" + name + "&per_page=1";
						System.out.println(url);
						MetadataCacher mc = null;
						while (true) {
							boolean gotIt = false;
							int j = current;
							while (true) {
								String username = GithubConfig.username;
								mc = new MetadataCacher(url, username, GithubConfig.password);
								mc.authenticate();
								if (mc.isAuthenticated()) {
									mc.getResponseJson();
									String content = mc.getContent();
									int s = content.indexOf("\"total_count\":");
									if (s > 0) {
										s += "\"total_count\":".length();
										int e = content.indexOf(',', s);
										int count = Integer.parseInt(content.substring(s, e));
										if (count > 0)
											ps.println(name + "," + count);
									}
									gotIt = true;
									current = (j + keywords.length) % 20;
									if (current == ii) {
										try {
											Thread.sleep(1000*2);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									break;
								} else {
									j = (j + keywords.length) % 20;
									if (j == current) {
										current = ii;
										break;
									}
								}
							}
							if (gotIt)
								break;
							try {
								System.out.println("Waiting for resetting limit.");
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
					sc.close();
					ps.flush();
					ps.close();
				}
			}).start();
		}
	}

}
