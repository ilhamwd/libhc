import 'package:libhc/models/rfid_result.dart';
import 'package:libhc/physical_button_event.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'libhc_method_channel.dart';

abstract class LibhcPlatform extends PlatformInterface {
  LibhcPlatform() : super(token: _token);

  static final Object _token = Object();

  static LibhcPlatform _instance = MethodChannelLibhc();

  static LibhcPlatform get instance => _instance;

  static set instance(LibhcPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Stream<RfidResult> get rfidResult;

  Stream<PhysicalButtonEvent> get physicalButton;

  Stream<String> get qrCodeResult;

  Future<void> init();

  Future<bool?> startInventoryReader();

  Future<bool?> stopInventoryReader();

  Future<void> startQrScanner();

  Future<void> stopQrScanner();
}
