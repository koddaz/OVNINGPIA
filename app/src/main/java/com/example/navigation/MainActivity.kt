package com.example.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StartScreen(

                    )
                }
            }
        }
    }
}


class AViewModel : ViewModel() {
    private val _aNumber = MutableStateFlow(1)

    private val _colorInfoList = MutableStateFlow(
        listOf(
            ColorData(Color.Gray, "PLUS"),
            ColorData(Color.Red, "RÖD"),
            ColorData(Color.Green, "GRÖN")
        )
    )
    val colorInfoList: StateFlow<List<ColorData>> = _colorInfoList

    val aNumber: StateFlow<Int> = _aNumber

    fun addOne() {
        _aNumber.value++
    }

    fun navigateWithColors(index: Int, navController: NavHostController) {
        val colorData = _colorInfoList.value.getOrNull(index)
        colorData?.let {
            val colorInt = it.color.toArgb()
            navController.navigate("${Routes.COLOR}/${colorInt}/${it.title}")
        }
    }
}

object Routes {
    const val MAIN = "main"
    const val COLOR = "color"
}

data class ColorData(
    val color: Color,
    val title: String
)

@Composable
fun StartScreen() {
    val navController = rememberNavController()
    val aViewModel: AViewModel = viewModel()



    val number by aViewModel.aNumber.collectAsState()
    val add = {
        aViewModel.addOne()
    }

    
    NavHost(navController = navController, startDestination = Routes.MAIN, modifier = Modifier.fillMaxSize()) {
        composable(Routes.MAIN) {
            MainScreen(navigate = { index -> aViewModel.navigateWithColors(index, navController) }, number = number, add = add)
        }
        composable("${Routes.COLOR}/{color}/{title}") { backStackEntry ->
            val colorArg = backStackEntry.arguments?.getString("color") ?: Color.Gray.toArgb().toString()
            val color = try {
                Color(colorArg.toInt())
            } catch (e: NumberFormatException) {
                Color.Gray
            }
            val title = backStackEntry.arguments?.getString("title") ?: "TITLE"
            ColorScreen(navController = navController, color = color, title = title, number = number)
        }
    }
}


@Composable
fun MainScreen(
    aViewModel: AViewModel = viewModel(),
    add: () -> Unit = {},
    number: Int,
    navigate: (Int) -> Unit = {}
) {
    val colorInfoList by aViewModel.colorInfoList.collectAsState()

    Column(Modifier.fillMaxSize().background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("$number", style = MaterialTheme.typography.displayMedium)
        LazyColumn() {

            items(colorInfoList.size) { index ->
                CustomButton(color = colorInfoList[index].color, title = colorInfoList[index].title, onClick = {
                    if (index > 0) navigate(index)
                    else add()


                }
                )
                if (index == 0) {
                    Spacer(modifier = Modifier.size(40.dp))
                } else {
                    Spacer(modifier = Modifier.size(10.dp))
                }

            }
        }
    }
}

@Composable
fun ColorScreen(
    navController: NavHostController,
    goBack: () -> Unit = { navController.popBackStack() },
    number: Int,
    color: Color,
    title: String
) {
    Column(
        Modifier.fillMaxSize().background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$number", style = MaterialTheme.typography.displayLarge)
        CustomButton(color = color, title = title, onClick = { goBack() })
    }
}

@Composable
fun CustomButton(
    color: Color,
    title: String,
    onClick: () -> Unit) {

    Column(

        Modifier.width(200.dp).height(150.dp).background(color).clickable { onClick()},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.titleLarge )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

        StartScreen()

}