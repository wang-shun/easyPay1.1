package com.acewill.chikenprintlibrary.printer;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.acewill.chikenprintlibrary.printer.vendor.CommandPrinter;

import java.io.IOException;
import java.util.UUID;


/**
 * 容大蓝牙打印机
 * Created by Acewill on 2016/8/19.
 */
public class BluetoothPrinter extends CommandPrinter {
    BluetoothDevice device;
    private BluetoothSocket bluetoothSocket = null;

    public BluetoothPrinter(BluetoothDevice device, PrinterWidth width) {
        super(width == PrinterWidth.WIDTH_80MM ? 42 : 32);
        this.device = device;
    }

    @Override
    public void init() throws IOException {
            bluetoothSocket = this.device.createRfcommSocketToServiceRecord(UUID.randomUUID());
            bluetoothSocket.connect();
            super.setOutputStream(bluetoothSocket.getOutputStream());
    }

    @Override
    public void close() throws IOException {
        super.close();
        bluetoothSocket.close();
    }

}
