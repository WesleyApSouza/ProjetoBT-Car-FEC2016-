package wesleysouza.controlecar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ConectarDispositivo {
    private BluetoothSocket meuSocket = null;
    private BluetoothAdapter meuBluetooth = null;
    private static String MAC = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConectarDispositivo(BluetoothAdapter meuBluetooth, String MAC) throws IOException {
        this.meuBluetooth = meuBluetooth;
        ConectarDispositivo.MAC = MAC;
        preparaDevice();
    }
    private void preparaDevice() throws IOException {
        BluetoothDevice dispositivo = meuBluetooth.getRemoteDevice(MAC);
        meuSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    public void close() throws IOException {
        meuSocket.close();

    }
    public void conectar() throws IOException {
        meuSocket.connect();
    }
    public void enviarDado(byte[] valor) throws IOException {
        meuSocket.getOutputStream().write(valor);
    }

}
