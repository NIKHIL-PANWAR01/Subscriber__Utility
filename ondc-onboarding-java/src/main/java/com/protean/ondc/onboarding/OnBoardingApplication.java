package com.protean.ondc.onboarding;

import static com.protean.ondc.onboarding.constant.ApplicationConstant.ENCRYPTION_KEY_PAIR_FILE_NAME;
import static com.protean.ondc.onboarding.constant.ApplicationConstant.REQUEST_ID_FILE_NAME;
import static com.protean.ondc.onboarding.constant.ApplicationConstant.SIGN_KEY_PAIR_FILE_NAME;
import static com.protean.ondc.onboarding.constant.ApplicationConstant.SITE_VERIFICATION_FILE_NAME;

import java.util.Scanner;
import java.util.UUID;

import com.protean.ondc.onboarding.model.EncKeyModel;
import com.protean.ondc.onboarding.model.KeyModel;
import com.protean.ondc.onboarding.model.RequestModel;
import com.protean.ondc.onboarding.service.CommonService;
import com.protean.ondc.onboarding.service.CryptoService;
import com.protean.ondc.onboarding.service.EncFileService;
import com.protean.ondc.onboarding.service.FileService;
import com.protean.ondc.onboarding.service.RequestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnBoardingApplication {

	public static void main(String[] args) {
		new OnBoardingApplication().start();
	}

	public void start() {
		printBanner();

		CryptoService.setup();

		CryptoService cryptoService = new CryptoService();

		// Using Scanner for Getting Input from User
		Scanner scanner = new Scanner(System.in);

		// log.info("Press any key to continue with the process or type exit to end this process");
		System.out.println("Press any key to continue with the process or type exit to end this process");
		String input = scanner.nextLine();

		if ("exit".equalsIgnoreCase(input)) {
			// log.info("going to exit");
			System.out.println("going to exit");
			System.exit(0);
		}

		// get subscriber id
		// log.info("What is your subscriber id ?");
		System.out.println("What is your subscriber id ?");
		String subscriberId = scanner.nextLine();
		log.info("OK \n");
		System.out.println("OK \n");

		// generating keys
		// log.info("Step 7. Generate Signing Key Pair - signing_public_key and
		// signing_private_key");
		System.out.println("Step 7. Generate Signing Key Pair - signing_public_key and signing_private_key");
		KeyModel keyModel = cryptoService.generateKey();
		System.out.println(keyModel + "\n");

		// save keys to file
		new FileService().createFile(scanner, keyModel, SIGN_KEY_PAIR_FILE_NAME);
		// log.info("Overwriting existing file");
		System.out.println("Overwriting existing file");
		// log.info("Stored generated Signing Key pairs in {}", SIGN_KEY_PAIR_FILE_NAME + "\n");
		System.out.println("Stored generated Signing Key pairs in" + " " + SIGN_KEY_PAIR_FILE_NAME + "\n");

		// generate encryption key pairs
		// log.info("Step 8. Generate Encryption Key Pair - encryption_public_key and
		// encryption_private_key\n");
		System.out.println("Step 8. Generate Encryption Key Pair - encryption_public_key and encryption_private_key\n");

		EncKeyModel encKeyModel = cryptoService.generateEncKey();
		System.out.println(encKeyModel);

		// save keys to file
		new EncFileService().createFile(scanner, encKeyModel, ENCRYPTION_KEY_PAIR_FILE_NAME);
		System.out.println("Overwriting existing file");
		// log.info("Overwriting existing file");
		// log.info("Stored generated Encryption Key pairs in {}", ENCRYPTION_KEY_PAIR_FILE_NAME +
		// "\n");
		System.out.println("Stored generated Encryption Key pairs in" + " " + ENCRYPTION_KEY_PAIR_FILE_NAME + "\n");

		// generate request id
		// log.info("Step 9. Generate Unique Request ID (request_id)");
		System.out.println("Step 9. Generate Unique Request ID (request_id)");
		String requestId = UUID.randomUUID().toString();
		// log.info(reqModel + "\n");
		System.out.println(requestId + "\n");
		RequestModel requestModel = new RequestModel(requestId, subscriberId);
		new RequestService().createFile(scanner, requestModel, REQUEST_ID_FILE_NAME);
		// sign the request id
		// log.info("Step 10. Generate SIGNED_UNIQUE_REQ_ID \n ");
		System.out.println("Step 10. Generate SIGNED_UNIQUE_REQ_ID \n ");
		// log.info("Signing Request Id with Signing Private Key Generated");
		System.out.println("Signing Request Id with Signing Private Key Generaed");
		String signature = cryptoService.generateSignature(requestId, keyModel.getPrivateKey());
		System.out.println(signature + "\n");

		// generate ondc-site-verification.html
		// log.info("Step 11. Create ondc-site-verification.html and place it at subscriber_id by
		// adding SIGNED_UNIQUE_REQ_ID generated");
		System.out.println("Step 11. Create ondc-site-verification.html and place it at subscriber_id by adding SIGNED_UNIQUE_REQ_ID generated");
		// log.info("Generating {}", SITE_VERIFICATION_FILE_NAME);
		System.out.println("Generating" + " " + SITE_VERIFICATION_FILE_NAME);
		new CommonService().generateOndcSiteVerificationHtml(signature);
		final String url = "https://" + subscriberId + "/" + SITE_VERIFICATION_FILE_NAME;
		// log.info("{} generated, please place it in {}", SITE_VERIFICATION_FILE_NAME, url + "\n");
		System.out.println("Generated, please place it in " + "" + SITE_VERIFICATION_FILE_NAME + url + "\n");

		// display final information
		// log.info("Request ID: {}", reqModel + "\n");
		System.out.println("Request ID: " + requestId + "\n");
		// log.info("Subscriber ID: {}", subscriberId);
		System.out.println("Subscriber ID:" + subscriberId);
	}

	private void printBanner() {
		log.info("\n");
		log.info("######################################################################");
		log.info("################### ONDC ONBOARDING CLI TOOL #########################");
		log.info("######################################################################");
		log.info("\n");
	}

}
