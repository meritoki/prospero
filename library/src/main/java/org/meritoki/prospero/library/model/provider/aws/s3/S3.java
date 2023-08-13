//Reference
//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/java_s3_code_examples.html
//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-explicit.html
//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/java_s3_code_examples.html#scenarios
//https://stackoverflow.com/questions/41113119/java-nio-file-implementation-for-aws
//https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/model/ListObjectsV2Request.html#prefix()
//https://stackoverflow.com/questions/9329234/amazon-aws-ios-sdk-how-to-list-all-file-names-in-a-folder/9330600#9330600
package org.meritoki.prospero.library.model.provider.aws.s3;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.provider.aws.AWS;
import org.meritoki.prospero.library.model.provider.aws.s3.goes16.Batch;
import org.meritoki.prospero.library.model.provider.aws.s3.goes16.Request;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.node.NodeController;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

public class S3 extends AWS {

	static Logger logger = LoggerFactory.getLogger(S3.class.getName());
	public static final S3TransferManager transferManager = createCustonTm();
	public static final S3Client s3Client;

	public S3() {
		super("s3");
	}

	public S3(String name) {
		super(name);
	}

	public void init() throws Exception {
		super.init();
	}



	private static S3TransferManager createCustonTm() {
		// snippet-start:[s3.tm.java2.s3clientfactory.create_custom_tm]
		S3AsyncClient s3AsyncClient = S3AsyncClient.crtBuilder()
				.credentialsProvider(DefaultCredentialsProvider.create()).region(Region.US_EAST_1)
				.targetThroughputInGbps(20.0).minimumPartSizeInBytes(8 * MB).build();

		S3TransferManager transferManager = S3TransferManager.builder().s3Client(s3AsyncClient).build();
		// snippet-end:[s3.tm.java2.s3clientfactory.create_custom_tm]
		return transferManager;
	}

	private static S3TransferManager createDefaultTm() {
		// snippet-start:[s3.tm.java2.s3clientfactory.create_default_tm]
		S3TransferManager transferManager = S3TransferManager.create();
		// snippet-end:[s3.tm.java2.s3clientfactory.create_default_tm]
		return transferManager;
	}

	static {
		s3Client = S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create()).region(Region.US_EAST_1)
				.build();
	}

	public static void executeBatch(String path, Batch batch) {
		System.out.println("Executing Batch...");
		System.out.println("Executing Batch Requests: " + batch.requestList.size());
//		S3Client client = getClient();
//		sendPhoneMessage(batch.form.phoneNumber, "Executing Batch Requests: " + batch.requestList.size());
		for (int i = 0; i < batch.requestList.size(); i++) {
			Request request = batch.requestList.get(i);
			if (request.status.equals("pending")) {
				try {
					processRequest(request);
				} catch (Exception e) {
					request.status = "error";
					e.printStackTrace();
				}
				NodeController.saveJson(new java.io.File(path), batch);
			}
		}
//		sendPhoneMessage(batch.form.phoneNumber, "Executing Batch All " + batch.requestList.size() + " Request(s) Completed!");
	}

	public static void processRequest(Request request) throws Exception {
		Time time = request.time;
		String days = (time.day != -1) ? String.format("%03d", Time.getDayOfYear(time.year, time.month, time.day))
				: "001";
		String hours = (time.hour != -1) ? String.format("%02d", time.hour) : null;
		String prefix = request.prefix + "/" + time.year + "/" + days + "/" + hours + "/";
		logger.info("processRequest(...) prefix=" + prefix);
		List<String> keyList = listBucketObjects(getClient(), request.bucket, prefix);
		if (keyList.size() > 0) {
			String key = keyList.get(0);
			logger.info("processRequest(...) key=" + key);
			String[] keyArray = key.split("/");
			request.fileName = keyArray[keyArray.length - 1];
//			getObjectBytes(getClient(), request.bucket, key, request.getFilePath());
			File check = new File(request.getFilePath());
			if(!check.exists()) {
				downloadFile(S3.transferManager, request.bucket, key, request.getFilePath());
			}
			request.status = "complete";
		}
	}

	public static S3Client getClient() {
		ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
		Region region = Region.US_EAST_1;
		S3Client client = S3Client.builder().region(region).credentialsProvider(credentialsProvider).build();
		return client;
	}

	public static void getObjectBytes(S3Client s3, String bucketName, String keyName, String path) {
		logger.info("getObjectBytes(...," + bucketName + ", " + keyName + ", " + path + ")");
		try {
			GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(bucketName).build();
			logger.info("getObjectBytes(...," + bucketName + ", " + keyName + ", " + path + ") objectRequest="
					+ objectRequest);
			ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
			byte[] data = objectBytes.asByteArray();

			// Write the data to a local file.
			File myFile = new File(path);
			OutputStream os = new FileOutputStream(myFile);
			os.write(data);
			System.out.println("Successfully obtained bytes from an S3 object");
			os.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	public static Long downloadFile(S3TransferManager transferManager, String bucketName, String key,
			String downloadedFileWithPath) {
		logger.info("downloadFile(..., " + bucketName + ", " + key + ", " + downloadedFileWithPath + ")");
		DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
				.getObjectRequest(b -> b.bucket(bucketName).key(key))
				.addTransferListener(LoggingTransferListener.create()).destination(Paths.get(downloadedFileWithPath))
				.build();

		FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);

		CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
		logger.info("Content length [{}]", downloadResult.response().contentLength());
		return downloadResult.response().contentLength();
	}

	public static void getContentType(S3Client s3, String bucketName, String keyName) {

		try {
			HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(keyName).bucket(bucketName).build();

			HeadObjectResponse objectHead = s3.headObject(objectRequest);
			String type = objectHead.contentType();
			System.out.println("The object content type is " + type);

		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	public static List<String> listBucketObjects(S3Client s3, String bucketName, String prefix) throws Exception {
		ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder().prefix(prefix).bucket(bucketName)
				.maxKeys(1).build();

		boolean done = false;
		List<String> keyList = new ArrayList<>();
		while (!done) {
			ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsReqManual);
			for (S3Object content : listObjResponse.contents()) {
				System.out.println(content.key());
				keyList.add(content.key());
			}

			if (keyList.size() > 0) {
				done = true;

			} else {

				if (listObjResponse.nextContinuationToken() == null) {
					done = true;
				} else {
					listObjectsReqManual = listObjectsReqManual.toBuilder()
							.continuationToken(listObjResponse.nextContinuationToken()).build();
				}
			}
		}
		return keyList;
	}

	// convert bytes to kbs.
	private static long calKb(Long val) {
		return val / 1024;
	}

}
//listBucketObjects(client, "noaa-goes17");
//getObjectBytes(getClient(), "noaa-goes16",
//		"ABI-L1b-RadC/2000/001/12/OR_ABI-L1b-RadC-M3C01_G16_s20000011200000_e20000011200000_c20170671748180.nc",
//		"OR_ABI-L1b-RadC-M3C01_G16_s20000011200000_e20000011200000_c20170671748180.nc");
//downloadFile(S3ClientFactory.transferManager, "noaa-goes17",
//		"ABI-L2-MCMIPF/2019/153/18/OR_ABI-L2-MCMIPF-M6_G17_s20191531800341_e20191531809408_c20191531809482.nc",
//		"OR_ABI-L2-MCMIPF-M6_G17_s20191531800341_e20191531809408_c20191531809482.nc");
//getContentType(client, "noaa-goes16",
//		"ABI-L1b-RadC/2000/001/12/OR_ABI-L1b-RadC-M3C01_G16_s20000011200000_e20000011200000_c20170671748180.nc");
//public Long downloadFile(S3TransferManager transferManager, String bucketName, String key,
//String downloadedFileWithPath) {
//DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
//	.getObjectRequest(b -> b.bucket(bucketName).key(key))
//	.addTransferListener(LoggingTransferListener.create()).destination(Paths.get(downloadedFileWithPath))
//	.build();
//
//FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);
//
//CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
//System.out.println("Content length:"+ downloadResult.response().contentLength());
//return downloadResult.response().contentLength();
//}
//try {
//ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketName).build();
//
//ListObjectsResponse res = s3.listObjects(listObjects);
//List<S3Object> objects = res.contents();
//for (S3Object myValue : objects) {
//	System.out.print("\n The name of the key is " + myValue.key());
//	System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
//	System.out.print("\n The owner is " + myValue.owner());
//}
//
//} catch (S3Exception e) {
//System.err.println(e.awsErrorDetails().errorMessage());
//System.exit(1);
//}
//public Long downloadFile(S3TransferManager transferManager, String bucketName, String key,
//String downloadedFileWithPath) {
//DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
//	.getObjectRequest(b -> b.bucket(bucketName).key(key))
//	.addTransferListener(LoggingTransferListener.create()).destination(Paths.get(downloadedFileWithPath))
//	.build();
//
//FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);
//
//CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
//logger.info("Content length [{}]", downloadResult.response().contentLength());
//return downloadResult.response().contentLength();
//}
//String key = tempRoleCredentials.accessKeyId();
//String secKey = tempRoleCredentials.secretAccessKey();
//String secToken = tempRoleCredentials.sessionToken();
//
//// List all buckets in the account associated with the assumed role
//// by using the temporary credentials retrieved by invoking stsClient.assumeRole().
//StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(
//      AwsSessionCredentials.create(key, secKey, secToken));
//S3Client client = S3Client.builder()
//      .region(Region.US_EAST_1)
//      .endpointOverride(URI.create("https://s3.us-west-2.amazonaws.com"))
//      .forcePathStyle(true)
//      .build();
