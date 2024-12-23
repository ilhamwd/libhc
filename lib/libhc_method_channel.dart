import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:libhc/models/rfid_result.dart';
import 'package:libhc/physical_button_event.dart';

import 'libhc_platform_interface.dart';

class MethodChannelLibhc extends LibhcPlatform {
  bool _isQrCodeScannerOn = false;
  bool _isRfidScannerOn = false;

  @visibleForTesting
  final methodChannel = const MethodChannel('libhc');

  @visibleForTesting
  final eventChannel = const EventChannel('libhc_stream');

  final _rfidStreamController = StreamController<RfidResult>.broadcast();
  final _physicalButtonStreamController =
      StreamController<PhysicalButtonEvent>.broadcast();
  final _qrCodeStreamController = StreamController<String>.broadcast();

  MethodChannelLibhc() {
    eventChannel.receiveBroadcastStream().listen((data) {
      final rawData = data as Map<Object?, Object?>;
      final type = rawData['type'];

      if (type == "rfid_results") {
        for (final rawData in rawData['data'] as List<dynamic>) {
          try {
            _rfidStreamController.sink.add(RfidResult.fromJson(rawData));
          } catch (e) {
            continue;
          }
        }
      }

      if (type == "physical_button_state") {
        _physicalButtonStreamController.sink
            .add(PhysicalButtonEvent.values[rawData['data'] as int]);
      }

      if (type == "qr_code_result") {
        _qrCodeStreamController.sink.add(rawData['data'] as String);
      }
    });
  }

  @override
  Stream<RfidResult> get rfidResult => _rfidStreamController.stream;

  @override
  Stream<PhysicalButtonEvent> get physicalButton =>
      _physicalButtonStreamController.stream;

  @override
  Stream<String> get qrCodeResult => _qrCodeStreamController.stream;

  @override
  Future<void> init() {
    return methodChannel.invokeMethod("init");
  }

  @override
  Future<bool?> startInventoryReader() async {
    if (_isRfidScannerOn) return true;

    _isRfidScannerOn = true;

    return await methodChannel.invokeMethod<bool>("startInventoryReader");
  }

  @override
  Future<bool?> stopInventoryReader() {
    _isRfidScannerOn = false;

    return methodChannel.invokeMethod<bool>("stopInventoryReader");
  }

  @override
  Future<void> startQrScanner() async {
    if (_isQrCodeScannerOn) return;

    _isQrCodeScannerOn = true;

    await methodChannel.invokeMethod("startQrScanner");
  }

  @override
  Future<void> stopQrScanner() async {
    _isQrCodeScannerOn = false;

    await methodChannel.invokeMethod("stopQrScanner");
  }
}
