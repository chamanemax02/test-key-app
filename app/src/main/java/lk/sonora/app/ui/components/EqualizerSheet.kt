package lk.sonora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lk.sonora.app.player.SoundEffectManager
import lk.sonora.app.theme.AccentPink
import lk.sonora.app.theme.AccentPurple
import lk.sonora.app.theme.BgCard
import lk.sonora.app.theme.BgCardElevated
import lk.sonora.app.theme.SonoraGradient
import lk.sonora.app.theme.TextMuted
import lk.sonora.app.theme.TextPrimary
import lk.sonora.app.theme.TextSecondary

@Composable
fun EqualizerSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundState by SoundEffectManager.state.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(580.dp),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Title, Switch, and Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SonoraGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sound & Equalizer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (soundState.isEnabled) "Enhanced Audio Active" else "Equalizer Disabled",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (soundState.isEnabled) AccentPink else TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = soundState.isEnabled,
                        onCheckedChange = { SoundEffectManager.toggleEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.ui.graphics.Color.Black,
                            checkedTrackColor = SpotifyGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = BgCardElevated
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Presets Horizontal Row
            Text(
                text = "SOUND PRESETS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                soundState.availablePresets.forEach { preset ->
                    val isSelected = soundState.currentPreset == preset
                    val bgModifier = if (isSelected) Modifier.background(SpotifyGreen) else Modifier.background(BgCardElevated)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .then(bgModifier)
                            .clickable {
                                SoundEffectManager.setPreset(preset)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) androidx.compose.ui.graphics.Color.Black else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5-Band Equalizer Sliders
            Text(
                text = "FREQUENCY BANDS (dB)",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            soundState.bands.forEach { band ->
                val freqLabel = when {
                    band.frequencyHz >= 1000 -> "${band.frequencyHz / 1000} kHz"
                    else -> "${band.frequencyHz} Hz"
                }
                val min = band.minLevel.toFloat()
                val max = band.maxLevel.toFloat()
                val current = band.currentLevel.toFloat()
                val dB = (band.currentLevel / 100)

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = freqLabel, fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${if (dB > 0) "+$dB" else "$dB"} dB", fontSize = 12.sp, color = SpotifyGreen)
                    }
                    Slider(
                        value = current,
                        onValueChange = { newValue ->
                            SoundEffectManager.setBandLevel(band.id, newValue.toInt().toShort())
                        },
                        valueRange = min..max,
                        enabled = soundState.isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = TextPrimary,
                            activeTrackColor = SpotifyGreen,
                            inactiveTrackColor = Color(0x22FFFFFF)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bass Boost & 3D Surround Effects
            Text(
                text = "AUDIO ENHANCEMENTS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Bass Boost Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "🔥 Deep Bass Booster", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(text = "${(soundState.bassBoostStrength / 10)}%", fontSize = 12.sp, color = SpotifyGreen)
                }
                Slider(
                    value = soundState.bassBoostStrength.toFloat(),
                    onValueChange = { SoundEffectManager.setBassBoost(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = soundState.isEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = TextPrimary,
                        activeTrackColor = SpotifyGreen,
                        inactiveTrackColor = Color(0x22FFFFFF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3D Virtualizer / Surround Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "🎧 3D Surround Sound (Virtualizer)", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(text = "${(soundState.virtualizerStrength / 10)}%", fontSize = 12.sp, color = SpotifyGreen)
                }
                Slider(
                    value = soundState.virtualizerStrength.toFloat(),
                    onValueChange = { SoundEffectManager.setVirtualizer(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = soundState.isEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = TextPrimary,
                        activeTrackColor = SpotifyGreen,
                        inactiveTrackColor = Color(0x22FFFFFF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Speed Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", tint = TextSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAYBACK SPEED",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speedOptions.forEach { speed ->
                    val isSelected = soundState.playbackSpeed == speed
                    val speedBgModifier = if (isSelected) Modifier.background(SonoraGradient) else Modifier.background(BgCardElevated)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .then(speedBgModifier)
                            .clickable {
                                SoundEffectManager.setPlaybackSpeed(speed)
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (speed == 1.0f) "1.0x (Normal)" else "${speed}x",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
