package com.example.listycity

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Keeps the list of cities in sync with the "cities" collection in Firestore.
 * The UI only reads [cities]; all changes go to Firestore, and the snapshot
 * listener rebuilds the local list whenever the database changes.
 */
class CityRepository {

    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")

    private val _cities = mutableStateListOf<City>()

    val cities: List<City>
        get() = _cities

    init {
        // Runs once now, then again every time the collection changes.
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("CityRepository", "Listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _cities.clear()
                for (doc in snapshot.documents) {
                    doc.toObject(City::class.java)?.let { _cities.add(it) }
                }
            }
        }
    }

    /** Saves a new city to Firestore. */
    fun addCity(city: City) {
        citiesRef.add(city)
    }

    /** Finds the matching city document(s) in Firestore and replaces them. */
    fun updateCity(oldCity: City, updatedCity: City) {
        citiesRef
            .whereEqualTo("name", oldCity.name)
            .whereEqualTo("province", oldCity.province)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.set(updatedCity)
                }
            }
    }

    /** Participation exercise: deletes the matching city document(s) from Firestore. */
    fun deleteCity(city: City) {
        citiesRef
            .whereEqualTo("name", city.name)
            .whereEqualTo("province", city.province)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.delete()
                }
            }
    }
}