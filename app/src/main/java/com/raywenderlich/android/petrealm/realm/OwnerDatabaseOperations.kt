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

package com.raywenderlich.android.petrealm.realm

import com.raywenderlich.android.petrealm.owners.models.Owner
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class OwnerDatabaseOperations @Inject constructor(private val config: RealmConfiguration) {

  suspend fun insertOwner(name: String, image: Int?) {
    // 1.
    val realm = Realm.getInstance(config)
    // 2.
    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      // 3.
      val owner = OwnerRealm(name = name, image = image)
      // 4.
      realmTransaction.insert(owner)
    }
  }

  suspend fun retrieveOwners(): List<Owner> {
    // 1.
    val realm = Realm.getInstance(config)
    val owners = mutableListOf<Owner>()

    // 2.
    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      owners.addAll(realmTransaction
        // 3.
        .where(OwnerRealm::class.java)
        // 4.
        .findAll()
        .sort("name", Sort.ASCENDING)

        // 5.
        .map { owner ->

          Owner(
            name = owner.name,
            image = owner.image,
            id = owner.id,
            numberOfPets = getPetCount(realmTransaction, owner.id)
          )
        }
      )
    }
    return owners
  }

  suspend fun updatePets(petId: String, ownerId: String) {
    val realm = Realm.getInstance(config)

    // 1.
    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      // 2.
      val pet = realmTransaction
        .where(PetRealm::class.java)
        .equalTo("id", petId)
        .findFirst()

      // 3.
      val owner = realmTransaction
        .where(OwnerRealm::class.java)
        .equalTo("id", ownerId)
        .findFirst()

      // 4.
      pet?.isAdopted = true
      // 5.
      owner?.pets?.add(pet)
    }
  }

  suspend fun removeOwner(ownerId: String) {
    val realm = Realm.getInstance(config)

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      // 1.
      val ownerToRemove = realmTransaction
        .where(OwnerRealm::class.java)
        .equalTo("id", ownerId)
        .findFirst()
      // 2.
      ownerToRemove?.pets?.deleteAllFromRealm()
      // 3.
      ownerToRemove?.deleteFromRealm()
    }
  }


  private fun getPetCount(realm: Realm, ownerId: String): Long {
    // 1.
    return realm.where(PetRealm::class.java)
      // 2.
      .equalTo("owner.id", ownerId)
      // 3.
      .count()
  }

}