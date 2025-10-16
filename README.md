# expo-audio-route-example

This repository demonstrates how to set up a simple/minimalistic example of a local Expo Module that provides access to the device’s active audio route.
It includes native implementations for iOS and Android, exposing an API to:

- Query the current audio output (e.g. speaker, wired headset, Bluetooth)
- Subscribe to audio route change events in real time

## Run it
```
npm install

# Connect your device and build locally
npx expo run:ios --device
npx expo run:android --device
```
