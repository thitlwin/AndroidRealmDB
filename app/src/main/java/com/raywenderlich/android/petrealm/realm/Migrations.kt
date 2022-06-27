package com.raywenderlich.android.petrealm.realm

import io.realm.RealmMigration

// 1.
val migration = RealmMigration { realm, oldVersion, newVersion ->
    // 2.
    if (oldVersion == 1L) {
        // 3.
        val ownerSchema = realm.schema.get("OwnerRealm")
        val petSchema = realm.schema.get("PetRealm")

        // 4.
        petSchema?.let {
            ownerSchema?.addRealmListField("pets", it)
        }
    }
}
