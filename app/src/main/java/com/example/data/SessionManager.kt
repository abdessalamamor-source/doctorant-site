package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "doctoral_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("authenticated_user_id")
        private val KEY_TEST_SEED_DONE = booleanPreferencesKey("test_seed_initialized")
    }

    suspend fun saveSessionUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
        }
    }

    suspend fun getSessionUserId(): String? {
        return context.dataStore.data.map { prefs -> prefs[KEY_USER_ID] }.first()
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
        }
    }

    suspend fun isTestSeedDone(): Boolean {
        return context.dataStore.data.map { prefs -> prefs[KEY_TEST_SEED_DONE] ?: false }.first()
    }

    suspend fun setTestSeedDone(done: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TEST_SEED_DONE] = done
        }
    }
}
