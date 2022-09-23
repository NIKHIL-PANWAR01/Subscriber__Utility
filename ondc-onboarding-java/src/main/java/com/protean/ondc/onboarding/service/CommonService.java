package com.protean.ondc.onboarding.service;

import static com.protean.ondc.onboarding.constant.ApplicationConstant.PATH;
import static com.protean.ondc.onboarding.constant.ApplicationConstant.SITE_VERIFICATION_FILE_NAME;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CommonService {

	private static final String HTML_FILE_TEMPLATE_PATH = "template/site-verification-template.html";

	public void generateOndcSiteVerificationHtml(String signature) {

		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(HTML_FILE_TEMPLATE_PATH);

		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int length; (length = inputStream.read(buffer)) != -1;) {
				result.write(buffer, 0, length);
			}
			String htmlString = result.toString(StandardCharsets.UTF_8);

			htmlString = htmlString.replace("$content", signature);

			// create html file
			StringBuilder sb = new StringBuilder();
			StringBuilder file = sb.append(PATH).append("/").append(SITE_VERIFICATION_FILE_NAME);

			Path path = Paths.get(file.toString());

			boolean exists = Files.exists(path);

			if (exists) {
				// delete if present
				Files.delete(path);
			}

			// create new file
			Files.createDirectories(path.getParent());
			Files.createFile(path);
			Files.write(path, htmlString.getBytes(), StandardOpenOption.WRITE);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
