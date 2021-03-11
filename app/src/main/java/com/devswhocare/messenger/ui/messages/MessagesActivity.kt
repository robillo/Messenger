package com.devswhocare.messenger.ui.messages

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.data.model.Status
import com.devswhocare.messenger.databinding.ActivityMessagesBinding
import com.devswhocare.messenger.ui.base.BaseActivity
import com.devswhocare.messenger.ui.message_detail.MessageDetailActivity
import com.devswhocare.messenger.ui.messages.adapter.MessageAdapter
import com.devswhocare.messenger.util.ColorUtil
import com.devswhocare.messenger.util.Constants.extraMessageAddress
import com.devswhocare.messenger.util.DaggerViewModelFactory
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener
import ru.alexbykov.nopaginate.paginate.NoPaginate
import javax.inject.Inject

open class MessagesActivity : BaseActivity(), OnLoadMoreListener,
    MessageAdapter.MessageClickListener {

    @Inject
    lateinit var adapter: MessageAdapter

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    private lateinit var noPaginate: NoPaginate

    private val viewModel by viewModels<MessagesActivityViewModel> {
        daggerViewModelFactory
    }

    private val firstMessagePosition = 0
    private lateinit var binding: ActivityMessagesBinding
    private lateinit var senderAddressToOpen: String

    companion object {
        fun newIntent(context: Context, senderAddressToOpen: String): Intent {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra(extraMessageAddress, senderAddressToOpen)
            return intent
        }
    }

    override fun inject() {
        binding = ActivityMessagesBinding.inflate(layoutInflater)
    }

    override fun getBindingRoot(): ConstraintLayout {
        return binding.root
    }

    override fun setup() {
        setWindowInsets(binding.parentView)
        getExtraParameters()
        setObservers()
        setClickListeners()
        setupAdapter()
        setScrollListener()
        viewModel.saveContentProviderMessages()
    }

    private fun getExtraParameters() {
        intent?.getStringExtra(extraMessageAddress)?.let {
            senderAddressToOpen = it
        }
    }

    private fun setScrollListener() {
        val layoutManager = binding.messagesRecycler.layoutManager as LinearLayoutManager
        binding.messagesRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when(newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        binding.backToTopTv.visibility = View.GONE
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if(
                            layoutManager.findFirstCompletelyVisibleItemPosition() ==
                            firstMessagePosition
                        )
                            binding.backToTopTv.visibility = View.GONE

                        else
                            binding.backToTopTv.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setupAdapter() {
        adapter.setMessageClickListener(this)
        binding.messagesRecycler.adapter = adapter
        noPaginate = NoPaginate.with(binding.messagesRecycler)
            .setOnLoadMoreListener(this).build()
    }

    private fun setObservers() {
        viewModel.saveMessageStatusLiveData.observe(this, {
            when(it.status) {
                Status.SUCCESS -> {
                    binding.loadingLayout.visibility = View.GONE
                    openDetailsScreenIfRequired()
                }
                Status.LOADING -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                }
                Status.ERROR -> {}
            }
        })
    }

    private fun openDetailsScreenIfRequired() {
        if(::senderAddressToOpen.isInitialized) {
            openDetailsScreen(senderAddressToOpen, 0)
        }
    }

    private fun openDetailsScreen(senderAddressToOpen: String, position: Int) {
        startActivity(MessageDetailActivity.newIntent(
            this,
            senderAddressToOpen,
            position
        ))
    }

    private fun setClickListeners() {
        binding.backToTopTv.setOnClickListener {
            binding.backToTopTv.visibility = View.GONE
            binding.messagesRecycler.scrollToPosition(firstMessagePosition)
        }
    }

    override fun onLoadMore() {
        viewModel.getNextMessages().observe(this, {
            it?.let {
                val list = viewModel.getGroupedMessagesForList(it)
                if(list.isNotEmpty()) {
                    if(viewModel.wasLastFetchFirstPage()) {
                        adapter.setMessagesList(list)
                    }
                    else {
                        adapter.addMessages(list)
                    }
                }
                else {
                    //no messages to show
                }
            } ?: run {
                //no messages to show
            }
        })
    }

    override fun onMessageClicked(position: Int, message: Message, accentColor: Int) {
        openDetailsScreen(message.messageSenderAddress, position)
    }
}