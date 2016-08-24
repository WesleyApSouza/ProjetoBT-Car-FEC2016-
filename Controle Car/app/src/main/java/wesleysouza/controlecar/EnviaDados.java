package wesleysouza.controlecar;

import java.io.IOException;

public class EnviaDados implements IenviaDados {

    private ConectarDispositivo cd = null;

    public EnviaDados(ConectarDispositivo cd){
            this.cd = cd;
    }
    @Override
    public void enviaDados(String dado) throws IOException {
        cd.enviarDado(dado.getBytes());
    }

    @Override
    public void enviaDados(int dado, String chave)throws IOException {
        cd.enviarDado(String.valueOf(chave+dado).getBytes());
    }
}
