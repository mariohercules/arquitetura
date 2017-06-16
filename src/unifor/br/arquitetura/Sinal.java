package unifor.br.arquitetura;
import unifor.br.base.ArquiteturaBase;
import unifor.br.base.BarramentoBase;
import unifor.br.base.SinalBase;

public class Sinal {

	ArquiteturaBase origem;
	ArquiteturaBase destino;
	byte[] instrucao;
	BarramentoBase tipoBarramento;
	SinalBase tipoSinalControle;
	String endereco;

	public int getEndInt() {
		return Integer.parseInt(endereco.replace("0x", ""));
	}

}
