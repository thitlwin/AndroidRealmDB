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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raywenderlich.android.petrealm.R
import com.raywenderlich.android.petrealm.common.adapters.ImageAdapter
import com.raywenderlich.android.petrealm.common.utils.addTracker
import com.raywenderlich.android.petrealm.common.viewmodels.SharedViewModel
import com.raywenderlich.android.petrealm.databinding.FragmentAddPetBinding
import com.raywenderlich.android.petrealm.pets.repositories.PetDataStatus
import com.raywenderlich.android.petrealm.pets.utils.getPetImages
import com.raywenderlich.android.petrealm.pets.viewmodels.AddPetViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class AddPetFragment : BottomSheetDialogFragment() {

  @Inject
  lateinit var imagesAdapter: ImageAdapter

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: AddPetViewModel by viewModels { viewModelFactory }
  private val sharedViewModel: SharedViewModel by activityViewModels { viewModelFactory }
  private var binding: FragmentAddPetBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidSupportInjection.inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentAddPetBinding.inflate(layoutInflater, container, false)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupSpinner()
    setupRecyclerView()
    setupSelectionTracker()
    setupButton()

    viewModel.petDataStatus.observe(viewLifecycleOwner) { status ->
      when (status) {
        PetDataStatus.Added -> {
          sharedViewModel.reload()
          dismiss()
        }
      }
    }
  }

  private fun setupRecyclerView() {
    binding?.apply {
      with(petImages) {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imagesAdapter.addImages(getPetImages())
        adapter = imagesAdapter
      }
    }
  }

  private fun setupSelectionTracker() {
    binding?.apply {
      imagesAdapter.addTracker(petImages) { selectedImage ->
        viewModel.setSelectedImage(selectedImage)
      }
    }
  }

  private fun setupButton() {
    binding?.apply {
      buttonCreate.setOnClickListener {
        textInputLayoutAge.error = ""
        textInputLayoutName.error = ""

        if (viewModel.isValid(editTextName.text.toString(), editTextAge.text.toString())) {
          viewModel.addPet(editTextName.text.toString(), editTextAge.text.toString())
        } else {
          textInputLayoutName.error = getString(R.string.name_error)
          textInputLayoutAge.error = getString(R.string.age_error)
        }
      }
    }
  }

  private fun setupSpinner() {
    binding?.apply {
      ArrayAdapter.createFromResource(requireContext(), R.array.pet_types,
          android.R.layout.simple_spinner_item)
          .also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
          }
      spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val petType = requireContext().resources.getStringArray(R.array.pet_types)[position]
          viewModel.setPetType(petType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
      }
    }
  }
}