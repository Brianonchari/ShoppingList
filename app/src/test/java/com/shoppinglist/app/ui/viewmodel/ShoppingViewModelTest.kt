package com.shoppinglist.app.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.shoppinglist.app.repositories.FakeShoppingRepository
import com.shoppinglist.app.ui.MainCoroutineRule
import com.shoppinglist.app.ui.getOrAwaitValueTest
import com.shoppinglist.app.utils.Constants
import com.shoppinglist.app.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, returns error`() {
        viewModel.insertShoppingItem("name", "", "10.0")
        //get value of resource we are emmiting
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        //Assert that the value emmited is an error resource
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, returns error`() {
        val string = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem(string, "5", "10.0")
        //get value of resource we are emmiting
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        //Assert that the value emmited is an error resource
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, returns error`() {
        val maxPriceLength = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem("name", "5", maxPriceLength)
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() {
        viewModel.insertShoppingItem("name", "999999999999999999999999999999", "10")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }


    @Test
    fun `insert shopping item with valid input, returns success`() {
        viewModel.insertShoppingItem("name", "9", "10")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}