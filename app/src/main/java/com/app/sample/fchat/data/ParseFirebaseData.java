package com.app.sample.fchat.data;

import android.content.Context;

import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.model.Friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bibaswann on 23-06-2017.
 */

public class ParseFirebaseData {
    private SettingsAPI set;

    public ParseFirebaseData(Context context) {
        set = new SettingsAPI(context);
    }

    public List<Friend> getUserList(String userData) {
        List<Friend> frnds = new ArrayList<>();
        String name = null, id = null, photo = null;
        for (String oneUser : userData.split("[}][,]")) {
            String[] temp = oneUser.replace("}", "").split("[{]");
            String[] userParts = temp[temp.length - 1].split(",");
            for (String part : userParts) {
                if (part.split("=")[0].trim().equals("name"))
                    name = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("id"))
                    id = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("photo"))
                    photo = part.split("=")[1].trim();
            }
            if (!set.readSetting("myid").equals(id))
                frnds.add(new Friend(id, name, photo));
        }
        return frnds;
    }

    public List<ChatMessage> getMessageListForUser(String msgData) {
        List<ChatMessage> chats = new ArrayList<>();
        if(msgData.replace("{","").replace("}","").split(",")[1].trim().equals("value = null"))
            return chats;
        ChatMessage tempMsg = null;
        String text = null, msgTime = null, senderId = null, senderName = null, senderPhoto = null, receiverId = null, receiverName = null, receiverPhoto = null;
        for (String msgInConv : msgData.split("[}][,]")) {
            String[] temp = msgInConv.replace("}", "").split("[{]");
            String[] msgParts = temp[temp.length - 1].split(",");
            for (String part : msgParts) {
                if (part.split("=")[0].trim().equals("text"))
                    text = decodeText(part.split("=")[1].trim());
                if (part.split("=")[0].trim().equals("timestamp"))
                    msgTime = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("senderid"))
                    senderId = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("sendername"))
                    senderName = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("senderphoto"))
                    senderPhoto = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("receiverid"))
                    receiverId = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("receivername"))
                    receiverName = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("receiverphoto"))
                    receiverPhoto = part.split("=")[1].trim();
            }
            tempMsg = new ChatMessage(text, msgTime, receiverId, receiverName, receiverPhoto, senderId, senderName, senderPhoto);
            chats.add(tempMsg);
        }
        Collections.sort(chats, new Comparator<ChatMessage>() {
            public int compare(ChatMessage c1, ChatMessage c2) {
                return (c1.getComparableTimestamp() > c2.getComparableTimestamp() ? 1 : (c1.getComparableTimestamp() < c2.getComparableTimestamp() ? -1 : 0));
            }
        });
        return chats;
    }

    public List<ChatMessage> getLastMessageList(String msgData) {
        //Return only last messages of every conversation "I" am involved in
        List<ChatMessage> lastChats = new ArrayList<>();
        ChatMessage tempMsg = null;
        List<ChatMessage> tempMsgList;
        long lastTimeStamp;
        String text = null, msgTime = null, senderId = null, senderName = null, senderPhoto = null, receiverId = null, receiverName = null, receiverPhoto = null;
        for (String oneConv : msgData.split("[}][}][,]")) {
            tempMsgList = new ArrayList<>();
            lastTimeStamp = 0;
            for (String msgInConv : oneConv.split("[}][,]")) {
                String[] temp = msgInConv.replace("}", "").split("[{]");
                String[] msgParts = temp[temp.length - 1].split(",");
                for (String part : msgParts) {
                    if (part.split("=")[0].trim().equals("text"))
                        text = decodeText(part.split("=")[1].trim());
                    if (part.split("=")[0].trim().equals("timestamp")) {
                        msgTime = part.split("=")[1].trim();
                        if (Long.parseLong(msgTime) > lastTimeStamp)
                            lastTimeStamp = Long.parseLong(msgTime);
                    }
                    if (part.split("=")[0].trim().equals("senderid"))
                        senderId = part.split("=")[1].trim();
                    if (part.split("=")[0].trim().equals("sendername"))
                        senderName = part.split("=")[1].trim();
                    if (part.split("=")[0].trim().equals("senderphoto"))
                        senderPhoto = part.split("=")[1].trim();
                    if (part.split("=")[0].trim().equals("receiverid"))
                        receiverId = part.split("=")[1].trim();
                    if (part.split("=")[0].trim().equals("receivername"))
                        receiverName = part.split("=")[1].trim();
                    if (part.split("=")[0].trim().equals("receiverphoto"))
                        receiverPhoto = part.split("=")[1].trim();
                }
                tempMsg = new ChatMessage(text, msgTime, receiverId, receiverName, receiverPhoto, senderId, senderName, senderPhoto);
                tempMsgList.add(tempMsg);
            }
            for (ChatMessage oneTemp : tempMsgList) {
                if ((set.readSetting("myid").equals(oneTemp.getReceiver().getId())) || (set.readSetting("myid").equals(oneTemp.getSender().getId()))) {
                    if (oneTemp.getTimestamp().equals(String.valueOf(lastTimeStamp))) {
                        lastChats.add(oneTemp);
                    }
                }
            }
        }
        return lastChats;
    }

    private String encodeText(String msg) {
        return msg.replace(",", "#comma#").replace("{", "#braceopen#").replace("}", "#braceclose#").replace("=", "#equals#");
    }

    private String decodeText(String msg) {
        return msg.replace("#comma#", ",").replace("#braceopen#", "{").replace("#braceclose#", "}").replace("#equals#", "=");
    }
}
