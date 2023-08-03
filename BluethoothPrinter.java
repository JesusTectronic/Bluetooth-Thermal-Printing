package tprinter.connection.android;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import juno.io.IOUtils;

public class BluethoothPrinter {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public final String address;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket = null;
    private InputStream in;
    private OutputStream out;

    public BluethoothPrinter(String address) {
        this.address = address;
    }

    @SuppressLint("MissingPermission") // Manifest.permission.BLUETOOTH_CONNECT
    public static BluetoothSocket secureSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @SuppressLint("MissingPermission") // Manifest.permission.BLUETOOTH_CONNECT
    public static BluetoothSocket insecureSocket(BluetoothDevice device) throws IOException {
        return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    public static BluetoothSocket socket(BluetoothDevice device) throws Exception {
        Class<?> clazz = device.getClass();
        Class<?>[] paramTypes = new Class[]{Integer.TYPE};
        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
        Object[] params = new Object[]{1};
        return (BluetoothSocket)m.invoke(device, params);
    }

    @SuppressLint({"MissingPermission"})
    public void open(int timeout) throws IOException {
        if (this.mBluetoothAdapter == null) {
            throw new IOException("mBluetoothAdapter == null");

        } else {
            if (!this.mBluetoothAdapter.isEnabled()) {
                this.mBluetoothAdapter.enable();
                delay((long)(timeout / 8));

                if (!this.mBluetoothAdapter.isEnabled()) {
                    throw new IOException("Bluetooth is not enable");
                }
            }

            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(this.address);

            try {
                this.socket = secureSocket(device);
                this.mBluetoothAdapter.cancelDiscovery();
                this.socket.connect();

            } catch (Exception secureError) {
                try {
                    this.socket = insecureSocket(device);
                    this.mBluetoothAdapter.cancelDiscovery();
                    this.socket.connect();

                } catch (Exception insecureError) {
                    //throw new IOException("Could not create Insecure RFComm Connection " + device.getAddress(), var3);
                    try {
                        this.socket = socket(device);
                        Thread.sleep(500L);
                        this.socket.connect();

                    } catch (Exception socketError) {
                        throw new IOException("Could not connect to '" + this.address + "' device", socketError);
                    }
                }
            }

            this.out = this.socket.getOutputStream();
            this.in = this.socket.getInputStream();
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public void close() {
        if (this.socket != null) {
            try {
                IOUtils.closeQuietly(this.out);
                IOUtils.closeQuietly(this.in);
                this.socket.close();
            } catch (IOException ignored) {
            }
        }

    }

    public void close(int timeout) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
        }
        close();
    }

    public String name() {
        return this.address;
    }

    public byte[] read() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IOUtils.copy(this.in, bytes);
        return bytes.toByteArray();
    }

    public void writeByte(int i) throws IOException {
        this.out.write(i);
    }

    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0 ; i < len ; i++) {
            writeByte(b[off + i]);
        }
    }
    
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void writeString(String str, String charset) throws IOException {
        write(str.getBytes(charset));
    }

    @SuppressLint("MissingPermission") // Manifest.permission.BLUETOOTH_CONNECT
    public static Set<BluetoothDevice> getBondedDevices() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) return new HashSet<BluetoothDevice>();
        return mBluetoothAdapter.getBondedDevices();
    }

    public static BluetoothDevice[] getBondedDevicesAsArray() {
        Set<BluetoothDevice> pairedDevices = getBondedDevices();
        return pairedDevices.toArray(new BluetoothDevice[0]);
    }
}
