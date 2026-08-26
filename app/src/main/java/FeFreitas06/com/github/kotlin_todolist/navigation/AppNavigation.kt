package FeFreitas06.com.github.kotlin_todolist.navigation

import FeFreitas06.com.github.kotlin_todolist.ui.FormularioTarefaScreen
import FeFreitas06.com.github.kotlin_todolist.ui.ListaTarefasScreen
import FeFreitas06.com.github.kotlin_todolist.viewmodel.TarefaViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation(viewModel: TarefaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            ListaTarefasScreen(
                viewModel = viewModel,
                onNovaTarefa = { navController.navigate("formulario/0") },
                onEditarTarefa = { id -> navController.navigate("formulario/$id") }
            )
        }
        composable("formulario/{tarefaId}") { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId")?.toInt() ?: 0
            FormularioTarefaScreen(
                viewModel = viewModel,
                tarefaId = tarefaId,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}