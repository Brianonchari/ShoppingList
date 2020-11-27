package com.shoppinglist.app.repositories

import androidx.lifecycle.LiveData
import com.shoppinglist.app.data.local.ShoppingItem
import com.shoppinglist.app.data.remote.responses.ImageResponse
import com.shoppinglist.app.utils.Resource

interface ShoppingRepository {
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}