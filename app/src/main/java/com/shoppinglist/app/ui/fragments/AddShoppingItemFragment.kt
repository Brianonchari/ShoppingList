package com.shoppinglist.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.shoppinglist.app.R
import com.shoppinglist.app.ui.viewmodel.ShoppingViewModel
import com.shoppinglist.app.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_shopping_item.*
import kotlinx.android.synthetic.main.fragment_shopping.*
import kotlinx.android.synthetic.main.fragment_shopping.rootLayout
import javax.inject.Inject

@AndroidEntryPoint
class AddShoppingItemFragment @Inject constructor(
    private val glide :RequestManager
): Fragment(R.layout.fragment_add_shopping_item){
    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
        subscribeToObservers()
        btnAddShoppingItem.setOnClickListener {
            viewModel.insertShoppingItem(
                etShoppingItemName.text.toString(),
                etShoppingItemAmount.text.toString(),
                etShoppingItemPrice.text.toString()
            )
        }
        ivShoppingImage.setOnClickListener {
            findNavController().navigate(AddShoppingItemFragmentDirections.actionAddShoppingItemFragmentToImagePickFragment())
        }

        val callback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                viewModel.setCurImageUrl("")
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun subscribeToObservers(){
        viewModel.currentImgUrl.observe(viewLifecycleOwner, Observer {
            glide.load(it).into(ivShoppingImage)
        })

        viewModel.insertShoppingItemStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when(result.status){
                    Status.SUCCESS ->{
                        Snackbar.make(
                            requireView(),
                             "Added Shopping Item",
                        Snackbar.LENGTH_LONG
                        ).show()
                        findNavController().popBackStack()
                    }
                    Status.LOADING ->{
                        /* NO-OP */
                    }
                    Status.ERROR ->{
                        Snackbar.make(
                            requireActivity().rootLayout,
                            result.message?:"Unkown error",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }
}