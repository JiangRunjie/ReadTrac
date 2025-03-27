package com.readtrac.readtrac.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.readtrac.readtrac.ui.view.*
import com.readtrac.readtrac.viewmodel.BookDetailViewModel
import com.readtrac.readtrac.viewmodel.BookViewModel
import com.readtrac.readtrac.viewmodel.ReviewViewModel

/**
 * Navigation routes for the application
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddBook : Screen("add_book")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    object Review : Screen("review/{bookId}") {
        fun createRoute(bookId: Long) = "review/$bookId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: BookViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onAddBook = { navController.navigate(Screen.AddBook.route) },
                    onBookSelected = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId)) // Navigate to BookDetail screen
                    }
                )
            }
            
            composable(Screen.AddBook.route) {
                AddBookScreen(
                    onBookAdded = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
                val bookDetailViewModel: BookDetailViewModel = hiltViewModel()

                // Fetch book details when the screen is displayed
                LaunchedEffect(bookId) {
                    bookDetailViewModel.fetchBookDetails(bookId)
                }

                val bookDetail by bookDetailViewModel.bookDetail.collectAsState()

                bookDetail?.let { book ->
                    BookDetailsScreen(
                        book = book,
                        onProgressUpdate = { progress ->
                            bookDetailViewModel.updateProgress(bookId, progress)
                        },
                        onAddReview = { review ->
                            // Handle adding a review (to be implemented)
                        }
                    )
                }
            }

            composable(
                route = Screen.Review.route,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
                ReviewScreen(bookId = bookId)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}