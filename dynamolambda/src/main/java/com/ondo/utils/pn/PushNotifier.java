package com.ondo.utils.pn;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public interface PushNotifier {

	void sendPushNotification(PushNotification pushObj,LambdaLogger logger);

}