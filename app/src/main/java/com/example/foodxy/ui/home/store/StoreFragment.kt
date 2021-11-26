package com.example.foodxy.ui.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.FragmentStoreBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class storeFragment : Fragment(R.layout.fragment_store), OnProductListener {
    
    private lateinit var binding: FragmentStoreBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private lateinit var  firestoreListener: ListenerRegistration

    private lateinit var adapter: ProductAdapter





    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val response = IdpResponse.fromResultIntent(it.data)

        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
            }
        } else{
            if (response == null){
                Toast.makeText(requireContext(), "Hasta pronto", Toast.LENGTH_SHORT).show()
               // finish()
            }else{
                response.error?.let {
                    if (it.errorCode == ErrorCodes.NO_NETWORK){
                        Toast.makeText(requireContext(), "Sin Red", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Codigod de error: ${it.errorCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStoreBinding.bind(view)

        configAuth()
        configRecyclerView()
        configButtons()
    }


    private fun configAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->

            if (auth.currentUser != null) {
                //supportActionBar?.title = auth.currentUser?.displayName
                binding.llprogress.visibility = View.GONE
                binding.nsvProducts.visibility = View.VISIBLE

            } else {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                resultLauncher.launch(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)
        configFirestoreRealtime()
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
        firestoreListener.remove()
    }


    private fun configRecyclerView() {
        adapter = ProductAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext().applicationContext,3,
                GridLayoutManager.HORIZONTAL, false)
            adapter=this@storeFragment.adapter
        }

    }

    private fun configButtons(){

        binding.btnViewCart.setOnClickListener {

            val fragment = CartFragment()
            fragmentManager?.let { it1 -> fragment.show(it1.beginTransaction(), CartFragment::class.java.simpleName) }
        }

    }



    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_sign_out -> {
                AuthUI.getInstance().signOut(this)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sesión teminada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.nsvProducts.visibility = View.GONE
                            binding.llprogress.visibility = View.VISIBLE

                        }else{
                            Toast.makeText(this, "No se pudo cerrar la sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        return super.onOptionsItemSelected(item)
    }

*/
    private fun configFirestoreRealtime() {

        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection(Constants.COLL_PRODUCTS)

        firestoreListener = productRef.addSnapshotListener { snapshots, error ->

            if (error != null){

                Toast.makeText(requireContext(), "Error al consultar datos", Toast.LENGTH_SHORT).show()

                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges){
                val product = snapshot.document.toObject(Product::class.java)
                product.id = snapshot.document.id

                when(snapshot.type){
                    DocumentChange.Type.ADDED -> adapter.add(product)
                    DocumentChange.Type.MODIFIED -> adapter.update(product)
                    DocumentChange.Type.REMOVED -> adapter.delete(product)

                }
            }
        }

    }

    override fun onClick(product: Product) {

    }
}