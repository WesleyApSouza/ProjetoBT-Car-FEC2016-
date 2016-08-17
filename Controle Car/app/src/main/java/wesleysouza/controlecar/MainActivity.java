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
    private SeekBar barRe;
    private RadioButton rbFarol;
    private Button btnParado;
    private Button btnFrente;
    private Button btnRe;
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

        rbFarol = (RadioButton) findViewById(R.id.rbFarol);

        barFrente = (SeekBar) findViewById(R.id.barFrente);
        barRe = (SeekBar) findViewById(R.id.barRe);
        final SeekBar barDirection = (SeekBar) findViewById(R.id.barDirection);

        btnParado = (Button) findViewById(R.id.btnParado);
        btnRe = (Button) findViewById(R.id.btnRe);
        btnFrente = (Button) findViewById(R.id.btnFrente);
        Button btnListaConexao = (Button) findViewById(R.id.btnListaConexao);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnDesconectar = (Button) findViewById(R.id.btnDesconectar);
        meuBluetooth = BluetoothAdapter.getDefaultAdapter();
        barRe.setEnabled(false);
        barFrente.setEnabled(false);


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

                enviaDados(seekBar,"D");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                enviaDados(seekBar,"D");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
                enviaDados(seekBar,"D");
            }
        });
        barRe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                enviaDados(seekBar,"R");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                enviaDados(seekBar,"R");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
                enviaDados(seekBar,"R");
            }
        });
        assert barDirection != null;
        barDirection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                enviaDados(seekBar,"V");
                mudaDirecao(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                enviaDados(seekBar,"V");
                mudaDirecao(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(90);
                enviaDados(seekBar,"V");
                mudaDirecao(seekBar);
            }

        });
        btnParado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnParado.setBackgroundResource(R.drawable.botaoclick);
                btnFrente.setBackgroundResource(R.drawable.botao);
                btnRe.setBackgroundResource(R.drawable.botao);
                barRe.setEnabled(false);
                barFrente.setEnabled(false);
            }
        });
        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnParado.setBackgroundResource(R.drawable.botao);
                btnFrente.setBackgroundResource(R.drawable.botao);
                btnRe.setBackgroundResource(R.drawable.botaoclick);

                barRe.setEnabled(true);
                barRe.setVisibility(View.VISIBLE);
                barFrente.setVisibility(View.INVISIBLE);
            }
        });
        btnFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnParado.setBackgroundResource(R.drawable.botao);
                btnFrente.setBackgroundResource(R.drawable.botaoclick);
                btnRe.setBackgroundResource(R.drawable.botao);

                barFrente.setEnabled(true);
                barRe.setVisibility(View.INVISIBLE);
                barFrente.setVisibility(View.VISIBLE);
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
    private void enviaDados(SeekBar seekBar,String chave){
        try{
            if(isBtConnected)
                cd.enviarDado(String.valueOf(chave+seekBar.getProgress()).getBytes());

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Erro, ao enviar dados via Bluetooth",Toast.LENGTH_LONG).show();
        }

    }
}

