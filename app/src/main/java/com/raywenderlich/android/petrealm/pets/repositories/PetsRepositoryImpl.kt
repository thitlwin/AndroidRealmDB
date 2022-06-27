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

package com.raywenderlich.android.petrealm.pets.repositories

import com.raywenderlich.android.petrealm.realm.PetDatabaseOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PetsRepositoryImpl @Inject constructor(
    private val databaseOperations: PetDatabaseOperations
) : PetsRepository {

  override fun addPet(
      name: String,
      age: Int,
      type: String,
      image: Int?
  ): Flow<PetDataStatus> = flow {
    emit(PetDataStatus.Loading)
    databaseOperations.insertPet(name, age, type, image)
    emit(PetDataStatus.Added)
  }.flowOn(Dispatchers.IO)

  override fun getPetsToAdopt(): Flow<PetDataStatus> = flow {
    emit(PetDataStatus.Loading)
    val petsToAdopt = databaseOperations.retrievePetsToAdopt()
    emit(PetDataStatus.Result(petsToAdopt))
  }.flowOn(Dispatchers.IO)

  override fun getAdoptedPets(): Flow<PetDataStatus> = flow {
    emit(PetDataStatus.Loading)
    val adoptedPets = databaseOperations.retrieveAdoptedPets()
    emit(PetDataStatus.Result(adoptedPets))
  }.flowOn(Dispatchers.IO)

  override fun deletePet(petId: String): Flow<PetDataStatus> = flow {
    emit(PetDataStatus.Loading)
    databaseOperations.removePet(petId)
    emit(PetDataStatus.Deleted)
  }.flowOn(Dispatchers.IO)

  override fun filterPets(petType: String): Flow<PetDataStatus> = flow {
    emit(PetDataStatus.Loading)
    val filteredPets = databaseOperations.retrieveFilteredPets(petType)
    emit(PetDataStatus.Result(filteredPets))
  }.flowOn(Dispatchers.IO)
}