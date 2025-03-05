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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    private val _colorRed = MutableStateFlow(Color.Red)
    private val _colorGreen = MutableStateFlow(Color.Green)
    private val _titleList = MutableStateFlow(listOf("PLUS", "RÖD", "GRÖN"))
    private val _colorList = MutableStateFlow(listOf(Color.Gray, Color.Red, Color.Green))

    val colorList: StateFlow<List<Color>> = _colorList
    val titleList: StateFlow<List<String>> = _titleList
    val aNumber: StateFlow<Int> = _aNumber
    val colorRed: StateFlow<Color> = _colorRed
    val colorGreen: StateFlow<Color> = _colorGreen

    fun addOne() {
        _aNumber.value++
    }

    fun updateButtonInfo(index: Int, newColor: Color, newTitle: String) {
        val updateColors = _colorList.value.toMutableList()
        val updateTitles = _titleList.value.toMutableList()
        updateColors[index] = newColor
        updateTitles[index] = newTitle
        _colorList.value = updateColors
        _titleList.value = updateTitles
    }

}

object Routes {
    const val MAIN = "main"
    const val COLOR = "color"
}

@Composable
fun StartScreen() {
    val navController = rememberNavController()
    val aViewModel: AViewModel = viewModel()
    val mainColor by aViewModel.colorRed.collectAsState()
    var color = mainColor

    val number by aViewModel.aNumber.collectAsState()
    val add = {
        aViewModel.addOne()
    }
    val navigateRed = {
        color = aViewModel.colorRed.value
        navController.navigate(Routes.COLOR)
    }
    val navigateBlue = {
        color = aViewModel.colorGreen.value
        navController.navigate(Routes.COLOR)
    }
    
    NavHost(navController = navController, startDestination = Routes.MAIN, modifier = Modifier.fillMaxSize()) {
        composable(Routes.MAIN) {
            MainScreen(navigateRed = navigateRed, navigateGreen = navigateBlue, number = number, add = add)
        }
        composable(Routes.COLOR) {
            ColorScreen(color = color, navController = navController, number = number)
        }
    }
}


@Composable
fun MainScreen(
    aViewModel: AViewModel = viewModel(),
    navigateRed: () -> Unit = {},
    navigateGreen: () -> Unit = {},
    add: () -> Unit = {},
    number: Int
) {
    val titleList by aViewModel.titleList.collectAsState()
    val colorList by aViewModel.colorList.collectAsState()

    Column(Modifier.fillMaxSize().background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("$number", style = MaterialTheme.typography.displayMedium)
        LazyColumn() {

            items(titleList.size) { index ->
                CustomButton(color = colorList[index], title = titleList[index], onClick = {
                    if (colorList[index] == Color.Red)
                        navigateRed()
                    else if (colorList[index] == Color.Green)
                        navigateGreen()
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
    color: Color,
    aViewModel: AViewModel = viewModel(),
    goBack: () -> Unit = {navController.popBackStack()},
    number: Int
) {
    val colorRed by aViewModel.colorRed.collectAsState()
    val title = if (color == colorRed) "RÖD" else "GRÖN"

    Column(Modifier.fillMaxSize().background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
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