package com.shoppinglist.app.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.shoppinglist.app.adapters.ImageAdapter
import com.shoppinglist.app.adapters.ShoppingItemAdapter
import com.shoppinglist.app.repositories.FakeShoppingRepositoryAndroidTest
import com.shoppinglist.app.ui.viewmodel.ShoppingViewModel
import javax.inject.Inject

class TestShoppingFragmentFactory @Inject constructor(
    private val imageAdapter: ImageAdapter,
    private val glide :RequestManager,
    private val shoppingItemAdapter: ShoppingItemAdapter
):FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            ImagePickFragment::class.java.name -> ImagePickFragment(imageAdapter)
            AddShoppingItemFragment::class.java.name -> AddShoppingItemFragment(glide)
            ShoppingFragment::class.java.name -> ShoppingFragment(
                shoppingItemAdapter,
                ShoppingViewModel(FakeShoppingRepositoryAndroidTest())
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}