package com.merchpulse.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.feature.auth.presentation.SignInViewModel
import com.merchpulse.shared.feature.auth.SignInEffect
import com.merchpulse.shared.feature.auth.SignInIntent
import org.koin.compose.koinInject
import com.merchpulse.core.designsystem.AppResources as Res
import com.merchpulse.core.designsystem.component.MerchPulseLogo

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinInject(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBiometricLoginClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginMethod by remember { mutableStateOf("password") }
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignInEffect.NavigateToHome -> onLoginSuccess()
                is SignInEffect.ShowError -> { /* handled in state */ }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = if (isExpanded) 480.dp else if (isMedium) 400.dp else 600.dp)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))

                // Top Logo
                MerchPulseLogo(
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Titles
                Text(
                    stringResource(Res.string.merchant_portal),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(Res.string.manage_business),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                // Login/Sign Up Switcher
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        // Sliding background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .shadow(1.dp, RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(12.dp))
                        )

                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(Res.string.login), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { onNavigateToSignUp() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(Res.string.sign_up), color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Login Method Switcher
                if (viewModel.isBiometricEnabled && viewModel.hasLastUser) {
                    TabRow(
                        selectedTabIndex = if (loginMethod == "password") 0 else 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            if (tabPositions.isNotEmpty()) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (loginMethod == "password") 0 else 1]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = loginMethod == "password",
                            onClick = { loginMethod = "password" },
                            text = { Text("Password", fontWeight = if (loginMethod == "password") FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = loginMethod == "biometric",
                            onClick = { loginMethod = "biometric" },
                            text = { Text("Biometric", fontWeight = if (loginMethod == "biometric") FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                    Spacer(Modifier.height(32.dp))
                }

                if (loginMethod == "password") {
                    // Phone Number Input
                    var isCountryDropdownExpanded by remember { mutableStateOf(false) }
                    
                    data class Country(val flag: String, val code: String, val maxLength: Int)
                    
                    val countries = listOf(
                        Country("\uD83C\uDDEA\uD83C\uDDEC", "+20", 11),  // Egypt
                        Country("\uD83C\uDDFA\uD83C\uDDF8", "+1", 10),   // US
                        Country("\uD83C\uDDF8\uD83C\uDDE6", "+966", 9),  // KSA
                        Country("\uD83C\uDDE6\uD83C\uDDEA", "+971", 9)   // UAE
                    )

                    val currentCountry = countries.find { it.code == state.countryCode } ?: countries[0]

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(Res.string.phone_number),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = { input ->
                                if (input.length <= currentCountry.maxLength && input.all { it.isDigit() }) {
                                    viewModel.handleIntent(SignInIntent.PhoneNumberChanged(input))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            placeholder = { Text(stringResource(Res.string.phone_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            leadingIcon = {
                                Box {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable { isCountryDropdownExpanded = true }
                                            .padding(start = 12.dp, end = 8.dp)
                                    ) {
                                        Text(currentCountry.flag, fontSize = 18.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(state.countryCode, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                        Icon(
                                            Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height(24.dp)
                                                .width(1.dp),
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isCountryDropdownExpanded,
                                        onDismissRequest = { isCountryDropdownExpanded = false },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                    ) {
                                        countries.forEach { country ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(country.flag, fontSize = 20.sp)
                                                        Spacer(Modifier.width(12.dp))
                                                        Text(country.code, color = MaterialTheme.colorScheme.onBackground)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.handleIntent(SignInIntent.CountryCodeChanged(country.code))
                                                    isCountryDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Password Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(Res.string.password),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.pin,
                            onValueChange = { viewModel.handleIntent(SignInIntent.PinChanged(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            placeholder = { Text(stringResource(Res.string.enter_password), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                            stringResource(Res.string.forgot_password),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {  }
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    if (state.error != null) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(16.dp))
                    }

                    // Sign In Button
                    Button(
                        onClick = { viewModel.handleIntent(SignInIntent.Submit) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(Res.string.sign_in), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                } else {
                    // Biometric Login Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(24.dp))
                            
                            Text(
                                "Quick Access",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Text(
                                "Use your biometric data to sign in safely and quickly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            
                            Spacer(Modifier.height(32.dp))
                            
                            Button(
                                onClick = onBiometricLoginClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Fingerprint, null)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Start Authentication", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (loginMethod == "password") {
                     // Footer Logo
                    MerchPulseLogo(
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.recaptcha_line1),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        stringResource(Res.string.recaptcha_line2),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
