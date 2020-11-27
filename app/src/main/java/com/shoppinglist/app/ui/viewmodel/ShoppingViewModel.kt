package com.shoppinglist.app.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.app.data.local.ShoppingItem
import com.shoppinglist.app.data.remote.responses.ImageResponse
import com.shoppinglist.app.repositories.ShoppingRepository
import com.shoppinglist.app.utils.Constants
import com.shoppinglist.app.utils.Event
import com.shoppinglist.app.utils.Resource
import kotlinx.coroutines.launch


class ShoppingViewModel @ViewModelInject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {
    val shoppingItems = repository.observeAllShoppingItems()
    val totalPrice = repository.observeTotalPrice()
    private val _images = MutableLiveData<Event<Resource<ImageResponse>>>()
    val images: LiveData<Event<Resource<ImageResponse>>> = _images

    private val _currentImgUrl = MutableLiveData<String>()
    val currentImgUrl: LiveData<String> = _currentImgUrl

    private val _insertShoppingItemStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShoppingItemStatus: LiveData<Event<Resource<ShoppingItem>>> =
        _insertShoppingItemStatus

    fun setCurImageUrl(url: String) {
        _currentImgUrl.postValue(url)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    /**Validate user input**/
    fun insertShoppingItem(name: String, amount: String, price: String) {
        if (name.isEmpty() || amount.isEmpty() || price.isEmpty()) {
            _insertShoppingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Fields must not be empty",
                        null
                    )
                )
            )
            return
        }

        if (name.length > Constants.MAX_NAME_LENGTH) {
            _insertShoppingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Name of item must not exceed ${Constants.MAX_NAME_LENGTH} characters",
                        null
                    )
                )
            )
            return
        }

        if (price.length > Constants.MAX_PRICE_LENGTH) {
            _insertShoppingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Price of item must not exceed ${Constants.MAX_PRICE_LENGTH} ",
                        null
                    )
                )
            )
            return
        }

        val amount = try {
            amount.toInt()
        } catch (e: Exception) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Please enter valid amount", null)))
            return
        }

        val shoppingItem = ShoppingItem(name, amount, price.toFloat(),_currentImgUrl.value?: "")
        insertShoppingItemIntoDb(shoppingItem)
        setCurImageUrl("")
        _insertShoppingItemStatus.postValue(Event(Resource.success(shoppingItem)))
    }

    fun searchForImage(imageQuery: String) {
        if(imageQuery.isEmpty()){
            return
        }
        _images.value = Event(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = Event(response)
        }
    }
}
