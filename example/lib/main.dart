import 'package:flutter/material.dart';
import 'package:libhc/libhc.dart';

void main() {
  runApp(const App());
}

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: Home());
  }
}

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  final libhc = Libhc();

  @override
  void initState() {
    super.initState();

    init();
  }

  void init() async {
    libhc.physicalButton.listen((status) {
      if (status == PhysicalButtonEvent.pressed) {
        libhc.startQrScanner();
        libhc.startInventoryReader();
      } else {
        libhc.stopQrScanner();
        libhc.stopInventoryReader();
      }
    });
    await libhc.init();
  }

  @override
  void dispose() {
    libhc.stopInventoryReader();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text("UHF Test")),
        body: SizedBox.expand(
          child: Column(
            children: [
              StreamBuilder<String>(
                  stream: libhc.qrCodeResultStream,
                  builder: (context, snapshot) {
                    if (!snapshot.hasData) {
                      return const Text("Waiting for QR data...");
                    }

                    return Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text("QR deteced:"),
                        Text(snapshot.data!)
                      ],
                    );
                  }),
              const SizedBox(height: 5),
              StreamBuilder<RfidResult>(
                  stream: libhc.rfidResultStream,
                  builder: (context, snapshot) {
                    if (!snapshot.hasData) {
                      return const Text("Waiting for RFID data...");
                    }

                    return Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text("Tag deteced:"),
                        Text(
                            "Tag ID: ${snapshot.data!.ecp} (RSSI: ${snapshot.data!.rssi})")
                      ],
                    );
                  }),
              KeyboardListener(
                focusNode: FocusNode(),
                autofocus: true,
                onKeyEvent: (value) => print(value),
                child: FilledButton(
                    onPressed: () async {
                      print("Starting!");
                      // await libhc.startQrScanner();

                      print("Started!");
                    },
                    child: Text("Start QR")),
              ),
            ],
          ),
        ));
  }
}
