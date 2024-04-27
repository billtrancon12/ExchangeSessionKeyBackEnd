package com.scheduler.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class ReadFileService {
	public static String readFile(String filePath) throws FileNotFoundException {
		try {
			File file = new File(filePath);
			Scanner scanner = new Scanner(file);
			StringBuilder stringBuilder = new StringBuilder();
			while(scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine());
			}
			scanner.close();
			return stringBuilder.toString();
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
			return "";
		}
	}
}
