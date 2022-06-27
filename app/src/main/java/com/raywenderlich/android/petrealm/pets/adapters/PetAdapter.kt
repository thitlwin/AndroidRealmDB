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

package com.raywenderlich.android.petrealm.pets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.petrealm.R
import com.raywenderlich.android.petrealm.databinding.ItemPetBinding
import com.raywenderlich.android.petrealm.pets.models.Pet
import com.raywenderlich.android.petrealm.pets.ui.PetsToAdoptFragmentDirections
import com.squareup.picasso.Picasso
import javax.inject.Inject

class PetAdapter @Inject constructor() : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

  private val pets = mutableListOf<Pet>()
  private var removePet: ((petId: String) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
    val binding = ItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return PetViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
    val pet = pets[position]
    holder.bind(pet)
  }

  override fun getItemCount() = pets.size

  fun addItems(petsToAdopt: List<Pet>) {
    pets.clear()
    pets.addAll(petsToAdopt)
    notifyDataSetChanged()
  }

  fun addRemovePetAction(removePet: (petId: String) -> Unit) {
    this.removePet = removePet
  }

  inner class PetViewHolder(private val binding: ItemPetBinding) : RecyclerView.ViewHolder(binding
      .root) {

    fun bind(pet: Pet) {
      with(binding) {
        pet.image?.let {
          Picasso.get().load(it).into(imagePet)
        }
        textViewPetAge.text = root.context.getString(R.string.pet_age, pet.age, root.context
            .resources.getQuantityString(R.plurals.age, pet.age))
        textViewPetName.text = pet.name
        textViewPetType.text = pet.petType
        textViewPetOwner.isVisible = pet.isAdopted
        buttonAdopt.isVisible = pet.isAdopted.not()
        buttonRemove.isVisible = pet.isAdopted.not()
        pet.ownerName?.let { owner ->
          textViewPetOwner.text = root.context.getString(R.string.owned_by, owner)
        }
        buttonAdopt.setOnClickListener {
          val navController = Navigation.findNavController(binding.root)
          val action = PetsToAdoptFragmentDirections.actionSelectOwner(pet.id)
          navController.navigate(action)
        }
        buttonRemove.setOnClickListener {
          pets.removeAt(adapterPosition)
          notifyItemRemoved(adapterPosition)
          removePet?.invoke(pet.id)
        }
      }
    }
  }
}
