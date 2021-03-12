package com.devswhocare.messenger.ui.messages

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devswhocare.messenger.R
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.data.model.Status
import com.devswhocare.messenger.databinding.ActivityMessagesBinding
import com.devswhocare.messenger.ui.base.BaseActivity
import com.devswhocare.messenger.ui.message_detail.MessageDetailActivity
import com.devswhocare.messenger.ui.messages.adapter.MessageAdapter
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

    private val scheme = "package"
    private val firstMessagePosition = 0
    private val requestCodeMessagePermissions: Int = 12
    private lateinit var binding: ActivityMessagesBinding
    private lateinit var senderAddressToOpen: String

    companion object {
        fun newIntent(context: Context, senderAddressToOpen: String): Intent {
            val intent = Intent(context, MessagesActivity::class.java)
            Log.e("mytag", "$senderAddressToOpen new intent")
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
        viewModel.setTimeAgoMaps(
                resources.getIntArray(R.array.header_hours),
                resources.getStringArray(R.array.header_names)
        )
        setObservers()
        setClickListeners()
        setupAdapter()
        setScrollListener()

        fetchDataIfPermissionsGiven()
    }

    private fun fetchDataIfPermissionsGiven() {
        if(hasMessagesPermission())
            queryContentProviderForNewMessages()
        else
            requestMessagePermissions()
    }

    private fun requestMessagePermissions() {
        if(
                canRequestPermission(Manifest.permission.READ_SMS) &&
                canRequestPermission(Manifest.permission.RECEIVE_SMS)
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                    ),
                    requestCodeMessagePermissions
            )
        }
        else {
            openSettings()
        }
    }

    @Suppress("DEPRECATION")
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(scheme, packageName, null)
        intent.data = uri
        startActivityForResult(intent, requestCodeMessagePermissions)
    }

    private fun canRequestPermission(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
        )
    }

    private fun hasMessagesPermission(): Boolean {
        val permissions = listOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
        )
        var isAllPermissionsGranted = true
        permissions.forEach {
            if (checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionsGranted = false
                return@forEach
            }
        }
        return isAllPermissionsGranted
    }

    private fun queryContentProviderForNewMessages() {
        viewModel.saveContentProviderMessages()
    }

    private fun getExtraParameters() {
        intent?.getStringExtra(extraMessageAddress)?.let {
            senderAddressToOpen = it
            Log.e("mytag", "received $senderAddressToOpen $it")
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            requestCodeMessagePermissions -> {
                fetchDataIfPermissionsGiven()
            }
        }
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
                Status.LOADING ->
                    if(!viewModel.hasDataBeenLocallyStoredOnce())
                        binding.loadingLayout.visibility = View.VISIBLE

                Status.ERROR -> {}
            }
        })
    }

    private fun openDetailsScreenIfRequired() {
        if(::senderAddressToOpen.isInitialized) {
            openDetailsScreen(senderAddressToOpen, 0)
            Log.e("mytag", "open $senderAddressToOpen")
        }
    }

    private fun openDetailsScreen(senderAddressToOpen: String, position: Int) {
        Log.e("mytag", "sender address (activity) is $senderAddressToOpen")
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
                    if(viewModel.wasLastFetchFirstPage())
                        adapter.setMessagesList(list)
                    else
                        adapter.addMessages(list)
                }
            }
        })
    }

    override fun onMessageClicked(position: Int, message: Message, accentColor: Int) {
        Log.e("mytag", "message click $message $position")
        openDetailsScreen(message.messageSenderAddress, position)
    }
}