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
package com.app.sample.fchat.model

import com.app.sample.fchat.data.Tools

class ChatMessage(var text: String?, var timestamp: String?, var friendId: String?, var friendName: String?, var friendPhoto: String?, var senderId: String?, var senderName: String?, var senderPhoto: String?, var isRead: Boolean?) {

    val readableTime: String?
        get() {
            return try {
                Tools.formatTime(java.lang.Long.valueOf(timestamp))
            } catch (ignored: NumberFormatException) {
                null
            }

        }

    val receiver: Friend
        get() = Friend(friendId!!, friendName!!, friendPhoto!!)

    val sender: Friend
        get() = Friend(senderId!!, senderName!!, senderPhoto!!)

    val comparableTimestamp: Long
        get() = java.lang.Long.parseLong(timestamp)


}
