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

package com.raywenderlich.android.petrealm.pets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.petrealm.R
import com.raywenderlich.android.petrealm.databinding.FragmentAdoptedPetsBinding
import com.raywenderlich.android.petrealm.owners.repository.OwnerDataStatus
import com.raywenderlich.android.petrealm.pets.adapters.PetAdapter
import com.raywenderlich.android.petrealm.pets.repositories.PetDataStatus
import com.raywenderlich.android.petrealm.pets.viewmodels.AdoptedPetsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class AdoptedPetsFragment : Fragment() {

  private var binding: FragmentAdoptedPetsBinding? = null

  @Inject
  lateinit var petsAdapter: PetAdapter

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: AdoptedPetsViewModel by viewModels { viewModelFactory }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidSupportInjection.inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentAdoptedPetsBinding.inflate(layoutInflater, container, false)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding?.apply {
      adoptedPetsList.layoutManager = LinearLayoutManager(requireContext())
      adoptedPetsList.adapter = petsAdapter
    }

    viewModel.petDataStatus.observe(viewLifecycleOwner) { status ->
      binding?.progress?.isVisible = false
      when (status) {
        PetDataStatus.Added -> createSnackbar(R.string.pet_added)
        PetDataStatus.Deleted -> createSnackbar(R.string.pet_deleted)
        PetDataStatus.Loading -> binding?.progress?.isVisible = true
        is PetDataStatus.Result -> petsAdapter.addItems(status.petList)
      }
    }
  }

  override fun onResume() {
    super.onResume()

    viewModel.getPetsToAdopt()
  }

  private fun createSnackbar(@StringRes message: Int) {
    binding?.apply {
      Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }
  }
}