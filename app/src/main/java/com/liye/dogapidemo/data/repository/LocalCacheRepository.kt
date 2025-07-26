package com.liye.dogapidemo.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.liye.dogapidemo.data.model.DogBreed
import org.json.JSONArray
import org.json.JSONObject

class LocalCacheRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("dog_breeds_cache", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BREEDS = "breeds"
        private const val KEY_CACHE_TIME = "cache_time"
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000 // 24小时
    }

    fun getCachedBreeds(): List<DogBreed>? {
        val breedsJson = sharedPreferences.getString(KEY_BREEDS, null) ?: return null
        val cacheTime = sharedPreferences.getLong(KEY_CACHE_TIME, 0)

        // 检查缓存是否过期
        if (System.currentTimeMillis() - cacheTime > CACHE_EXPIRATION_TIME) {
            clearCache()
            return null
        }

        return try {
            // 使用自定义方法反序列化
            deserializeBreeds(breedsJson)
        } catch (e: Exception) {
            clearCache()
            null
        }
    }

    fun saveBreeds(breeds: List<DogBreed>) {
        // 使用自定义方法序列化
        val breedsJson = serializeBreeds(breeds)
        sharedPreferences.edit()
            .putString(KEY_BREEDS, breedsJson)
            .putLong(KEY_CACHE_TIME, System.currentTimeMillis())
            .apply()
    }

    fun clearCache() {
        sharedPreferences.edit()
            .remove(KEY_BREEDS)
            .remove(KEY_CACHE_TIME)
            .apply()
    }

    private fun serializeBreeds(breeds: List<DogBreed>): String {
        val jsonArray = JSONArray()
        for (breed in breeds) {
            val jsonObject = JSONObject()
            jsonObject.put("name", breed.name)
            val subBreedsArray = JSONArray()
            for (subBreed in breed.subBreeds) {
                subBreedsArray.put(subBreed)
            }
            jsonObject.put("subBreeds", subBreedsArray)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun deserializeBreeds(json: String): List<DogBreed> {
        val breeds = mutableListOf<DogBreed>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val subBreedsArray = jsonObject.getJSONArray("subBreeds")
                val subBreeds = mutableListOf<String>()
                for (j in 0 until subBreedsArray.length()) {
                    subBreeds.add(subBreedsArray.getString(j))
                }
                breeds.add(DogBreed(name, subBreeds))
            }
        } catch (e: Exception) {
            // 解析失败，返回空列表
            return emptyList()
        }
        return breeds
    }
}