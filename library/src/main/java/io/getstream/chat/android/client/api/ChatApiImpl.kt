package io.getstream.chat.android.client.api

import io.getstream.chat.android.client.api.models.*
import io.getstream.chat.android.client.call.ChatCall
import io.getstream.chat.android.client.logger.ChatLogger
import io.getstream.chat.android.client.models.*
import io.getstream.chat.android.client.parser.JsonParser
import io.getstream.chat.android.client.utils.ProgressCallback
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class ChatApiImpl(
    private val retrofitApi: RetrofitApi,
    private val retrofitCdnApi: RetrofitCdnApi,
    private val config: ChatConfig,
    private val parser: JsonParser,
    private val logger: ChatLogger
) : ChatApi {

    private var userId: String = ""
    private var connectionId: String = ""
    private val callMapper =
        RetrofitCallMapper(parser)

    override fun setConnection(userId: String, connectionId: String) {
        this.userId = userId
        this.connectionId = connectionId
    }

    override fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        mimeType: String,
        callback: ProgressCallback
    ) {
        val body = ProgressRequestBody(
            file,
            mimeType,
            callback
        )
        val part = createFormData("file", file.name, body)

        if (mimeType.contains("image")) {
            retrofitCdnApi.sendImage(
                channelType,
                channelId,
                part,
                config.apiKey,
                userId,
                connectionId
            ).enqueue(RetroProgressCallback(callback))
        } else {
            retrofitCdnApi.sendFile(
                channelType,
                channelId,
                part,
                config.apiKey,
                userId,
                connectionId
            ).enqueue(RetroProgressCallback(callback))
        }
    }

    override fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        mimeType: String
    ): ChatCall<String> {

        val part = createFormData("file", file.name, file.asRequestBody())

        if (mimeType.contains("image")) {
            return callMapper.map(
                retrofitCdnApi.sendImage(
                    channelType,
                    channelId,
                    part,
                    config.apiKey,
                    userId,
                    connectionId
                )
            ).map {
                it.file
            }
        } else {
            return callMapper.map(
                retrofitCdnApi.sendFile(
                    channelType,
                    channelId,
                    part,
                    config.apiKey,
                    userId,
                    connectionId
                )
            ).map {
                it.file
            }
        }
    }

    override fun deleteFile(channelType: String, channelId: String, url: String): ChatCall<Unit> {
        return callMapper.map(
            retrofitCdnApi.deleteFile(channelType, channelId, config.apiKey, connectionId, url)
        ).map {
            Unit
        }
    }

    override fun deleteImage(channelType: String, channelId: String, url: String): ChatCall<Unit> {
        return callMapper.map(
            retrofitCdnApi.deleteImage(channelType, channelId, config.apiKey, connectionId, url)
        ).map {
            Unit
        }
    }

    override fun addDevice(firebaseToken: String): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.addDevices(config.apiKey, userId, connectionId, AddDeviceRequest(firebaseToken))
        ).map {
            Unit
        }
    }

    override fun deleteDevice(firebaseToken: String): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.deleteDevice(
                firebaseToken, config.apiKey, userId, connectionId
            )
        ).map {
            Unit
        }
    }

    override fun getDevices(): ChatCall<List<Device>> {
        return callMapper.map(
            retrofitApi.getDevices(config.apiKey, userId, connectionId)
        ).map {
            it.devices
        }
    }

    override fun searchMessages(request: SearchMessagesRequest): ChatCall<List<Message>> {
        return callMapper.map(
            retrofitApi.searchMessages(config.apiKey, connectionId, request)
        ).map {
            it.results.map { resp ->
                resp.message
            }
        }
    }

    override fun getRepliesMore(
        messageId: String,
        firstId: String,
        limit: Int
    ): ChatCall<List<Message>> {
        return callMapper.map(
            retrofitApi.getRepliesMore(
                messageId,
                config.apiKey,
                userId,
                connectionId,
                limit,
                firstId
            )
        ).map {
            it.messages
        }
    }

    override fun getReplies(messageId: String, limit: Int): ChatCall<List<Message>> {
        return callMapper.map(
            retrofitApi.getReplies(
                messageId,
                config.apiKey,
                userId,
                connectionId,
                limit
            )
        ).map {
            it.messages
        }
    }

    override fun getReactions(
        messageId: String,
        offset: Int,
        limit: Int
    ): ChatCall<List<Reaction>> {
        return callMapper.map(
            retrofitApi.getReactions(
                messageId,
                config.apiKey,
                connectionId,
                offset,
                limit
            )
        ).map {
            it.reactions
        }
    }

    override fun deleteReaction(messageId: String, reactionType: String): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.deleteReaction(
                messageId,
                reactionType,
                config.apiKey,
                userId,
                connectionId
            )
        ).map {
            it.message
        }
    }

    override fun deleteMessage(messageId: String): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.deleteMessage(
                messageId,
                config.apiKey,
                userId,
                connectionId
            )
        ).map {
            it.message
        }
    }

    override fun sendAction(request: SendActionRequest): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.sendAction(
                request.messageId, config.apiKey, userId, connectionId, request
            )
        ).map {
            it.message
        }
    }

    override fun getMessage(messageId: String): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.getMessage(messageId, config.apiKey, userId, connectionId)
        ).map {
            it.message
        }
    }

    override fun sendMessage(
        channelType: String,
        channelId: String,
        message: Message
    ): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.sendMessage(
                channelType,
                channelId,
                config.apiKey,
                userId,
                connectionId,
                MessageRequest(message)
            )
        ).map {
            it.message
        }
    }

    override fun updateMessage(
        message: Message
    ): ChatCall<Message> {
        return callMapper.map(
            retrofitApi.updateMessage(
                message.id,
                config.apiKey,
                userId,
                connectionId,
                MessageRequest(message)
            )
        ).map {
            it.message
        }
    }


    override fun queryChannels(query: QueryChannelsRequest): ChatCall<List<Channel>> {
        return callMapper.map(
            retrofitApi.queryChannels(
                config.apiKey,
                userId,
                connectionId,
                query
            )
        ).map {
            flattenChannels(it.channels)
        }
    }

    override fun stopWatching(
        channelType: String,
        channelId: String
    ): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.stopWatching(
                channelType,
                channelId,
                config.apiKey,
                connectionId,
                emptyMap()
            )
        ).map {
            Unit
        }
    }

    override fun queryChannel(
        channelType: String,
        channelId: String,
        query: ChannelQueryRequest
    ): ChatCall<Channel> {

        if (channelId.isEmpty()) {
            return callMapper.map(
                retrofitApi.queryChannel(
                    channelType,
                    config.apiKey,
                    userId,
                    connectionId,
                    query
                )
            ).map { flattenChannel(it) }
        } else {
            return callMapper.map(
                retrofitApi.queryChannel(
                    channelType,
                    channelId,
                    config.apiKey,
                    userId,
                    connectionId,
                    query
                )
            ).map { flattenChannel(it) }
        }
    }

    override fun updateChannel(
        channelType: String,
        channelId: String,
        request: UpdateChannelRequest
    ): ChatCall<Channel> {
        return callMapper.map(
            retrofitApi.updateChannel(
                channelType,
                channelId,
                config.apiKey,
                connectionId,
                request
            )
        ).map { flattenChannel(it) }
    }

    override fun markRead(
        channelType: String,
        channelId: String,
        messageId: String
    ): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.markRead(
                channelType,
                channelId,
                config.apiKey,
                userId,
                connectionId,
                MarkReadRequest(messageId)
            )
        ).map {
            Unit
        }
    }

    override fun showChannel(channelType: String, channelId: String): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.showChannel(channelType, channelId, config.apiKey, connectionId, emptyMap())
        ).map {
            Unit
        }
    }

    override fun hideChannel(
        channelType: String,
        channelId: String,
        clearHistory: Boolean
    ): ChatCall<Unit> {
        return callMapper.map(
            retrofitApi.hideChannel(
                channelType,
                channelId,
                config.apiKey,
                connectionId,
                HideChannelRequest(clearHistory)
            )
        ).map {
            Unit
        }
    }

    override fun rejectInvite(channelType: String, channelId: String): ChatCall<Channel> {
        return callMapper.map(
            retrofitApi.rejectInvite(
                channelType, channelId, config.apiKey, connectionId, RejectInviteRequest()
            )
        ).map {
            it.channel
        }
    }

    override fun acceptInvite(
        channelType: String,
        channelId: String,
        message: String
    ): ChatCall<Channel> {
        return callMapper.map(
            retrofitApi.acceptInvite(
                channelType,
                channelId,
                config.apiKey,
                connectionId,
                AcceptInviteRequest(
                    User(
                        userId
                    ), AcceptInviteRequest.AcceptInviteMessage(message)
                )
            )
        ).map {
            it.channel
        }
    }

    override fun deleteChannel(channelType: String, channelId: String): ChatCall<Channel> {
        return callMapper.map(
            retrofitApi.deleteChannel(channelType, channelId, config.apiKey, connectionId)
        ).map {
            flattenChannel(it)
        }
    }

    override fun markAllRead(): ChatCall<EventResponse> {
        return callMapper.map(
            retrofitApi.markAllRead(
                config.apiKey,
                userId,
                connectionId
            )
        )
    }

    override fun setGuestUser(userId: String, userName: String): ChatCall<TokenResponse> {
        return callMapper.map(
            retrofitApi.setGuestUser(
                config.apiKey,
                body = GuestUserRequest(
                    id = userId,
                    name = userName
                )
            )
        )
    }

    override fun getUsers(
        queryUsers: QueryUsers
    ): ChatCall<QueryUserListResponse> {
        return callMapper.map(
            retrofitApi.queryUsers(
                config.apiKey,
                connectionId,
                queryUsers
            )
        )
    }

    override fun addMembers(
        channelType: String,
        channelId: String,
        members: List<String>
    ) = callMapper.map(
        retrofitApi.addMembers(
            apiKey = config.apiKey,
            connectionId = connectionId,
            channelType = channelType,
            channelId = channelId,
            body = AddMembersRequest(
                members = members
            )
        )
    )

    override fun removeMembers(
        channelType: String,
        channelId: String,
        members: List<String>
    ) = callMapper.map(
        retrofitApi.removeMembers(
            apiKey = config.apiKey,
            connectionId = connectionId,
            channelType = channelType,
            channelId = channelId,
            body = RemoveMembersRequest(
                members = members
            )
        )
    )

    override fun muteUser(
        targetId: String
    ): ChatCall<MuteUserResponse> {
        return callMapper.map(
            retrofitApi.muteUser(
                config.apiKey,
                userId,
                connectionId,
                MuteUserRequest(targetId, userId)
            )
        )
    }

    override fun unMuteUser(
        targetId: String
    ): ChatCall<MuteUserResponse> {

        return callMapper.map(
            retrofitApi.unMuteUser(
                config.apiKey,
                userId,
                connectionId,
                MuteUserRequest(targetId, userId)
            )
        )
    }

    override fun flag(
        targetId: String
    ): ChatCall<FlagResponse> {

        val body: MutableMap<String, String> = HashMap()
        body["target_user_id"] = targetId

        return callMapper.map(
            retrofitApi.flag(
                config.apiKey,
                userId,
                connectionId,
                body
            )
        )
    }

    override fun banUser(
        targetId: String,
        timeout: Int,
        reason: String,
        channelType: String,
        channelId: String
    ): ChatCall<CompletableResponse> {

        return callMapper.map(
            retrofitApi.banUser(
                apiKey = config.apiKey,
                connectionId = connectionId,
                body = BanUserRequest(
                    targetUserId = targetId,
                    timeout = timeout,
                    reason = reason,
                    channelType = channelType,
                    channelId = channelId
                )
            )
        )
    }

    override fun unBanUser(
        targetId: String,
        channelType: String,
        channelId: String
    ): ChatCall<CompletableResponse> {

        return callMapper.map(
            retrofitApi.unBanUser(
                apiKey = config.apiKey,
                connectionId = connectionId,
                targetUserId = targetId,
                channelId = channelId,
                channelType = channelType
            )
        )
    }

    private fun flattenChannels(responses: List<ChannelResponse>): List<Channel> {
        return responses.map {
            flattenChannel(it)
        }
    }

    private fun flattenChannel(response: ChannelResponse): Channel {
        response.channel.watcherCount = response.watcher_count
        response.channel.read = response.read.orEmpty()
        response.channel.members = response.members.orEmpty()
        response.channel.messages = response.messages.orEmpty()
        return response.channel
    }

}