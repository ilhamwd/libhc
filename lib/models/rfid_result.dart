class RfidResult {
  final String ecp;
  final int rssi;

  RfidResult.fromJson(Map<Object?, Object?> json)
      : ecp = json['ecp']?.toString() ?? "N/A",
        rssi = json['rssi'] as int? ?? -1;
}
