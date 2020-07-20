/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickersbrasil.presentation.home.ViewController

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.stickersbrasil.R
import com.facebook.drawee.view.SimpleDraweeView

class StickerPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    val stickerPreviewView: SimpleDraweeView = itemView.findViewById(R.id.sticker_preview)
}