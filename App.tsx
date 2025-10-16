import * as React from "react";
import { Text, View } from "react-native";
import AudioModule, { type AudioRoute } from "./modules/expo-audio-route";
import { useEventListener } from "expo";

export default function App() {
  const [currentRoute, setCurrentRoute] = React.useState<AudioRoute>("unknown");
  useEventListener(AudioModule, "onAudioRouteChange", ({ route }) => {
    setCurrentRoute(route);
  });

  React.useEffect(() => {
    // This will call getCurrentRouteAsync function we declared in the Swift/Kotlin code
    AudioModule.getCurrentRouteAsync().then((route) => {
      setCurrentRoute(route);
    });
  }, []);

  return (
    <View
      style={{
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <Text>Audio Route: {currentRoute}</Text>
    </View>
  );
}
