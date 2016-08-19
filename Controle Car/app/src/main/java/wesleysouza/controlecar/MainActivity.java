package wesleysouza.controlecar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SeekBar barFrente;
    private RadioButton rbFarol;

    private TextView txtFD;
    private Button btnConnect;
    private Button btnDesconectar;
    private TextView txtTest;
    private TextView txtIndicaPos;
    private BluetoothAdapter meuBluetooth = null;
    private static final int SOLICITA_ATIVAÇÃO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private boolean conexao = false;
    private static String MAC = null;
    private boolean isBtConnected = false;
    private ConectarDispositivo cd = null;
    int teste = 0;
    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTest = (TextView) findViewById(R.id.txtTestBar);
        txtIndicaPos = (TextView) findViewById(R.id.btnTxtIndicaPos);
        txtFD = (TextView) findViewById(R.id.txtFD);
        rbFarol = (RadioButton) findViewById(R.id.rbFarol);

        barFrente = (SeekBar) findViewById(R.id.barFrente);
        final SeekBar barDirection = (SeekBar) findViewById(R.id.barDirection);

        Button btnListaConexao = (Button) findViewById(R.id.btnListaConexao);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnDesconectar = (Button) findViewById(R.id.btnDesconectar);
        meuBluetooth = BluetoothAdapter.getDefaultAdapter();


        if (meuBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Seu dispositivo não possui bluetooth", Toast.LENGTH_LONG).show();
        } else if (!meuBluetooth.isEnabled()) {
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, SOLICITA_ATIVAÇÃO);
        }

        rbFarol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teste == 1){
                    rbFarol.setChecked(false);
                    enviaDados("FF");
                    teste = 0;
                }else
                {
                    enviaDados("FT");
                    rbFarol.setChecked(true);
                    teste = 1;
                }

            }
        });

        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cd.close();
                    btnDesconectar.setVisibility(View.INVISIBLE);
                    btnConnect.setVisibility(View.VISIBLE);
                    reseta();
                    rbFarol.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Desconectado com sucesso",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"ERRO; Ao desconectar com o Modulo Bluetooth",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(conexao) {
                        if (!isBtConnected) {
                            cd = new ConectarDispositivo(meuBluetooth,MAC);
                            cd.conectar();
                            isBtConnected = true;
                            btnConnect.setVisibility(View.INVISIBLE);
                            btnDesconectar.setVisibility(View.VISIBLE);
                            rbFarol.setEnabled(true);
                            Toast.makeText(getApplicationContext(),"Conectado com Sucesso!",Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Selecione um dispositivo!!",Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(),"ERRO Conexao",Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            assert btnListaConexao != null;
            btnListaConexao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent abreLista = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(abreLista, SOLICITA_CONEXAO);

                }
            });
        }catch (NullPointerException ignored){

        }

        barFrente.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 127) {
                    double conta = (progress - 127.5) *2;
                    enviaDados((int)conta, "D");
                    txtFD.setText("Acelera");
                }else if (progress < 127){
                    int conta = 255 - (progress * 2);
                    enviaDados(conta, "R");
                    txtFD.setText("RÉ");
                }else{
                    txtFD.setText("Parado");
                    enviaDados(0,"D");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() > 127) {
                    int conta = (seekBar.getProgress() - 127) *2;
                    //seekBar.setProgress(conta);
                    enviaDados(conta, "D");
                    txtFD.setText("Acelera");
                }else if (seekBar.getProgress() < 127){
                    int conta = 255 - (seekBar.getProgress() * 2);
                    //seekBar.setProgress(conta);
                    enviaDados(conta, "R");
                    txtFD.setText("RÉ");
                }else{
                    enviaDados(0,"D");
                    txtFD.setText("Parado");
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(127);
                txtFD.setText("Parado");
                enviaDados(0,"D");
            }
        });
        assert barDirection != null;
        barDirection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                enviaDados(seekBar.getProgress(),"V");
                mudaDirecao(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                enviaDados(seekBar.getProgress(),"V");
                mudaDirecao(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(90);
                enviaDados(seekBar.getProgress(),"V");
                mudaDirecao(seekBar);
            }

        });
    }

    private void reseta() {
        conexao = false;
        isBtConnected = false;
        MAC = null;
        txtTest.setText("Dispositivo: nenhum");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SOLICITA_ATIVAÇÃO:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth Não ativado, app será finalizado", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MEC);
                    conexao = true;
                    txtTest.setText("Dispositivo: "+MAC);

                    Toast.makeText(getApplicationContext(), "MAC FINAL:" + MAC, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "FALHA AO OBTER O MAC", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void mudaDirecao(SeekBar seekBar){
        if(seekBar.getProgress() > 90){
            txtIndicaPos.setText("Direita");
        }else if(seekBar.getProgress()< 90){
            txtIndicaPos.setText("Esquerda");
        }else{
            txtIndicaPos.setText("Frente");
        }
    }
    private void enviaDados(String dado){
        try {
            cd.enviarDado(dado.getBytes());
        }catch (IOException e){
            Toast.makeText(getApplicationContext(),"Erro, ao enviar dados via Bluetooth",Toast.LENGTH_LONG).show();
        }


    }
    private void enviaDados(int dado,String chave){
        try{
            if(isBtConnected)
                cd.enviarDado(String.valueOf(chave+dado).getBytes());

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Erro, ao enviar dados via Bluetooth",Toast.LENGTH_LONG).show();
        }

    }
}

