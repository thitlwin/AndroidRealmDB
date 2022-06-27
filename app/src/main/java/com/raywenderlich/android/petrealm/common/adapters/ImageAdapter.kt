/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.petrealm.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.petrealm.databinding.ItemImageBinding
import javax.inject.Inject

class ImageAdapter @Inject constructor() :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

  private val images = mutableListOf<Int>()
  private var tracker: SelectionTracker<Long>? = null

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
    val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ImageViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
    val image = images[position]
    holder.bind(image, tracker?.isSelected(position.toLong()) ?: false)
  }

  override fun getItemCount() = images.size

  override fun getItemId(position: Int) = position.toLong()

  fun addImages(images: List<Int>) {
    this.images.addAll(images)
  }

  fun setTracker(tracker: SelectionTracker<Long>) {
    this.tracker = tracker
  }

  fun getSelectedImage(position: Long): Int {
    return images[position.toInt()]
  }

  fun selectImage(image: Int) {
    val position = this.images.indexOf(image)

    if (position >= 0) {
      val selected = this.tracker?.select(position.toLong())
    }
  }

  class ImageViewHolder(private val binding: ItemImageBinding) :
      RecyclerView.ViewHolder(binding.root) {

    fun bind(@DrawableRes imageRes: Int, isSelected: Boolean) {
      with(binding) {
        image.setImageResource(imageRes)
        selectedImage.isVisible = isSelected
      }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object :
        ItemDetailsLookup.ItemDetails<Long>() {
      override fun getPosition() = adapterPosition

      override fun getSelectionKey() = itemId
    }
  }
}