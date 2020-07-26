/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickersdogs.presentation.home.ViewController

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.stickersdogs.R

internal class StickerPackListItemViewHolder(val container: View) : RecyclerView.ViewHolder(container) {
    @JvmField
    val titleView: TextView = container.findViewById(R.id.sticker_pack_title)

    @JvmField
    val publisherView: TextView = container.findViewById(R.id.sticker_pack_publisher)

    @JvmField
    val fileSizeView: TextView = container.findViewById(R.id.sticker_pack_filesize)

    @JvmField
    val addButton: ImageView = container.findViewById(R.id.add_button_on_list)

    @JvmField
    val imageRowView: LinearLayout = container.findViewById(R.id.sticker_packs_list_item_image_list)

}