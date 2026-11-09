package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.DoctoralRepository
import com.example.ui.DoctoralViewModel
import com.example.ui.MainApp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DoctoralViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = DoctoralRepository(
            dao = database.applicationDao(),
            scope = lifecycleScope
        )

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DoctoralViewModel(repository) as T
                }
            }
        )[DoctoralViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    color = BackgroundLight,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainApp(viewModel = viewModel)
                }
            }
        }
    }
}
