package com.shoppinglist.app.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shoppinglist.app.R
import com.shoppinglist.app.data.local.ShoppingDao
import com.shoppinglist.app.data.local.ShoppingItemDatabase
import com.shoppinglist.app.data.remote.PixabayAPI
import com.shoppinglist.app.repositories.DefaultShoppingRepository
import com.shoppinglist.app.repositories.ShoppingRepository
import com.shoppinglist.app.utils.Constants.BASE_URL
import com.shoppinglist.app.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideShoppingItemDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, ShoppingItemDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDefaultShoppingRepository(
        dao:ShoppingDao,
        api:PixabayAPI
    )= DefaultShoppingRepository(dao,api) as ShoppingRepository

    @Singleton
    @Provides
    fun providesShoppingDao(database: ShoppingItemDatabase) = database.shoppingDao()

    @Singleton
    @Provides
    fun providesPixabayApi():PixabayAPI{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PixabayAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
    )
} 