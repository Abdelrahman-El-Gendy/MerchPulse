package com.merchpulse.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.merchpulse.android.navigation.Screen

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@Composable
fun MerchPulseMainScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val windowSizeClass = LocalWindowSizeClass.current
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    // Hide bottom bar on auth and detail/form screens
    val showNavigation = currentRoute !in listOf(
        Screen.SignIn.route, 
        Screen.SignUp.route,
        Screen.ProductForm.route,
        Screen.LowStock.route
    )

    val darkBg = MaterialTheme.colorScheme.background

    Row(modifier = Modifier.fillMaxSize().background(darkBg)) {
        // Show Navigation Rail for Medium and Expanded screens
        if (showNavigation && !isCompact) {
            ResponsiveNavigationRail(navController, currentRoute)
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Screen Content wraps the entire area
            content(PaddingValues(bottom = if (showNavigation && isCompact) 100.dp else 0.dp))

            // Floating Bottom Bar as an overlay - only for compact screens
            if (showNavigation && isCompact) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    FloatingBottomBar(navController, currentRoute)
                }
            }
        }
    }
}

@Composable
fun ResponsiveNavigationRail(navController: NavController, currentRoute: String?) {
    val accentBlue = MaterialTheme.colorScheme.primary
    val navBackBg = MaterialTheme.colorScheme.surface
    
    val navAction = { route: String ->
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavigationRail(
        containerColor = navBackBg,
        header = {
            IconButton(
                onClick = { if (currentRoute != Screen.Punch.route) navAction(Screen.Punch.route) },
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(56.dp)
                    .background(accentBlue, CircleShape)
            ) {
                Icon(Icons.Default.Fingerprint, "Punch", tint = Color.White)
            }
        },
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationRailItem(
                selected = currentRoute == Screen.Home.route,
                onClick = { navAction(Screen.Home.route) },
                icon = { Icon(Icons.Default.GridView, "Home") },
                label = { Text("Home") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = accentBlue,
                    selectedTextColor = accentBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationRailItem(
                selected = currentRoute == Screen.Inventory.route,
                onClick = { navAction(Screen.Inventory.route) },
                icon = { Icon(Icons.Default.Inventory2, "Stock") },
                label = { Text("Stock") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = accentBlue,
                    selectedTextColor = accentBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationRailItem(
                selected = currentRoute == Screen.Employees.route,
                onClick = { navAction(Screen.Employees.route) },
                icon = { Icon(Icons.Default.People, "Team") },
                label = { Text("Team") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = accentBlue,
                    selectedTextColor = accentBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationRailItem(
                selected = false,
                onClick = { },
                icon = { Icon(Icons.Default.Settings, "Settings") },
                label = { Text("Settings") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = accentBlue,
                    selectedTextColor = accentBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun FloatingBottomBar(navController: NavController, currentRoute: String?) {
    val darkBg = Color(0xFF1E2538) // Matching internal card bg
    val accentBlue = Color(0xFF3B82F6)

    val navAction = { route: String ->
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(92.dp), // Height to accommodate the raised Fingerprint button
        contentAlignment = Alignment.BottomCenter
    ) {
        // The bar background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            color = darkBg.copy(alpha = 0.98f),
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    Icons.Default.GridView, 
                    "Home", 
                    currentRoute == Screen.Home.route,
                    accentBlue
                ) { navAction(Screen.Home.route) }
                
                NavigationItem(
                    Icons.Default.Inventory2, 
                    "Stock", 
                    currentRoute == Screen.Inventory.route,
                    accentBlue
                ) { navAction(Screen.Inventory.route) }

                // Spacer for the center floating button
                Spacer(modifier = Modifier.size(64.dp))

                NavigationItem(
                    Icons.Default.People, 
                    "Team", 
                    currentRoute == Screen.Employees.route,
                    accentBlue
                ) { navAction(Screen.Employees.route) }

                NavigationItem(
                    Icons.Default.Settings, 
                    "Settings", 
                    false, // Placeholder
                    accentBlue
                ) { /* Settings action */ }
            }
        }

        // The previous circular Fingerprint "Punch" button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .shadow(12.dp, CircleShape)
                .background(accentBlue, CircleShape)
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (currentRoute != Screen.Punch.route) {
                            navAction(Screen.Punch.route)
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Fingerprint, 
                "Punch", 
                tint = Color.White, 
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavigationItem(
    icon: ImageVector, 
    label: String, 
    selected: Boolean, 
    accent: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Icon(
            icon, 
            label, 
            tint = if (selected) accent else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = if (selected) accent else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(4.dp)
                    .background(accent, CircleShape)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}
