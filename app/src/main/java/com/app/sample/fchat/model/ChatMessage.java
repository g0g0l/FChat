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
package com.app.sample.fchat.model;

import com.app.sample.fchat.data.Tools;

public class ChatMessage {

    private String text;
    private String friendId;
    private String friendName;
    private String friendPhoto;
    private String senderId;
    private String senderName;
    private String senderPhoto;
    private String timestamp;

    public ChatMessage(String text, String timestamp,String friendId, String friendName,String friendPhoto,String senderId,String senderName,String senderPhoto) {
        this.text = text;
        this.timestamp = timestamp;
        this.friendId=friendId;
        this.friendName=friendName;
        this.friendPhoto=friendPhoto;
        this.senderId=senderId;
        this.senderName=senderName;
        this.senderPhoto=senderPhoto;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReadableTime()
    {
        try {
            return Tools.formatTime(Long.valueOf(timestamp));
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    public Friend getReceiver() {
        return new Friend(friendId,friendName,friendPhoto);
    }

    public Friend getSender() {
        return new Friend(senderId,senderName,senderPhoto);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getComparableTimestamp()
    {
        return Long.parseLong(timestamp);
    }
}
