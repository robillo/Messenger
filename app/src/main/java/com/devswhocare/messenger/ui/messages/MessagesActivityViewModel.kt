package com.devswhocare.messenger.ui.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devswhocare.messenger.data.database.repository.MessageRepository
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.data.model.MessageListItem
import com.devswhocare.messenger.data.model.Resource
import com.devswhocare.messenger.util.DateTimeUtils
import com.devswhocare.messenger.util.MessagesUtil
import com.devswhocare.messenger.util.SharedPreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesActivityViewModel @Inject constructor(
    private val messagesUtil: MessagesUtil,
    private val messageRepository: MessageRepository,
    private val sharedPreferenceUtil: SharedPreferenceUtil
): ViewModel() {

    private var pageNumber: Int = 1

    private val hourNameMap: HashMap<Int, String> = HashMap()
    private val hourCountDisplayMap: HashMap<Int, Boolean> = HashMap()

    private val _saveMessageStatusLiveData = MutableLiveData<Resource<Boolean>>()
    val saveMessageStatusLiveData: LiveData<Resource<Boolean>>
        get() = _saveMessageStatusLiveData

    companion object {
        const val extraHasDataBeenLocallyStoredOnce = "hasDataBeenLocallyStoredOnce"
    }

    private fun setDataHasBeenLocallyStoredOnce() {
        sharedPreferenceUtil.putBoolean(extraHasDataBeenLocallyStoredOnce, true)
    }

    fun hasDataBeenLocallyStoredOnce(): Boolean {
        return sharedPreferenceUtil.getBoolean(extraHasDataBeenLocallyStoredOnce)
    }

    fun saveContentProviderMessages() {
        _saveMessageStatusLiveData.postValue(Resource.loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val messageList = messagesUtil.getAllSmsFromProvider()
                messageRepository.storeMessagesLocally(messageList)
            }.let {
                setDataHasBeenLocallyStoredOnce()
                _saveMessageStatusLiveData.postValue(Resource.success(true))
            }
        }
    }

    private fun fetchMessagePage(pageNumber: Int): LiveData<MutableList<Message>> {
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

    private fun getFormattedTimeAgo(hourCount: Long): String? {
        val valueForHourCount: Int = when {
            hourCount < 1 -> 0
            hourCount in 1 until 2 -> 1
            hourCount in 2 until 3 -> 2
            hourCount in 3 until 6 -> 3
            hourCount in 6 until 12 -> 6
            hourCount in 12 until 24 -> 12
            hourCount in 24 until 48 -> 24
            else -> 48
        }
        return getHourCountForValue(valueForHourCount)
    }

    private fun getHourCountForValue(value: Int): String? {
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

    fun setTimeAgoMaps(hours: IntArray, hourSectionNames: Array<String>) {
        for(index in hours.indices) {
            hourNameMap[hours[index]] = hourSectionNames[index]
            hourCountDisplayMap[hours[index]] = false
        }
    }

    fun getNextMessages(): LiveData<MutableList<Message>> {
        pageNumber += 1
        return fetchMessagePage(pageNumber - 1)
    }

    fun wasLastFetchFirstPage(): Boolean {
        return (pageNumber - 1) == 1
    }
}