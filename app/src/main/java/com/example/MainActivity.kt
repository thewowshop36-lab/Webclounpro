package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReferralsScreen
import com.example.ui.screens.TaskWatchingDialog
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.screens.WorkTasksScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DenvorkBackground
import com.example.ui.theme.DenvorkCard
import com.example.ui.theme.DenvorkCardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DenvorkViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: DenvorkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DenvorkApp(viewModel = viewModel)
            }
        }
    }
}

data class NavDestination(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenvorkApp(viewModel: DenvorkViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeTask by viewModel.activeTask.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showWebPortal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messageFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Active Task Simulator Modal
    activeTask?.let { task ->
        TaskWatchingDialog(
            task = task,
            onDismiss = { viewModel.closeActiveTask() },
            onComplete = { proof ->
                viewModel.completeActiveTask(task, proof)
            }
        )
    }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
        return
    }

    val user = currentUser!!

    val navItems = listOf(
        NavDestination("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
        NavDestination("Work", Icons.Filled.PlayCircleOutline, Icons.Outlined.PlayCircleOutline, "nav_work"),
        NavDestination("Referrals", Icons.Filled.Group, Icons.Outlined.Group, "nav_referrals"),
        NavDestination("Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_wallet"),
        NavDestination("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (!isWideScreen) {
            // Phone Portrait Layout with Bottom Navigation Bar
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DenvorkBackground),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, EmeraldPrimary, RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_denvork_logo),
                                        contentDescription = "Denvork Logo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DENVORK",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { showWebPortal = !showWebPortal },
                                modifier = Modifier.testTag("toggle_web_portal_btn")
                            ) {
                                Icon(
                                    imageVector = if (showWebPortal) Icons.Filled.Dashboard else Icons.Outlined.Language,
                                    contentDescription = if (showWebPortal) "Show Native App" else "Show Vercel Web Portal",
                                    tint = if (showWebPortal) CyanAccent else EmeraldLight
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            // Balance tag pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EmeraldPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = viewModel.formatMoney(user.balance),
                                    color = EmeraldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = DenvorkBackground
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_nav_bar"),
                        containerColor = DenvorkCard,
                        contentColor = TextPrimary
                    ) {
                        navItems.forEachIndexed { index, item ->
                            val isSelected = selectedTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) EmeraldPrimary else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) EmeraldLight else TextSecondary
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                                    selectedIconColor = EmeraldPrimary,
                                    unselectedIconColor = TextSecondary,
                                    selectedTextColor = EmeraldLight,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = DenvorkBackground
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    if (showWebPortal) {
                        WebPortalScreen(onClose = { showWebPortal = false })
                    } else {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                user = user,
                                onNavigateTab = { selectedTab = it }
                            )
                            1 -> WorkTasksScreen(viewModel = viewModel)
                            2 -> ReferralsScreen(viewModel = viewModel, user = user)
                            3 -> WalletScreen(viewModel = viewModel, user = user)
                            4 -> ProfileScreen(viewModel = viewModel, user = user)
                        }
                    }
                }
            }
        } else {
            // Wide Screen / Tablet Layout with Navigation Rail
            Row(modifier = Modifier.fillMaxSize().background(DenvorkBackground)) {
                NavigationRail(
                    containerColor = DenvorkCard,
                    contentColor = TextPrimary,
                    header = {
                        Spacer(modifier = Modifier.size(16.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, EmeraldPrimary, RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_denvork_logo),
                                contentDescription = "Denvork Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.fillMaxHeight().testTag("tablet_nav_rail")
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) EmeraldPrimary else TextSecondary
                                )
                            },
                            label = { Text(item.title, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                                selectedIconColor = EmeraldPrimary,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = EmeraldLight,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    if (showWebPortal) {
                        WebPortalScreen(onClose = { showWebPortal = false })
                    } else {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                user = user,
                                onNavigateTab = { selectedTab = it }
                            )
                            1 -> WorkTasksScreen(viewModel = viewModel)
                            2 -> ReferralsScreen(viewModel = viewModel, user = user)
                            3 -> WalletScreen(viewModel = viewModel, user = user)
                            4 -> ProfileScreen(viewModel = viewModel, user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
