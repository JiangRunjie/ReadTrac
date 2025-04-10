package com.readtrac.readtrac.routes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
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
import com.readtrac.readtrac.viewmodel.RecommendationViewModel
import com.readtrac.readtrac.viewmodel.ReviewViewModel

/**
 * Navigation routes for the application.
 * This sealed class defines all the possible navigation destinations within the app
 * and provides helper methods for route creation with parameters.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddBook : Screen("add_book")
    object Recommendations : Screen("recommendations")
    
    /**
     * Book detail screen that takes a book ID parameter
     */
    object BookDetail : Screen("book_detail/{bookId}") {
        /**
         * Creates a route with the specific book ID
         * @param bookId The ID of the book to display
         * @return Formatted route string with the book ID
         */
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    
    /**
     * Review screen that takes a book ID parameter
     */
    object Review : Screen("review/{bookId}") {
        /**
         * Creates a route with the specific book ID
         * @param bookId The ID of the book to review
         * @return Formatted route string with the book ID
         */
        fun createRoute(bookId: Long) = "review/$bookId"
    }
}

/**
 * Main navigation component for the application.
 * Sets up the navigation graph, handles routing between screens,
 * and manages the bottom navigation bar.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Home navigation item
                NavigationBarItem(
                    icon = { 
                        if (currentRoute == Screen.Home.route) {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        } else {
                            Icon(Icons.Outlined.Home, contentDescription = "Home") 
                        }
                    },
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            // Pop up to the start destination of the graph to avoid
                            // building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
                
                // Recommendations navigation item
                NavigationBarItem(
                    icon = { 
                        if (currentRoute == Screen.Recommendations.route) {
                            Icon(Icons.Default.ThumbUp, contentDescription = "Recommendations")
                        } else {
                            Icon(Icons.Outlined.ThumbUp, contentDescription = "Recommendations")
                        }
                    },
                    label = { Text("For You") },
                    selected = currentRoute == Screen.Recommendations.route,
                    onClick = {
                        navController.navigate(Screen.Recommendations.route) {
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
        // Main navigation host
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            // Add animations between destinations
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            // Home screen destination
            composable(Screen.Home.route) {
                val viewModel: BookViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onAddBook = { navController.navigate(Screen.AddBook.route) },
                    onBookSelected = { bookId ->
                        // Navigate to BookDetail screen with the selected book ID
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }
            
            // Add book screen destination
            composable(Screen.AddBook.route) {
                AddBookScreen(
                    onBookAdded = { 
                        // Return to previous screen after adding a book
                        navController.popBackStack() 
                    }
                )
            }
            
            // Book detail screen destination with bookId argument
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
                        onRatingChanged = { rating ->
                            bookDetailViewModel.updateRating(bookId, rating)
                        },
                        onAddReview = { 
                            // Navigate to Review screen with the current book ID
                            navController.navigate(Screen.Review.createRoute(bookId))
                        },
                        onBackPressed = {
                            // Return to the previous screen
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Review screen destination with bookId argument
            composable(
                route = Screen.Review.route,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
                ReviewScreen(
                    bookId = bookId,
                    onReviewSubmitted = {
                        // Return to the book detail screen after submitting a review
                        navController.popBackStack()
                    }
                )
            }
            
            // Recommendations screen destination
            composable(Screen.Recommendations.route) {
                val viewModel: RecommendationViewModel = hiltViewModel()
                RecommendationScreen(
                    viewModel = viewModel,
                    // Updated to stay within the RecommendationScreen component
                    // instead of navigating to BookDetailScreen
                    onBookSelected = { },
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Helper function that returns the current active route in the navigation stack.
 * 
 * @param navController The NavHostController to get the current route from
 * @return The current route as a String, or null if no route is active
 */
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}