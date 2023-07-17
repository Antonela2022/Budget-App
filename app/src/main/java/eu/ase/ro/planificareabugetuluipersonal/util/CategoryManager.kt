package eu.ase.ro.planificareabugetuluipersonal.util

import android.content.Context

object CategoryManager {
    private const val PREF_NAME = "CategoryPreferences"
    private const val KEY_CATEGORIES = "categories"

    fun saveCategories(categories: List<String>, userId: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(getPreferencesName(userId), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(KEY_CATEGORIES, categories.toSet())
        editor.apply()
    }

    fun getCategories(userId: String, context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences(getPreferencesName(userId), Context.MODE_PRIVATE)
        val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, emptySet())
        return categorySet?.toList() ?: emptyList()
    }

    private fun getPreferencesName(userId: String): String {
        return "$PREF_NAME-$userId"
    }
}


