package com.example.foodxy.ui.home.store.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodxy.core.Constants
import com.example.foodxy.data.model.Order
import com.example.foodxy.databinding.FragmentTrackBinding
import com.example.foodxy.ui.home.store.order.OrderAux
import com.google.firebase.firestore.FirebaseFirestore

class TrackFragment : Fragment() {

    private var binding: FragmentTrackBinding? = null

    private var order: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTrackBinding.inflate(inflater,container,false)

        binding?.let {
            return  it.root
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrder()
    }

    private fun getOrder() {

        order = (context as? OrderAux)?.getOrderSelected()

        order?.let {
            updateUI(it)

            getOrderInRealTime(it.id)
        }

    }



    private fun updateUI(order: Order) {

        binding?.let {
            it.progressBar.progress = order.status * (100/3) - 15

            it.cbOrdered.isChecked = order.status > 0
            it.cbPreparing.isChecked = order.status > 1
            it.cbSent.isChecked = order.status > 2
            it.cbDelivered.isChecked = order.status > 3
        }

    }

    private fun getOrderInRealTime(orderId: String) {

        val db = FirebaseFirestore.getInstance()

        val orderRef = db.collection(Constants.COLL_REQUEST).document(orderId)
        orderRef.addSnapshotListener { snapshot, error -> 
            
            if (error !=null){

                Toast.makeText(activity, "Error al consultar orden. ", Toast.LENGTH_SHORT).show()

                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()){

                val order = snapshot.toObject(Order::class.java)
                order?.let {
                    it.id = snapshot.id

                    updateUI(it)
                }

            }
            
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }

}