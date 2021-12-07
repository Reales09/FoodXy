package com.example.foodxy.ui.home.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.core.hide
import com.example.foodxy.core.show
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.ActivityStoreBinding

import com.example.foodxy.ui.home.store.products.OnProductListener
import com.example.foodxy.ui.home.store.products.ProductAdapter
import com.example.foodxy.ui.home.store.cart.CartFragment
import com.example.foodxy.ui.home.store.detail.DetailFragment
import com.example.foodxy.ui.home.store.order.OrderActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging

class StoreActivity : AppCompatActivity(), OnProductListener, MainAux {

    private lateinit var binding: ActivityStoreBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var adapter: ProductAdapter
    private lateinit var  firestoreListener: ListenerRegistration
    private var productSelected: Product? = null
    private val productCarList = mutableListOf<Product>()


    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val response = IdpResponse.fromResultIntent(it.data)

        if (it.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                val token = preferences.getString(Constants.PROP_TOKEN,null)

                token?.let {
                    val db = FirebaseFirestore.getInstance()
                    val tokenMap = hashMapOf(Pair(Constants.PROP_TOKEN, token))

                    db.collection(Constants.COLL_USERS)
                        .document(user.uid)
                        .collection(Constants.COLL_TOKENS)
                        .add(tokenMap)
                        .addOnSuccessListener {
                            Log.i("registered token", token)
                            preferences.edit {
                                putString(Constants.PROP_TOKEN,null)
                                    .apply()
                            }
                        }

                        .addOnFailureListener {
                            Log.i("no register token", token)
                        }
                }

            }
        } else{
            if (response == null){
                Toast.makeText(this, "Hasta pronto", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                response.error?.let {
                    if (it.errorCode == ErrorCodes.NO_NETWORK){
                        Toast.makeText(this, "Sin Red", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Codigod de error: ${it.errorCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


        configAuth()
        configRecyclerView()
        configButtons()

        //FCM
       /* FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val token = task.result
                Log.i("get token", token.toString())
            }else{
                Log.i("get token fail", task.exception.toString())
            }
        }

        */

    }


    private fun configAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->

            if (auth.currentUser != null) {
               // supportActionBar?.title = auth.currentUser?.displayName
                updateTitle(auth.currentUser!!)
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
            layoutManager = GridLayoutManager(this@StoreActivity,3,
                GridLayoutManager.HORIZONTAL, false)
            adapter=this@StoreActivity.adapter
        }
        /* (1..20).forEach {
             val product = Product(it.toString(), "Producto $it", "Este producto es el $it", "", it, it * 1.1 )
             adapter.add(product)
         }

         */
    }
    private fun configButtons(){

        binding.btnViewCart.setOnClickListener {

            val fragment = CartFragment()
            supportFragmentManager?.let { it1 -> fragment.show(it1.beginTransaction(), CartFragment::class.java.simpleName) }
        }

    }


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

            R.id.action_order_history -> startActivity(Intent(this,OrderActivity::class.java))


        }

        return super.onOptionsItemSelected(item)
    }




    private fun configFirestoreRealtime() {

        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("products")

        firestoreListener = productRef.addSnapshotListener { snapshots, error ->

            if (error != null){

                Toast.makeText(this, "Error al consultar datos", Toast.LENGTH_SHORT).show()

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

        val index = productCarList.indexOf(product)
        if (index != -1){
            productSelected = productCarList[index]
        }else{
            productSelected = product
        }



        val fragment = DetailFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.containerMain,fragment)
            .addToBackStack(null)
            .commit()

        showButton(false)

    }


    override fun getProductCart(): MutableList<Product> = productCarList

    override fun getProductSelected(): Product? = productSelected

    override fun showButton(isVisible: Boolean) {
        binding.btnViewCart.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun addProductToCart(product: Product) {

        val index = productCarList.indexOf(product)
        if (index != -1){
            productCarList.set(index,product)
        }else{
            productCarList.add(product)
        }

        updateTotal()
    }

    override fun updateTotal() {
        var total = 0.0
        productCarList.forEach {product ->
            total += product.totalPrice()
        }

        if (total == 0.0){

            binding.tvTotal.text = getString(R.string.product_empty_cart)
        }else{
            binding.tvTotal.text = getString(R.string.product_full_cart, total)
        }

    }

    override fun clearCart() {
        productCarList.clear()
    }

    override fun updateTitle(user: FirebaseUser) {
        supportActionBar?.title = user.displayName
    }
}