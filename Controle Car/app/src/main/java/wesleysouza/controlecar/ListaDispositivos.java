package wesleysouza.controlecar;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ListaDispositivos extends ListActivity {
    static String ENDERECO_MEC = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        BluetoothAdapter meuBluetoth = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> dispositivosPareados = meuBluetoth.getBondedDevices();
        if(dispositivosPareados.size() > 0){
            for(BluetoothDevice dispositivo:dispositivosPareados){
                String nomeBT = dispositivo.getName();
                String macBT = dispositivo.getAddress();
                ArrayBluetooth.add(nomeBT+"/n"+macBT);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String infoGeral = ((TextView) v).getText().toString();
        String endMAC =infoGeral.substring(infoGeral.length() - 17);
        Intent retornaMAC = new Intent();
        retornaMAC.putExtra(ENDERECO_MEC,endMAC);
        setResult(RESULT_OK,retornaMAC);
        finish();
    }
}
