/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickersbrasil.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.stickersbrasil.R
import br.com.stickersbrasil.presentation.home.ViewController.StickerPreviewViewHolder

class StickerPreviewAdapter internal constructor(

    private val layoutInflater: LayoutInflater,
    private val errorResource: Int,
    private val cellSize: Int,
    private val cellPadding: Int,
    private val stickerPack: StickerPack

) : RecyclerView.Adapter<StickerPreviewViewHolder>() {
    private val cellLimit = 0
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StickerPreviewViewHolder {
        val itemView = layoutInflater.inflate(R.layout.sticker_image_item, viewGroup, false)
        val vh =
            StickerPreviewViewHolder(
                itemView
            )
        val layoutParams = vh.stickerPreviewView.layoutParams
        layoutParams.height = cellSize
        layoutParams.width = cellSize
        vh.stickerPreviewView.layoutParams = layoutParams
        vh.stickerPreviewView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding)
        return vh
    }

    override fun onBindViewHolder(stickerPreviewViewHolder: StickerPreviewViewHolder, i: Int) {
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource)
        stickerPreviewViewHolder.stickerPreviewView.setImageURI(
            StickerPackLoader.getStickerAssetUri(
                stickerPack.identifier,
                stickerPack.stickers[i].imageFileName
            )
        )
    }

    override fun getItemCount(): Int {
        return stickerPack.stickers.size
    }

}