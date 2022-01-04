package com.ondo.utils.pn;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/*
import com.g2i.near.commons.utils.PushNotifier;
import com.g2i.near.commons.utils.PushSenderUtil;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;*/

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
//import com.ondo.blr.PushyAPI;
//import com.ondo.blr.PushyPushRequest;

public class PushSenderUtil implements PushNotifier {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(PushSenderUtil.class);

//	private String ONDO_PUSH_SENDER_ID = "ondo.push.sender.id";
//
//	private static final String IOS_DEVICE_TYPE = "01";
//
//	private static final String ANDROID_DEVICE_TYPE = "02";

	private String IOS_DEVICE_CERTIFICATE = "ONDO_PUSH_CERTIFICATE_PROD.p12";

	private static final String DEFAULT_SOUND = "default";

	public PushSenderUtil() {

	}

	public void sendPushNotification(final PushNotification pushObj, LambdaLogger logger) {

		// if (pushObj.getPushDeviceType().startsWith(IOS_DEVICE_TYPE)) {
		if (logger != null) {
			logger.log("%%%%% Push msg cntnt" + pushObj.getPushMsgContent());
		}

		System.out.println("&&&&&&&&& Push token " + pushObj.getPushRegId());
		System.out.println("############Sending to Mars Via Pushy");
		sendIOSPushNotification(pushObj, logger);
		System.out.println("############returning from Mars Via Pushy");
		// } /*
		// * else if (pushObj.getPushDeviceType().startsWith(ANDROID_DEVICE_TYPE)) {
		// * sendAndroidNotification(pushObj);
		// */
		// }

	}

	/**
	 * method for sending push notification
	 */
	private void sendIOSPushNotification(final PushNotification pushObj, LambdaLogger logger) {

		ApnsService service = null;
		try {

			// LOGGER.info(".....Push Device Registration Id ....." +
			// pushObj.getPushRegId());

			InputStream certStream = this.getClass().getClassLoader().getResourceAsStream(IOS_DEVICE_CERTIFICATE);

			if (certStream == null) {
				logger.log("############# NOT able to access certificate");
			}

			service = APNS.newService().withCert(certStream, "Ondo_push").withProductionDestination().build();

			//int days = (int) ((System.currentTimeMillis()) / 1000 / 60 / 60 / 24);

			PayloadBuilder payloadBuilder = APNS.newPayload();

			payloadBuilder = payloadBuilder.badge(0).alertBody(pushObj.getPushMsgContent()).sound(DEFAULT_SOUND);

// check if the message is too long (it won't be sent if it is) and
// trim it if it is.
			if (payloadBuilder.isTooLong()) {
				payloadBuilder = payloadBuilder.shrinkBody();
			}

			String payload = payloadBuilder.build();

			String token = pushObj.getPushRegId();
			System.out.println("#####Before Apple Push Service Payload " + payload + "  token " + token);
			ApnsNotification notification = service.push(token, payload);
			System.out.println("#####After Apple Push Service");
			if (logger != null) {
				logger.log("$$$$$$$ONDO notification ID:" + notification.getIdentifier());
			}

			/*
			 * LOGGER.
			 * info(".....Push message for the Registration Id sent successfully....." +
			 * pushObj.getPushRegId());
			 */

		} catch (Exception pushExcep) {
			/*
			 * LOGGER.error(".....Error in sending Push Notification to IOS device....." +
			 * pushExcep.getMessage());
			 */
			System.out.println("****** Push Notification Exception" + pushExcep.getMessage());
			if (logger != null) {
				logger.log(pushExcep.getMessage());
			}
		} finally {
// check if the service was successfull initialized and stop it
// here, if it was
			if (service != null) {
				logger.log("#####stopping PN service");
				service.stop();
			}

		}
	}

	/**
	 * method for sending push notification
	 */
	/*
	 * private void sendAndroidNotification(final PushNotification pushObj) {
	 * 
	 * try { Random randomGenerator = new Random(); Sender sender = new
	 * Sender(ONDO_PUSH_SENDER_ID);
	 * 
	 * Message message = new
	 * Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
	 * .addData("message", pushObj.getPushMsgContent()) // you can
	 * 
	 * .addData("title", "Near").addData("notId", "" +
	 * randomGenerator.nextInt(1000)).build();
	 * 
	 * Result result;
	 * 
	 * result = sender.send(message, pushObj.getPushRegId(), 1);
	 * 
	 * LOGGER.info("Push Notification Message Result: " + result.toString());
	 * LOGGER.info("Failed: " + result.getErrorCodeName());
	 * LOGGER.info("MessageId: " + result.getMessageId());
	 * LOGGER.info("CanonicalRegistrationId: " +
	 * result.getCanonicalRegistrationId());
	 * 
	 * } catch (Exception androidExcp) {
	 * LOGGER.error("Error in sending Push Notification to android device...." +
	 * androidExcp.getMessage()); } }
	 */

	  void sendIOSPushNotificationByPushy(final PushNotification pushObj, LambdaLogger logger) {
	System.out.println("Alok");
		
		List<String> deviceTokens = new ArrayList<>();

    	// Add your device tokens here
      //  deviceTokens.add("c1da2b1bd85a67e89195b7");
       deviceTokens.add(pushObj.getPushRegId());
        
    	// Convert to String[] array
    //	String[] to = deviceTokens.toArray(new String[deviceTokens.size()]);

    	// Optionally, send to a publish/subscribe topic instead
    	// String to = '/topics/news';

    	// Set payload (any object, it will be serialized to JSON)
    	Map<String, String> payload = new HashMap<>();

    	// Add "message" parameter to payload
    	payload.put("message", pushObj.getPushMsgContent());

    	// iOS notification fields
    	Map<String, Object> notification = new HashMap<>();

    	notification.put("badge", 1);
    	notification.put("sound", "ping.aiff");
    	notification.put("body", "Hello World \u270c");

    	// Prepare the push request
    //	PushyPushRequest push = new PushyPushRequest(payload, to, notification);

    	try {
    		// Try sending the push notification
    		logger.log("Before Pushy  SendPush");
    		//PushyAPI.sendPush(push);
    		logger.log("After Pushy  SendPush");
    	}
    	catch (Exception exc) {
    		// Error, print to console
    		System.out.println(exc.toString());
    	}
    	System.out.println("PN is sent using PUSHY");
	}
	
	
	
	public static void main(String args[]) throws ClassNotFoundException {
 		PushNotification pNObj = new PushNotification();
		pNObj.setPushDeviceType("01");
		pNObj.setPushRegId("FDB02C42BC53E0D2BED38D071B359D61C2F3F233B5C774D1C19B8063ECCC42C2");
		pNObj.setPushMsgContent("Healthiest temperature reported for an ONDO Band wearer. Tap here to review the dashboard.");
  		PushNotifier pushCntnt = new PushSenderUtil();
 		pushCntnt.sendPushNotification(pNObj, null);
		// sendIOSPushNotification(pNObj);
	}

}