package com.raywenderlich.android.petrealm.realm

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

// 1.
open class PetRealm(
    @PrimaryKey // 2.
    var id: String = ObjectId().toHexString(), // 3.
    @Required // 4.
    var name: String = "",
    @Required
    var petType: String = "",
    var age: Int = 0,
    var isAdopted: Boolean = false,
    @DrawableRes
    var image: Int? = null, // 5.
    @LinkingObjects("pets") // 1.
    val owner: RealmResults<OwnerRealm>? = null // 2.
): RealmObject() // 6.
