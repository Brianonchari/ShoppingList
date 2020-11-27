package com.shoppinglist.app.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shoppinglist.app.launchFragmentInHiltContainer
import com.shoppinglist.app.ui.fragments.ShoppingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class ShoppingDaoTest {
    @get:Rule
    var hiltRule =HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Inject
    @Named("test_db")
    lateinit var database: ShoppingItemDatabase
    private lateinit var  shoppingDao: ShoppingDao

    @Before
    fun setup(){
        hiltRule.inject()
        shoppingDao = database.shoppingDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun instertShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1,1f, "url", id=1)
        shoppingDao.insertShoppingItem(shoppingItem)
        val allshoppingItems = shoppingDao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(allshoppingItems).contains(shoppingItem)
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1 ,1f, "url" ,id = 1)
        shoppingDao.insertShoppingItem(shoppingItem)
        shoppingDao.deleteShoppingItem(shoppingItem)
        val allShoppingItems = shoppingDao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val shoppingItem1 = ShoppingItem("name", 2 ,10f, "url" ,id = 1)
        val shoppingItem2 = ShoppingItem("name", 15 ,5.5f, "url" ,id = 2)
        val shoppingItem3 = ShoppingItem("name", 10 ,100f, "url" ,id = 3)
        shoppingDao.insertShoppingItem(shoppingItem1)
        shoppingDao.insertShoppingItem(shoppingItem2)
        shoppingDao.insertShoppingItem(shoppingItem3)
        val totalPriceSum = shoppingDao.observeTotalPrice().getOrAwaitValue()
        assertThat(totalPriceSum).isEqualTo(2*10f + 15*5.5f + 10*100f)
    }

    @Test
    fun testLaunchFragmentInHiltContainer(){
        launchFragmentInHiltContainer<ShoppingFragment> {

        }
    }
}