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

package com.raywenderlich.android.petrealm.owners.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.petrealm.R
import com.raywenderlich.android.petrealm.databinding.ItemOwnerBinding
import com.raywenderlich.android.petrealm.owners.models.Owner
import com.squareup.picasso.Picasso
import javax.inject.Inject

class OwnerAdapter @Inject constructor() : RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {

  private val owners = mutableListOf<Owner>()
  private var onItemClicked: ((ownerId: String) -> Unit)? = null
  private var removeAction: ((ownerId: String) -> Unit)? = null
  private var showRemoveButtons = false

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
    val binding = ItemOwnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return OwnerViewHolder(binding)
  }

  override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
    val owner = owners[position]
    holder.bind(owner)
  }

  override fun getItemCount() = owners.size

  fun addItems(owners: List<Owner>) {
    this.owners.clear()
    this.owners.addAll(owners)
    notifyDataSetChanged()
  }

  fun addOnClickAction(onItemClicked: (ownerId: String) -> Unit) {
    this.onItemClicked = onItemClicked
  }

  fun addRemoveAction(removeAction: (ownerId: String) -> Unit) {
    showRemoveButtons = true
    this.removeAction = removeAction
  }

  inner class OwnerViewHolder(private val binding: ItemOwnerBinding) :
      RecyclerView.ViewHolder(binding.root) {

    fun bind(owner: Owner) {
      with(binding) {
        owner.image?.let {
          Picasso.get().load(it).into(imageOwner)
        }
        textViewOwnerName.text = owner.name
        textViewNumberOfPets.text = binding.root.context.getString(R.string.number_pets,
            owner.numberOfPets)
        buttonRemove.isVisible = showRemoveButtons
        buttonRemove.setOnClickListener {
          owners.removeAt(adapterPosition)
          notifyItemRemoved(adapterPosition)
          removeAction?.invoke(owner.id)
        }
        binding.root.setOnClickListener {
          onItemClicked?.invoke(owner.id)
        }
      }
    }
  }
}
