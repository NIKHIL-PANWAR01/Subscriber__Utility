package com.protean.ondc.onboarding.service;

import static com.protean.ondc.onboarding.constant.ApplicationConstant.PATH;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.protean.ondc.onboarding.model.EncKeyModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncFileService {

	public void createFile(Scanner scanner, EncKeyModel encKeyModel, String fileName) {

		try {

			ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = objectWriter.writeValueAsString(encKeyModel);

			// complete model as json part to file
			byte[] strToBytes = json.getBytes();

			StringBuilder sb = new StringBuilder();
			StringBuilder file = sb.append(PATH).append("/").append(fileName);

			Path path = Paths.get(file.toString());

			boolean exists = Files.exists(path);

			if (exists) {
				log.info("{} file already exists at the path {} Do u wish to overwrite(Y/N) ?", fileName, PATH);
				String s = scanner.nextLine();
				log.info("You entered {}", s);
				if ("N".equalsIgnoreCase(s)) {
					log.info("going to exit");
					System.exit(0);
				} else {
					Files.delete(path);
					log.debug("{} deleted", fileName);
				}
			}

			Files.createDirectories(path.getParent());
			Files.createFile(path);
			Files.write(path, strToBytes, StandardOpenOption.WRITE);

			log.debug("file create at path {}", file);
		} catch (Exception e) {
			log.error("file creating failed", e);
			throw new RuntimeException(e);
		}

	}
}
