package com.shoppinglist.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shoppinglist.app.R
import com.shoppinglist.app.adapters.ShoppingItemAdapter
import com.shoppinglist.app.ui.viewmodel.ShoppingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_shopping.*
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingFragment @Inject constructor(
    private val shoppingItemAdapter: ShoppingItemAdapter
) : Fragment(R.layout.fragment_shopping) {
    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
        subscribeToObservers()
        setupRecyclerView()
        fabAddShoppingItem.setOnClickListener {
            findNavController().navigate(ShoppingFragmentDirections.actionShoppingFragmentToAddShoppingItemFragment())
        }
    }

    private val itemTouchCallBack = object : ItemTouchHelper.SimpleCallback(
        0, LEFT or RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.layoutPosition
            val item = shoppingItemAdapter.shoppingItems[pos]
            viewModel.deleteShoppingItem(item)

            Snackbar.make(requireView(), "Successfuly deleted", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("UNDO") {
                        viewModel.insertShoppingItemIntoDb(item)
                    }
                    show()
                }
        }
    }

    private fun subscribeToObservers(){
        viewModel.shoppingItems.observe(viewLifecycleOwner, Observer {
            shoppingItemAdapter.shoppingItems = it
        })

        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            val price = it ?: 0
            val priceText = "Total Price: $price Kes "
            tvShoppingItemPrice.text = priceText
        })
    }

    private fun setupRecyclerView(){
        rvShoppingItems.apply {
            adapter = shoppingItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallBack).attachToRecyclerView(this)
        }
    }
}