package com.readtrac.readtrac.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.*
import com.readtrac.readtrac.ui.view.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController) ?: "home"
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(books = sampleBooks)
            }
            composable("details") {
                BookDetailsScreen(book = sampleBooks.first())
            }
            composable("recommendations") {
                RecommendationScreen(recommendedBooks = sampleBooks)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onItemSelected("home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "details",
            onClick = { onItemSelected("details") },
            icon = { Icon(Icons.Filled.Info, contentDescription = "Details") },
            label = { Text("Details") }
        )
        NavigationBarItem(
            selected = currentRoute == "recommendations",
            onClick = { onItemSelected("recommendations") },
            icon = { Icon(Icons.Filled.Star, contentDescription = "Recommendations") },
            label = { Text("Recommend") }
        )
    }
}

val sampleBooks = listOf(
    Book("1984", "George Orwell", 0.5f),
    Book("Brave New World", "Aldous Huxley", 0.3f),
    Book("Fahrenheit 451", "Ray Bradbury", 0.7f)
)