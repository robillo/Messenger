package com.devswhocare.messenger.ui.message_detail

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.devswhocare.messenger.R
import com.devswhocare.messenger.databinding.ActivityMessageDetailBinding
import com.devswhocare.messenger.ui.base.BaseActivity
import com.devswhocare.messenger.ui.message_detail.adapter.FullMessageAdapter
import com.devswhocare.messenger.util.ColorUtil
import com.devswhocare.messenger.util.Constants.extraMessageAddress
import com.devswhocare.messenger.util.Constants.extraPosition
import com.devswhocare.messenger.util.DaggerViewModelFactory
import javax.inject.Inject

class MessageDetailActivity : BaseActivity() {

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    @Inject
    lateinit var adapter: FullMessageAdapter

    private val viewModel by viewModels<MessageDetailViewModel> {
        daggerViewModelFactory
    }

    private lateinit var senderAddressToOpen: String
    private var position: Int = -1

    companion object {
        fun newIntent(
            context: Context, senderAddressToOpen: String, position: Int
        ): Intent {
            val intent = Intent(context, MessageDetailActivity::class.java)
            intent.putExtra(extraMessageAddress, senderAddressToOpen)
            intent.putExtra(extraPosition, position)
            return intent
        }
    }

    private lateinit var binding: ActivityMessageDetailBinding

    override fun inject() {
        binding = ActivityMessageDetailBinding.inflate(layoutInflater)
    }

    override fun getBindingRoot(): ConstraintLayout {
        return binding.root
    }

    override fun setup() {
        setWindowInsets(binding.parentView)
        getExtraParameters()
        setClickListeners()
        renderColors()
        setAdapter()
        setAddressDetails()
        fetchAddressMessages()
    }

    private fun setAdapter() {
        binding.fullMessageRecycler.adapter = adapter
    }

    private fun getExtraParameters() {
        intent?.getStringExtra(extraMessageAddress)?.let {
            senderAddressToOpen = it
        }
        position =
            intent.getIntExtra(
                extraPosition,
                0
            )
    }

    private fun fetchAddressMessages() {
        viewModel.getMessagesForAddress(senderAddressToOpen).observe(this, { list ->
            list?.let {
                adapter.setMessages(list)
            }
        })
    }

    private fun setClickListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun renderColors() {
        binding.profileBackgroundIv.setColorFilter(
            ContextCompat.getColor(this, ColorUtil.getAvatarColorForPosition(position)),
            PorterDuff.Mode.SRC_OVER
        )
    }

    private fun setAddressDetails() {
        binding.messageSenderNameTv.text = senderAddressToOpen
        binding.messageSenderMessageTv.text = String.format(
            getString(R.string.your_messages_with_s, senderAddressToOpen)
        )
        val initialLetter = senderAddressToOpen.first()
        if(Character.isLetter(initialLetter)) {
            binding.messageSenderInitialTv.visibility = View.VISIBLE
            binding.personIv.visibility = View.GONE
            binding.messageSenderInitialTv.text = initialLetter.toString()
        }
        else {
            binding.messageSenderInitialTv.visibility = View.GONE
            binding.personIv.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animateBackPressTransition(R.anim.anim_none, R.anim.slide_from_top_down)
    }
}