package com.devswhocare.messenger.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T>(
    viewBinding: ViewBinding
): RecyclerView.ViewHolder(viewBinding.root)