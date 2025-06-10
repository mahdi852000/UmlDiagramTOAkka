package com.iwu.connector;

public record ScannerBarcodeCommand(String barcode) implements ConnectorCommand  {}