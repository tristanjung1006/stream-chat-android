package com.getstream.sdk.chat.viewmodel.messages

import androidx.arch.core.executor.testing.InstantExecutorExtension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.createMessage
import com.getstream.sdk.chat.view.messages.MessageListItemWrapper
import io.getstream.chat.android.client.models.ChannelUserRead
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class MessageListItemLiveDataTests {

    private val currentUser = User()
    private lateinit var messages: MutableLiveData<List<Message>>
    private lateinit var threadMessages: MutableLiveData<List<Message>>
    private lateinit var typing: MutableLiveData<List<User>>
    private lateinit var reads: MutableLiveData<List<ChannelUserRead>>

    private lateinit var observer: TestObserver<MessageListItemWrapper>

    private lateinit var sut: MessageListItemLiveData

    @BeforeEach
    fun setUp() {
        observer = TestObserver()
        messages = MutableLiveData(listOf())
        threadMessages = MutableLiveData(listOf())
        typing = MutableLiveData(listOf())
        reads = MutableLiveData(listOf())

        sut = MessageListItemLiveData(currentUser, messages, threadMessages, typing, reads)
    }

    @Test
    fun `When messages are changed if live data in the thread state should not be observed any values `() {
        sut.observeForever(observer)
        threadMessages.value = listOf(createMessage())
        observer.cleanState()

        messages.value = listOf(createMessage())

        observer.lastObservedValue shouldBeEqualTo null
    }

    @Test
    fun `When messages are changed if live data is not in the thread state should observe last assigned value`() {
        sut.observeForever(observer)
        val sendingMessage = createMessage()

        messages.value = listOf(sendingMessage)

        observer.lastObservedValue.also { result ->
            result shouldNotBeEqualTo null
            result!!.listEntities.size shouldBeEqualTo 1
            result.listEntities.any { item ->
                item is MessageListItem.MessageItem && item.message == sendingMessage
            } shouldBeEqualTo true
        }
    }
}

class TestObserver<T> : Observer<T> {
    var lastObservedValue: T? = null
        private set

    override fun onChanged(value: T?) {
        lastObservedValue = value
    }

    fun cleanState() {
        lastObservedValue = null
    }
}