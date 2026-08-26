package FeFreitas06.com.github.kotlin_todolist

import FeFreitas06.com.github.kotlin_todolist.navigation.AppNavigation
import FeFreitas06.com.github.kotlin_todolist.ui.theme.KotlintoDoListTheme
import FeFreitas06.com.github.kotlin_todolist.viewmodel.TarefaViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlintoDoListTheme {
                val viewModel: TarefaViewModel = viewModel(
                    factory = TarefaViewModel.factory(applicationContext)
                )
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
