package com.devswhocare.messenger.ui.messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.devswhocare.messenger.data.database.repository.MessageRepository
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.data.model.MessageListItem
import com.devswhocare.messenger.data.model.Resource
import com.devswhocare.messenger.data.model.Status
import com.devswhocare.messenger.ui.messages.adapter.MessageAdapter
import com.devswhocare.messenger.util.DateTimeUtils
import com.devswhocare.messenger.util.MessagesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesActivityViewModel @Inject constructor(
    private val messagesUtil: MessagesUtil,
    private val messageRepository: MessageRepository,
    private val adapter: MessageAdapter
): ViewModel() {

    private var pageNumber: Int = 1

    private val _saveMessageStatusLiveData = MutableLiveData<Resource<Boolean>>()
    val saveMessageStatusLiveData: LiveData<Resource<Boolean>>
        get() = _saveMessageStatusLiveData

    fun saveContentProviderMessages() {
        _saveMessageStatusLiveData.postValue(Resource.loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val messageList = messagesUtil.getAllSmsFromProvider()
                messageRepository.storeMessagesLocally(messageList)
            }.let {
                _saveMessageStatusLiveData.postValue(Resource.success(true))
            }
        }
    }

    fun fetchMessagePage(pageNumber: Int): LiveData<MutableList<Message>> {
        Log.e("mytag", "fetching for page $pageNumber")
        return messageRepository.getMessagesForPage(pageNumber)
    }

    fun getGroupedMessagesForList(messageList: List<Message>?): MutableList<MessageListItem> {
        var messagesItemList: MutableList<MessageListItem> = ArrayList()
        messageList?.let {
            for(message in it) {
                val messagePostedHour = DateTimeUtils.getHoursAgoAccordingToCurrentTimeUsingMillis(
                    message.messagePostedTime.toLong()
                )
                message.messagePostedHour = getFormattedTimeAgo(messagePostedHour)
                Log.e("mytag", "${message.messagePostedHour}")
            }
            messagesItemList = it.groupBy {
                it.messagePostedHour
            }.flatMap { (hoursAgo, messages) ->
                hoursAgo?.let {
                    listOf(MessageListItem(header = hoursAgo)) +
                            messages.map { MessageListItem(message = it) }
                } ?: run {
                            messages.map { MessageListItem(message = it) }
                }
            }.toMutableList()
        }
        return messagesItemList
    }

    private var hasOlderBeenConsumed = false

    private fun getFormattedTimeAgo(hourCount: Long): String? {
        initHourCountMap()

        return when {
            hourCount < 1.0 -> getHourCountForValue(0)
            hourCount >= 1.0 && hourCount < 2.0 -> getHourCountForValue(1)
            hourCount >= 2.0 && hourCount < 3.0 -> getHourCountForValue(2)
            hourCount >= 3.0 && hourCount < 6.0 -> getHourCountForValue(3)
            hourCount >= 6.0 && hourCount < 12.0 -> getHourCountForValue(6)
            hourCount >= 12.0 && hourCount < 24.0 -> getHourCountForValue(12)
            hourCount >= 24.0 && hourCount < 48.0 -> getHourCountForValue(24)
            else -> getHourCountForValue(48)
        }
    }

    fun getHourCountForValue(value: Int): String? {
        return hourCountDisplayMap[value]?.let { alreadyShown ->
            if(alreadyShown) {
                null
            }
            else {
                hourCountDisplayMap[value] = true
                hourNameMap[value]
            }
        } ?: run {
            null
        }
    }

    fun initHourCountMap() {
        if(hourCountDisplayMap.isNotEmpty() && hourNameMap.isNotEmpty()) return

        hourCountDisplayMap[0] = false
        hourCountDisplayMap[1] = false
        hourCountDisplayMap[2] = false
        hourCountDisplayMap[3] = false
        hourCountDisplayMap[6] = false
        hourCountDisplayMap[12] = false
        hourCountDisplayMap[24] = false
        hourCountDisplayMap[48] = false

        hourNameMap[0] = "recent"
        hourNameMap[1] = "1 hour"
        hourNameMap[2] = "2 hours"
        hourNameMap[3] = "3 hours"
        hourNameMap[6] = "6 hours"
        hourNameMap[12] = "12 hours"
        hourNameMap[24] = "1 day"
        hourNameMap[48] = "older"
    }

    private val hourCountDisplayMap: HashMap<Int, Boolean> = HashMap()
    private val hourNameMap: HashMap<Int, String> = HashMap()

    fun getNextMessages(): LiveData<MutableList<Message>> {
        pageNumber += 1
        return fetchMessagePage(pageNumber - 1)
    }

    fun wasLastFetchFirstPage(): Boolean {
        return (pageNumber - 1) == 1
    }
}