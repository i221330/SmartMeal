package com.example.smartmeal.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Using Mac's IP address for physical phone connectivity
    // Phone and Mac must be on the same Wi-Fi network
    const val BASE_URL = "http://192.168.1.4/smartmeal/backend/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Create Gson instance that serializes null values
    private val gson = GsonBuilder()
        .serializeNulls()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val userService: UserApiService = retrofit.create(UserApiService::class.java)
    val recipeService: RecipeApiService = retrofit.create(RecipeApiService::class.java)
    val pantryService: PantryApiService = retrofit.create(PantryApiService::class.java)
    val shoppingService: ShoppingApiService = retrofit.create(ShoppingApiService::class.java)
    val mealPlanService: MealPlanApiService = retrofit.create(MealPlanApiService::class.java)
}

