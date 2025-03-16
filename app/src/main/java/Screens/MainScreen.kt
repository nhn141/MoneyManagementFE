package Screens

import Composables.BottomNavItem
import Composables.BottomNavigationBar
import Composables.Category_SpecificType_Body
import Composables.Category_SpecificType_Header
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF53dba9),
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
                    contentHeader = { HomeHeader() },
                    contentBody = { HomeScreen() }
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
                    contentBody = { TransactionScreen() }
                )
            }

            composable(BottomNavItem.Category.route) {
                GeneralTemplate(
                    contentHeader = { Category_SpecificType_Header(navController) },
                    contentBody = { Category_SpecificType_Body() }
                )
            }

            composable(BottomNavItem.Profie.route) {
                GeneralTemplate(
                    contentHeader = { ProfileHeader() },
                    contentBody = { ProfieScreen() }
                )
            }
        }
    }
}



@Composable
fun GeneralTemplate(contentHeader: @Composable () -> Unit, contentBody: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.32f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF53dba9)),
                shape = RectangleShape
            ) {
                contentHeader()
            }

            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFF3))
            ) {
                contentBody()
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
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home Screen", fontSize = 24.sp)
    }
}

@Composable
fun AnalysisScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Analysis Screen", fontSize = 24.sp)
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