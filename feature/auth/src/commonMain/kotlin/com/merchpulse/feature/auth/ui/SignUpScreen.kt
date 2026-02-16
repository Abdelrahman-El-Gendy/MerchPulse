package com.merchpulse.feature.auth.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.core.designsystem.AppResources as Res
import com.merchpulse.core.designsystem.component.MerchPulseLogo
import com.merchpulse.feature.auth.presentation.SignUpViewModel
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.feature.auth.SignUpEffect
import com.merchpulse.shared.feature.auth.SignUpIntent
import org.koin.compose.koinInject

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinInject(),
    preferencesManager: com.merchpulse.core.common.PreferencesManager = koinInject(),
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToHome -> {
                    preferencesManager.setBoolean("biometric_enabled", biometricEnabled)
                    onSignUpSuccess()
                }
                is SignUpEffect.ShowError -> { /* handled in state */ }
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
                    .widthIn(max = if (isExpanded) 480.dp else if (isMedium) 420.dp else 600.dp)
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // Logo Container
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        MerchPulseLogo(modifier = Modifier.size(45.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Title and Subtitle
                Text(
                    text = stringResource(Res.string.create_account_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = stringResource(Res.string.join_team_merchpulse),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(40.dp))

                // --- Form Fields ---
                
                // Full Name
                SignUpLabel(stringResource(Res.string.full_name_label_upper))
                Spacer(Modifier.height(8.dp))
                SignUpTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.handleIntent(SignUpIntent.FullNameChanged(it)) },
                    placeholder = "John Doe",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(Modifier.height(24.dp))

                // Phone Number
                SignUpLabel(stringResource(Res.string.phone_number_label_upper))
                Spacer(Modifier.height(8.dp))
                PhoneInput(
                    phoneNumber = state.phoneNumber,
                    countryCode = state.countryCode,
                    onPhoneChange = { viewModel.handleIntent(SignUpIntent.PhoneNumberChanged(it)) },
                    onCountryChange = { viewModel.handleIntent(SignUpIntent.CountryCodeChanged(it)) }
                )

                Spacer(Modifier.height(24.dp))

                // Password
                SignUpLabel(stringResource(Res.string.password_label_upper))
                Spacer(Modifier.height(8.dp))
                SignUpTextField(
                    value = state.pin,
                    onValueChange = { 
                        viewModel.handleIntent(SignUpIntent.PinChanged(it))
                        viewModel.handleIntent(SignUpIntent.ConfirmPinChanged(it))
                    },
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onToggleVisibility = { isPasswordVisible = !isPasswordVisible }
                )
                
                // Password Strength
                Spacer(Modifier.height(12.dp))
                PasswordStrengthIndicator(state.pin)

                Spacer(Modifier.height(24.dp))

                // Workplace Role
                SignUpLabel(stringResource(Res.string.workplace_role_label_upper))
                Spacer(Modifier.height(8.dp))
                RolePicker(
                    selectedRole = state.selectedRole,
                    onRoleChange = { viewModel.handleIntent(SignUpIntent.RoleChanged(it)) }
                )

                Spacer(Modifier.height(32.dp))

                // Biometrics Card
                BiometricToggleCard(
                    enabled = biometricEnabled,
                    onToggle = { biometricEnabled = it }
                )

                Spacer(Modifier.height(32.dp))

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = { viewModel.handleIntent(SignUpIntent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(
                            text = stringResource(Res.string.create_account_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Footer
                Row(
                    modifier = Modifier.clickable { onNavigateToSignIn() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.already_have_account_prefix),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(Res.string.log_in_label),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SignUpLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onToggleVisibility: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(leadingIcon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun PhoneInput(
    phoneNumber: String,
    countryCode: String,
    onPhoneChange: (String) -> Unit,
    onCountryChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    data class Country(val flag: String, val code: String, val name: String, val length: Int)
    val countries = listOf(
        Country("\uD83C\uDDEA\uD83C\uDDEC", "+20", "Egypt", 11),
        Country("\uD83C\uDDFA\uD83C\uDDF8", "+1", "United States", 10),
        Country("\uD83C\uDDF8\uD83C\uDDE6", "+966", "Saudi Arabia", 9),
        Country("\uD83C\uDDE6\uD83C\uDDEA", "+971", "United Arab Emirates", 9)
    )
    val selectedCountry = countries.find { it.code == countryCode } ?: countries[0]

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Country Selector
        Surface(
            modifier = Modifier
                .width(100.dp)
                .height(56.dp)
                .clickable { expanded = true },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(selectedCountry.flag, fontSize = 18.sp)
                Text(selectedCountry.code, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }

            DropdownMenu(
                expanded = expanded, 
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(country.flag, fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Text("${country.name} (${country.code})", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        onClick = {
                            onCountryChange(country.code)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { if (it.length <= selectedCountry.length) onPhoneChange(it) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("(555) 000-0000", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            ),
            visualTransformation = PhoneVisualTransformation(selectedCountry.length)
        )
    }
}

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.isEmpty() -> 0
        password.length < 4 -> 1
        password.length < 8 -> 2
        password.any { !it.isLetterOrDigit() } -> 4
        else -> 3
    }

    val strengthColor = when (strength) {
        0 -> MaterialTheme.colorScheme.outline
        1 -> Color(0xFFEF4444) // Red
        2 -> Color(0xFFF59E0B) // Amber/Orange
        3 -> Color(0xFF10B981) // Emerald/Green
        else -> Color(0xFF10B981) // Emerald/Green
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                val active = index < strength
                val color = if (active) strengthColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(color, RoundedCornerShape(10.dp))
                )
            }
        }
        
        if (password.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            val strengthText = when (strength) {
                1 -> "Weak. Try adding more characters."
                2 -> "Medium. Add numbers or symbols."
                3 -> "Strong. Good password!"
                4 -> "Excellent security strength!"
                else -> ""
            }
            Text(
                text = strengthText,
                style = MaterialTheme.typography.bodySmall,
                color = strengthColor
            )
        }
    }
}

@Composable
private fun RolePicker(
    selectedRole: com.merchpulse.shared.domain.model.Role,
    onRoleChange: (com.merchpulse.shared.domain.model.Role) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { expanded = true },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.BusinessCenter, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = when(selectedRole) {
                    com.merchpulse.shared.domain.model.Role.ADMIN -> "System Admin"
                    com.merchpulse.shared.domain.model.Role.MANAGER -> "Inventory Manager"
                    else -> "Staff Member"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f).background(MaterialTheme.colorScheme.surface)
        ) {
            com.merchpulse.shared.domain.model.Role.entries.filter { it != com.merchpulse.shared.domain.model.Role.ADMIN }.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name.lowercase().replaceFirstChar { it.uppercase() }, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onRoleChange(role)
                        expanded = false
                    }
                )
            }
        }
    }
    
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Role access levels are subject to merchant admin approval.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BiometricToggleCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Face, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.enable_faceid),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.biometric_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}
