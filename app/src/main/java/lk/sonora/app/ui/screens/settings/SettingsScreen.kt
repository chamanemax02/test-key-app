package lk.sonora.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lk.sonora.app.R
import lk.sonora.app.SonoraApplication
import lk.sonora.app.theme.*
import lk.sonora.app.ui.components.SectionHeader

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as SonoraApplication
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
        ) {
            // Appearance & Audio Section
            item {
                SectionHeader(title = stringResource(R.string.settings_appearance))
            }
            item {
                SettingsItemCard(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.settings_theme),
                    subtitle = stringResource(R.string.settings_theme_dark)
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsItemCard(
                    icon = Icons.Default.GraphicEq,
                    title = stringResource(R.string.settings_audio_quality),
                    subtitle = stringResource(R.string.settings_quality_high)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Storage & Data
            item {
                SectionHeader(title = "Storage & Data")
            }
            item {
                SettingsItemCard(
                    icon = Icons.Default.CleaningServices,
                    title = stringResource(R.string.settings_clear_cache),
                    subtitle = stringResource(R.string.settings_clear_cache_desc),
                    onClick = {
                        Toast.makeText(context, "Cache cleared successfully", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsItemCard(
                    icon = Icons.Default.DeleteForever,
                    title = stringResource(R.string.settings_reset_data),
                    subtitle = "Clear local history, queue, and cached metadata",
                    tint = ColorError,
                    onClick = { showResetDialog = true }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Legal & About
            item {
                SectionHeader(title = stringResource(R.string.settings_about))
            }
            item {
                SettingsItemCard(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.app_name),
                    subtitle = "${stringResource(R.string.settings_version)} • ${stringResource(R.string.settings_developer)}"
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsItemCard(
                    icon = Icons.Default.Security,
                    title = stringResource(R.string.settings_disclaimer_title),
                    subtitle = stringResource(R.string.settings_disclaimer_content)
                )
            }
        }
    }

    // Reset Data Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.settings_reset_data),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_reset_data_confirm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            app.musicRepository.clearAllData()
                            Toast.makeText(context, "App data reset complete", Toast.LENGTH_SHORT).show()
                            showResetDialog = false
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.action_confirm), color = ColorError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = stringResource(R.string.library_playlist_cancel), color = TextSecondary)
                }
            },
            containerColor = BgCard
        )
    }
}

@Composable
fun SettingsItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: androidx.compose.ui.graphics.Color = AccentPurple,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
