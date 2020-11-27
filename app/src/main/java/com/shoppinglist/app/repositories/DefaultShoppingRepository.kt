package com.shoppinglist.app.repositories

import androidx.lifecycle.LiveData
import com.shoppinglist.app.data.local.ShoppingDao
import com.shoppinglist.app.data.local.ShoppingItem
import com.shoppinglist.app.data.remote.PixabayAPI
import com.shoppinglist.app.data.remote.responses.ImageResponse
import com.shoppinglist.app.utils.Resource
import javax.inject.Inject

class DefaultShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val pixabayAPI: PixabayAPI
) : ShoppingRepository {
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return shoppingDao.observeAllShoppingItems()
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return shoppingDao.observeTotalPrice()
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try {
            val response = pixabayAPI.searchForImage(imageQuery)
            if (response.isSuccessful) {
                response?.body().let {
                    return@let Resource.success(it)
                } ?: Resource.error("Unknown error occured", null)
            } else {
                Resource.error("Unknown error ocuured", null)
            }
        } catch (e: Exception) {
            Resource.error("Could not reach the server.Check your internet connection", null)
        }
    }

}