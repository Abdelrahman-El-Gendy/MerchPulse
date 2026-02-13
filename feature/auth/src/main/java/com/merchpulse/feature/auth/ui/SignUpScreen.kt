package com.merchpulse.feature.auth.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.feature.auth.presentation.SignUpViewModel
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.feature.auth.SignUpEffect
import com.merchpulse.shared.feature.auth.SignUpIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel(),
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToHome -> onSignUpSuccess()
                is SignUpEffect.ShowError -> { /* handled in state */ }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Top Icon Box
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Join the Team",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Create your employee account to manage\ninventory, track time, and access sales tools.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Login/Sign Up Switcher
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    // Sliding background (Fixed on Right for SignUp)
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val tabWidth = maxWidth / 2
                        Box(
                            modifier = Modifier
                                .offset(x = tabWidth)
                                .width(tabWidth)
                                .fillMaxHeight()
                                .shadow(1.dp, RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(12.dp))
                        )
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onNavigateToSignIn() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Login", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { /* Already on Sign Up */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sign Up", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Full Name Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Full Legal Name",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.handleIntent(SignUpIntent.FullNameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    placeholder = { Text("e.g. Alex Johnson", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = {
                        Icon(Icons.Default.BusinessCenter, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Mobile Number
            var isCountryDropdownExpanded by remember { mutableStateOf(false) }
            val countries = listOf(
                "🇪🇬" to "+20",
                "🇺🇸" to "+1",
                "🇸🇦" to "+966",
                "🇦🇪" to "+971"
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Mobile Number",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = { viewModel.handleIntent(SignUpIntent.PhoneNumberChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    placeholder = { Text("111 222 3333", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = {
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { isCountryDropdownExpanded = true }
                                    .padding(start = 12.dp, end = 8.dp)
                            ) {
                                val currentCountry = countries.find { it.second == state.countryCode } ?: countries[0]
                                Text(currentCountry.first, fontSize = 18.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(state.countryCode, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
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
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                countries.forEach { (flag, code) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(flag, fontSize = 20.sp)
                                                Spacer(Modifier.width(12.dp))
                                                Text(code, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        },
                                        onClick = {
                                            viewModel.handleIntent(SignUpIntent.CountryCodeChanged(code))
                                            isCountryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Password Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Create Password",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.pin,
                    onValueChange = { 
                        viewModel.handleIntent(SignUpIntent.PinChanged(it))
                        viewModel.handleIntent(SignUpIntent.ConfirmPinChanged(it)) // Simplify for design match
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    placeholder = { Text("••••••••", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Spacer(Modifier.height(8.dp))
                // Password Strength Indicator
                val passwordStrength = remember(state.pin) {
                    when {
                        state.pin.isEmpty() -> 0
                        state.pin.length < 6 -> 1
                        state.pin.length < 8 -> 2
                        state.pin.any { it.isDigit() } && state.pin.any { !it.isLetterOrDigit() } && state.pin.length >= 8 -> 4
                        state.pin.length >= 8 -> 3
                        else -> 1
                    }
                }
                
                val strengthColor = when (passwordStrength) {
                    1 -> Color(0xFFEF4444) // Red stays Red
                    2 -> Color(0xFFF59E0B) // Orange stays Orange
                    3 -> Color(0xFFFACC15) // Yellow stays Yellow
                    4 -> Color(0xFF10B981) // Green stays Green
                    else -> MaterialTheme.colorScheme.outline
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(4) { index ->
                        val isFilled = index < passwordStrength
                        val color by animateColorAsState(
                            targetValue = if (isFilled) strengthColor else MaterialTheme.colorScheme.outline,
                            label = "strengthColor"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(color, RoundedCornerShape(2.dp))
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Must be at least 8 characters with 1 special character.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            // Desired Role
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Desired Role",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Requires Approval",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Role Options
                RoleOption(
                    title = "Sales Associate",
                    subtitle = "POS access, basic inventory view",
                    icon = Icons.Default.Storefront,
                    iconColor = Color(0xFF3B82F6),
                    isSelected = state.selectedRole == Role.STAFF,
                    onClick = { viewModel.handleIntent(SignUpIntent.RoleChanged(Role.STAFF)) }
                )
                Spacer(Modifier.height(12.dp))
                RoleOption(
                    title = "Inventory Manager",
                    subtitle = "Full stock control, auditing tools",
                    icon = Icons.Default.Dashboard,
                    iconColor = Color(0xFFA855F7),
                    isSelected = state.selectedRole == Role.MANAGER,
                    onClick = { viewModel.handleIntent(SignUpIntent.RoleChanged(Role.MANAGER)) }
                )
            }

            Spacer(Modifier.height(32.dp))

            // Terms Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isTermsAccepted,
                    onCheckedChange = { viewModel.handleIntent(SignUpIntent.TermsToggled(it)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Text(
                    buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Terms & Conditions")
                        }
                        append(" and ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Privacy Policy")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
            }

            // Request Account Button
            Button(
                onClick = { viewModel.handleIntent(SignUpIntent.Submit) },
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
                        Text("Request Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            TextButton(onClick = onNavigateToSignIn) {
                Text("Already have an account? Log In", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RoleOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            2.dp, 
            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(
                selected = isSelected,
                onClick = null, // Handled by Surface
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
