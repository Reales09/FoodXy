package com.example.foodxy

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        //Consultar información


        db.collection("ciudades").document("NY").addSnapshotListener { value, error ->

            value?.let { document ->
                if (document != null) {

                    val ciudad = document.toObject(Ciudad::class.java)

                    Log.d("Firebase", "DocumentSnapshot data: ${document.data}")

                    Log.d("Firebase", "Population: ${ciudad?.population}")
                    Log.d("Firebase", "color: ${ciudad?.color}")
                    Log.d("Firebase", "postal code: ${ciudad?.pc}")

                } else {
                    Log.d("Firebase", "No such document")
                }
            }

            //Ingresar información

            db.collection("ciudades").document("LA").set(Ciudad(3000000, "red"))
                .addOnSuccessListener {

                    Log.d("Firebase", "Se guardo la ciudad correctamente")
                }.addOnFailureListener { exception ->

                Log.d("FirebaseError", "Error ingresar dato", exception)
            }

        }
    }
}

data class Ciudad (val population: Int=0, val color: String="",val pc: Int=0)