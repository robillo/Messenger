package com.devswhocare.messenger.util

import com.devswhocare.messenger.R

object ColorUtil {
    private val colorList = listOf(
        R.color.avatar1,
        R.color.avatar2,
        R.color.avatar3,
        R.color.avatar4,
        R.color.avatar5,
        R.color.avatar6,
        R.color.avatar7,
        R.color.avatar8
    )
    fun getAvatarColorForPosition(position: Int): Int {
        return colorList[position % colorList.size]
    }

    fun getRandomHighlightColor(): Int {
        return colorList.random()
    }
}