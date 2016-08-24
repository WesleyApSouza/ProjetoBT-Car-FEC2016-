package wesleysouza.controlecar;

import java.io.IOException;

public interface IenviaDados {
    void enviaDados(int dado,String chave)throws IOException;
    void enviaDados(String dado)throws IOException;
}
