package Screens

import DI.API.TokenHandler.AuthStorage
import DI.Composables.CategorySection.AddTransactionScreen
import DI.Composables.CategorySection.Category_SpecificType_Body
import DI.Composables.CategorySection.Category_SpecificType_Header
import DI.Composables.CategorySection.GeneralTemplate
import DI.Models.BalanceInfo
import DI.Models.BottomNavItem
import DI.Composables.NavbarSection.BottomNavigationBar
import DI.Composables.CategorySection.CategoriesGrid
import DI.Composables.CategorySection.Category
import DI.Composables.CategorySection.HeaderSection
import DI.Composables.ProfileSection.ProfileHeaderSection
import DI.Composables.ProfileSection.ProfileScreen
import DI.Composables.HomeSection.HomePageHeaderSection
import DI.ViewModels.CategoryViewModel
import ViewModels.AuthViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneymanagement_frontend.R

@Composable
fun MainScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
//        containerColor = Color(0xFF53dba9),
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                GeneralTemplate(
                    contentHeader = { HomePageHeaderSection(navController) },
                    contentBody = { HomeScreen() }
                )
            }

            composable("add_transaction") {
                AddTransactionScreen(navController)
            }

            composable("category_specific_type") {
                GeneralTemplate(
                    contentHeader = { Category_SpecificType_Header(navController) },
                    contentBody = { Category_SpecificType_Body(navController) }
                )
            }

            composable(BottomNavItem.Analysis.route) {
                GeneralTemplate(
                    contentHeader = { AnalysisHeader() },
                    contentBody = { AnalysisScreen() }

                )
            }

            composable(BottomNavItem.Transaction.route) {
                GeneralTemplate(
                    contentHeader = { TransactionHeader() },
                    contentBody = { TransactionScreen() },
                    fraction = 0.14f
                )
            }

            composable(BottomNavItem.Category.route) {
                val categories = mutableListOf(
                    Category("Food", R.drawable.ic_food),
                    Category("Transport", R.drawable.ic_transport),
                    Category("Medicine", R.drawable.ic_medicine),
                    Category("Groceries", R.drawable.ic_groceries),
                    Category("Rent", R.drawable.ic_rent),
                    Category("Gifts", R.drawable.ic_gifts),
                    Category("Savings", R.drawable.ic_savings),
                    Category("Entertainment", R.drawable.ic_entertainment),
                    Category("More", R.drawable.ic_more)
                )
                GeneralTemplate(
                    //contentHeader = { Category_SpecificType_Header(navController) },
                    //contentBody = { Category_SpecificType_Body() }
                    contentHeader = { HeaderSection(BalanceInfo("$7,783.00", "-$1,187.00", "$20,000.00"), navController) },
                    contentBody = { CategoriesGrid(categories, navController) }
                )
            }

            composable(BottomNavItem.Profie.route) {
                GeneralTemplate(
                    contentHeader = { ProfileHeaderSection() },
                    contentBody = { ProfileScreen() }
                )
            }
        }
    }
}


@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun AnalysisHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Analysis", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun TransactionHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Transactions", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun CategoryHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Categories", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile", fontSize = 24.sp, color = Color.White)
    }
}


@Composable
fun HomeScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    // Get the Context using LocalContext
    val context = LocalContext.current

    // Retrieve the token (use remember to avoid recomputing unnecessarily)
    val token = remember { AuthStorage.getToken(context) ?: "No token found" }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
       Column {
           Button(
               onClick = {
                   authViewModel.logout()
               },
               modifier = Modifier.padding(start = 25.dp)
           ) {
               Text("Log out")
           }

           Text(
               text = "Home Screen\nToken: $token",
               fontSize = 24.sp,
               textAlign = androidx.compose.ui.text.style.TextAlign.Center
           )
       }
    }
}

@Composable
fun AnalysisScreen(categoryViewModel: CategoryViewModel = hiltViewModel()) {
    val categories = categoryViewModel.categories.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Button(
                onClick = {
                    categoryViewModel.getCategories()
                },
                modifier = Modifier.padding(start = 25.dp)
            ) {
                Text("Get Categories")
            }

            categories.value?.let { result ->
                if(result.isSuccess) {
                    val categoryList = result.getOrNull()
                    categoryList?.let { list ->
                        LazyColumn {
                            items(list) { category ->
                                Text(text = category.toString())
                            }
                        }
                    }
                } else {
                    Log.d("Fetching Categories", "Error getting categories to render")
                }
            }
        }

    }
}

@Composable
fun TransactionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Transaction Screen", fontSize = 24.sp)
    }
}

@Composable
fun CategoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Category Screen", fontSize = 24.sp)
    }
}

@Composable
fun ProfieScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profie Screen", fontSize = 24.sp)
    }
}

@Preview
@Composable
fun PreviewHeaderSection() {
    HeaderSection(BalanceInfo("$7,783.00", "-$1,187.00", "$20,000.00"), navController = rememberNavController())
}