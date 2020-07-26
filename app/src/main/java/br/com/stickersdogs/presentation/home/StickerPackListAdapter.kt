/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickersdogs.presentation.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.Formatter
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.stickersdogs.R
import com.facebook.drawee.view.SimpleDraweeView
import br.com.stickersdogs.presentation.home.StickerPackLoader.getStickerAssetUri
import br.com.stickersdogs.presentation.home.ViewController.StickerPackListItemViewHolder

private class StickerPackListAdapter internal constructor(

    private var stickerPacks: List<StickerPack>,
    private val onAddButtonClickedListener: OnAddButtonClickedListener
) : RecyclerView.Adapter<StickerPackListItemViewHolder>() {
    private var maxNumberOfStickersInARow = 0
    private var minMarginBetweenImages = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StickerPackListItemViewHolder {
        val context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val stickerPackRow =
            layoutInflater.inflate(R.layout.sticker_packs_list_item, viewGroup, false)
        return StickerPackListItemViewHolder(
            stickerPackRow
        )
    }

    override fun onBindViewHolder(viewHolder: StickerPackListItemViewHolder, index: Int) {
        val pack = stickerPacks[index]
        val context = viewHolder.publisherView.context
        viewHolder.publisherView.text = pack.publisher
        viewHolder.fileSizeView.text = Formatter.formatShortFileSize(context, pack.totalSize)
        viewHolder.titleView.text = pack.name
        viewHolder.container.setOnClickListener { view: View ->
            val intent = Intent(view.context, DetailsPackActivity::class.java)
            intent.putExtra(DetailsPackActivity.EXTRA_SHOW_UP_BUTTON, true)
            intent.putExtra(DetailsPackActivity.EXTRA_STICKER_PACK_DATA, pack)
            view.context.startActivity(intent)
        }
        viewHolder.imageRowView.removeAllViews()
        //if this sticker pack contains less stickers than the max, then take the smaller size.
        val actualNumberOfStickersToShow = Math.min(maxNumberOfStickersInARow, pack.stickers.size)
        for (i in 0 until actualNumberOfStickersToShow) {
            val rowImage = LayoutInflater.from(context).inflate(
                R.layout.sticker_packs_list_image_item,
                viewHolder.imageRowView,
                false
            ) as SimpleDraweeView
            rowImage.setImageURI(
                getStickerAssetUri(
                    pack.identifier,
                    pack.stickers[i].imageFileName
                )
            )
            val lp = rowImage.layoutParams as LinearLayout.LayoutParams
            val marginBetweenImages = minMarginBetweenImages - lp.leftMargin - lp.rightMargin
            if (i != actualNumberOfStickersToShow - 1 && marginBetweenImages > 0) { //do not set the margin for the last image
                lp.setMargins(
                    lp.leftMargin,
                    lp.topMargin,
                    lp.rightMargin + marginBetweenImages,
                    lp.bottomMargin
                )
                rowImage.layoutParams = lp
            }
            viewHolder.imageRowView.addView(rowImage)
        }
        setAddButtonAppearance(viewHolder.addButton, pack)
    }

    private fun setAddButtonAppearance(addButton: ImageView, pack: StickerPack) {
        if (pack.isWhitelisted) {
            addButton.setImageResource(R.drawable.ic_checked)
            addButton.isClickable = false
            addButton.setOnClickListener(null)
            setBackground(addButton, null)
        } else {
            addButton.setImageResource(R.drawable.ic_add_gradient)
            addButton.setOnClickListener { v: View? ->
                onAddButtonClickedListener.onAddButtonClicked(
                    pack
                )
            }
            val outValue = TypedValue()
            addButton.context.theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
            )
            addButton.setBackgroundResource(outValue.resourceId)
        }
    }

    private fun setBackground(view: View, background: Drawable?) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.background = background
        } else {
            view.setBackgroundDrawable(background)
        }
    }

    override fun getItemCount(): Int {
        return stickerPacks.size
    }

    fun setImageRowSpec(maxNumberOfStickersInARow: Int, minMarginBetweenImages: Int) {
        this.minMarginBetweenImages = minMarginBetweenImages
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow
            notifyDataSetChanged()
        }
    }

    fun setStickerPackList(stickerPackList: List<StickerPack>) {
        stickerPacks = stickerPackList
    }

    interface OnAddButtonClickedListener {
        fun onAddButtonClicked(stickerPack: StickerPack?)
    }

}