/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.sample.fchat.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

import static com.app.sample.fchat.util.Constants.LOG_TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(LOG_TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(LOG_TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(LOG_TAG, "FCM Data Message: " + remoteMessage.getData());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String token = FirebaseInstanceId.getInstance().getToken();

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }
}
