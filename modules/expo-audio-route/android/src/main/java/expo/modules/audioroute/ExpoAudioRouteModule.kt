package expo.modules.audioroute

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager

class ExpoAudioRouteModule : Module() {
  private var audioManager: AudioManager? = null
  private var deviceCallback: AudioDeviceCallback? = null

  override fun definition() = ModuleDefinition {
    Name("ExpoAudioRoute")
    Events("onAudioRouteChange")

    OnCreate {
      audioManager = appContext.reactContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    AsyncFunction("getCurrentRouteAsync") {
      currentRoute()
    }

    OnStartObserving("onAudioRouteChange") {
      startObservingRouteChanges()
    }

    OnStopObserving("onAudioRouteChange")  {
      stopObservingRouteChanges()
    }
  }

  private fun startObservingRouteChanges() {
    val am = audioManager ?: return
    if (deviceCallback != null) return
 
    deviceCallback = object : AudioDeviceCallback() {
      override fun onAudioDevicesAdded(added: Array<out AudioDeviceInfo>?) {
        sendEvent("onAudioRouteChange", mapOf("route" to currentRoute()))
      }
      override fun onAudioDevicesRemoved(removed: Array<out AudioDeviceInfo>?) {
        sendEvent("onAudioRouteChange", mapOf("route" to currentRoute()))
      }
    }
    am.registerAudioDeviceCallback(deviceCallback, null)
  }

  private fun stopObservingRouteChanges() {
    val am = audioManager ?: return
    deviceCallback?.let { am.unregisterAudioDeviceCallback(it) }
    deviceCallback = null
  }

  private fun currentRoute(): String {
    val am = audioManager ?: return "unknown"
    val outputs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

    // Check in priority order: wired > bluetooth > speaker
    val wiredTypes = listOf(
      AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
      AudioDeviceInfo.TYPE_WIRED_HEADSET,
    )

    val bluetoothTypes = listOf(
      AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
      AudioDeviceInfo.TYPE_BLUETOOTH_SCO
    )

    val speakerTypes = listOf(
      AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
      AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
    )

    val device = outputs.firstOrNull { it.type in wiredTypes }
      ?: outputs.firstOrNull { it.type in bluetoothTypes }
      ?: outputs.firstOrNull { it.type in speakerTypes }

    return when (device?.type) {
      in wiredTypes -> "wiredHeadset"
      in bluetoothTypes -> "bluetooth"
      in speakerTypes -> "speaker"
      else -> "unknown"
    }
  }
}