package com.hictech.hCloud.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class CacheTest {
	public static void main(String[] args) throws Exception {
		final long start = System.currentTimeMillis();
		
		Thread[] threads = new Thread[2];
		
		for( int i = 0; i < threads.length; i++ ) {
			threads[i] = new Thread() {
				public void run() {
					try {
						String resp = doGet("http://localhost:8080/hwildfly/cache");
						
						long ms = System.currentTimeMillis() - start;
						System.out.println(resp.substring(0, resp.indexOf("\n")));
						
						System.out.println("risposta dopo "+ms+"ms");
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				};
			};
		}
		
		for( Thread t: threads ) {
			t.start();
		}
		
		for( Thread t: threads ) {
			t.join();
		}

	}

	private static String doGet(String req) throws Exception {
		BufferedReader read = new BufferedReader(
			new InputStreamReader(
				new URL(req).openConnection().getInputStream()
			)
		);

		Scanner s = new Scanner(read);
		
		StringBuilder html = new StringBuilder();
		while(s.hasNextLine()) {
			html.append(s.nextLine()).append("\n");
		}
		
		s.close();
		
		return html.toString();
	}
}
