import 'package:libhc/models/rfid_result.dart';
import 'package:libhc/physical_button_event.dart';

import 'libhc_platform_interface.dart';

export 'package:libhc/models/rfid_result.dart';
export 'package:libhc/physical_button_event.dart';

class Libhc {
  Stream<RfidResult> get rfidResultStream => LibhcPlatform.instance.rfidResult;

  Stream<String> get qrCodeResultStream => LibhcPlatform.instance.qrCodeResult;

  Stream<PhysicalButtonEvent> get physicalButton =>
      LibhcPlatform.instance.physicalButton;

  Future<void> init() => LibhcPlatform.instance.init();

  Future<bool?> startInventoryReader() =>
      LibhcPlatform.instance.startInventoryReader();

  Future<bool?> stopInventoryReader() =>
      LibhcPlatform.instance.stopInventoryReader();

  Future<void> startQrScanner() => LibhcPlatform.instance.startQrScanner();

  Future<void> stopQrScanner() => LibhcPlatform.instance.stopQrScanner();
}
