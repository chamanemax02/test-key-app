package lk.sonora.app.player

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.os.Build
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EqualizerBand(
    val id: Short,
    val frequencyHz: Int,
    val minLevel: Short,
    val maxLevel: Short,
    val currentLevel: Short
)

data class SoundControlState(
    val isEnabled: Boolean = true,
    val currentPreset: String = "Normal",
    val availablePresets: List<String> = listOf("Normal", "Bass Booster", "Rock", "Pop", "Jazz", "Classical", "Acoustic", "Vocal Booster"),
    val bands: List<EqualizerBand> = emptyList(),
    val bassBoostStrength: Int = 300, // 0 - 1000
    val virtualizerStrength: Int = 200, // 0 - 1000
    val loudnessGainMb: Int = 0, // 0 - 1000 mB
    val playbackSpeed: Float = 1.0f,
    val sleepTimerMinutesLeft: Int = 0
)

object SoundEffectManager {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var attachedPlayer: ExoPlayer? = null

    private val _state = MutableStateFlow(SoundControlState())
    val state: StateFlow<SoundControlState> = _state.asStateFlow()

    fun attachAudioSession(player: ExoPlayer, audioSessionId: Int) {
        attachedPlayer = player
        if (audioSessionId == 0) return

        try {
            release()

            // 1. Equalizer
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _state.value.isEnabled
            }
            loadBands()

            // 2. Bass Boost
            bassBoost = BassBoost(0, audioSessionId).apply {
                if (strengthSupported) {
                    enabled = _state.value.isEnabled
                    setStrength(_state.value.bassBoostStrength.toShort())
                }
            }

            // 3. Virtualizer (3D Surround)
            virtualizer = Virtualizer(0, audioSessionId).apply {
                if (strengthSupported) {
                    enabled = _state.value.isEnabled
                    setStrength(_state.value.virtualizerStrength.toShort())
                }
            }

            // 4. Loudness Enhancer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                    enabled = _state.value.isEnabled
                    setTargetGain(_state.value.loudnessGainMb)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBands() {
        val eq = equalizer ?: return
        try {
            val numBands = eq.numberOfBands
            val minEqLevel = eq.bandLevelRange[0]
            val maxEqLevel = eq.bandLevelRange[1]

            val bandList = mutableListOf<EqualizerBand>()
            for (i in 0 until numBands) {
                val bandIdx = i.toShort()
                val freq = eq.getCenterFreq(bandIdx) / 1000 // Convert mHz to Hz
                val current = eq.getBandLevel(bandIdx)
                bandList.add(
                    EqualizerBand(
                        id = bandIdx,
                        frequencyHz = freq,
                        minLevel = minEqLevel,
                        maxLevel = maxEqLevel,
                        currentLevel = current
                    )
                )
            }
            _state.update { it.copy(bands = bandList) }
        } catch (e: Exception) {
            // Default fallback 5-bands if device audiofx query throws
            val defaultBands = listOf(
                EqualizerBand(0, 60, -1500, 1500, 0),
                EqualizerBand(1, 230, -1500, 1500, 0),
                EqualizerBand(2, 910, -1500, 1500, 0),
                EqualizerBand(3, 3600, -1500, 1500, 0),
                EqualizerBand(4, 14000, -1500, 1500, 0)
            )
            _state.update { it.copy(bands = defaultBands) }
        }
    }

    fun toggleEnabled(enabled: Boolean) {
        _state.update { it.copy(isEnabled = enabled) }
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
            virtualizer?.enabled = enabled
            loudnessEnhancer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(bandId: Short, level: Short) {
        try {
            equalizer?.setBandLevel(bandId, level)
            val updated = _state.value.bands.map {
                if (it.id == bandId) it.copy(currentLevel = level) else it
            }
            _state.update { it.copy(bands = updated, currentPreset = "Custom") }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setPreset(presetName: String) {
        val bands = _state.value.bands.toMutableList()
        if (bands.isEmpty()) return

        val levels: List<Short> = when (presetName) {
            "Bass Booster" -> listOf(700, 400, 0, -200, -400)
            "Rock" -> listOf(500, 300, -100, 200, 500)
            "Pop" -> listOf(-100, 200, 500, 300, -200)
            "Jazz" -> listOf(300, 100, -200, 200, 400)
            "Classical" -> listOf(400, 200, -200, 300, 300)
            "Acoustic" -> listOf(350, 250, 100, 250, 300)
            "Vocal Booster" -> listOf(-300, 0, 500, 400, 0)
            else -> listOf(0, 0, 0, 0, 0) // Normal
        }

        try {
            for (i in bands.indices) {
                val lvl = levels.getOrElse(i) { 0.toShort() }.toShort()
                equalizer?.setBandLevel(i.toShort(), lvl)
                bands[i] = bands[i].copy(currentLevel = lvl)
            }
            _state.update { it.copy(currentPreset = presetName, bands = bands) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(strength: Int) {
        _state.update { it.copy(bassBoostStrength = strength) }
        try {
            bassBoost?.setStrength(strength.toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizer(strength: Int) {
        _state.update { it.copy(virtualizerStrength = strength) }
        try {
            virtualizer?.setStrength(strength.toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLoudnessGain(gainMb: Int) {
        _state.update { it.copy(loudnessGainMb = gainMb) }
        try {
            loudnessEnhancer?.setTargetGain(gainMb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _state.update { it.copy(playbackSpeed = speed) }
        try {
            attachedPlayer?.playbackParameters = PlaybackParameters(speed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            loudnessEnhancer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            equalizer = null
            bassBoost = null
            virtualizer = null
            loudnessEnhancer = null
        }
    }
}
