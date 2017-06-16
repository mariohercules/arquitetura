package unifor.br.arquitetura;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import unifor.br.base.ArquiteturaBase;
import unifor.br.base.BarramentoBase;
import unifor.br.base.SinalBase;

public class RAM implements Runnable {

	static final int TAMANHO_RAM = 16;
	static int OFFSET = (16 * 100);

	int valorDaMemoriaDoIGB = Main.memoriaUtilizada + OFFSET;

	private final Semaphore semaforoControleList = new Semaphore(0, true);
	private final Semaphore semaforoDadoList = new Semaphore(0, true);

	List<Sinal> listaDeControle;
	List<Sinal> listaDeDados;

	static int valorDaSequencia = 0;
	byte[] valorDaMemoria;

	private Barramento barramento;

	public RAM(Barramento barramento) {
		super();
		this.valorDaMemoria = new byte[valorDaMemoriaDoIGB];
		this.barramento = barramento;
		this.listaDeControle = new ArrayList<>();
		this.listaDeDados = new ArrayList<>();

	}

	public void adicionarControleNaLista(Sinal sinal) {
		this.listaDeControle.add(sinal);
		semaforoControleList.release();
	}

	public void adicionarControleNaListaDeDados(Sinal sinal) {
		this.listaDeDados.add(sinal);
		semaforoDadoList.release();
	}

	public void adicionarInstrucao(byte[] instrucao) {
		for (int i = 0; i < instrucao.length; i++) {
			valorDaMemoria[valorDaSequencia + i] = instrucao[i];
		}

		valorDaSequencia = valorDaSequencia + (TAMANHO_RAM * (instrucao.length / TAMANHO_RAM));
	}

	public void adicionarInstrucao(int endereco, byte[] instrucao) {

		for (int i = 0; i < instrucao.length; i++)
			valorDaMemoria[endereco + i] = instrucao[i];

		valorDaSequencia = endereco + (TAMANHO_RAM * (instrucao.length / TAMANHO_RAM));
	}

	public byte[] pegarInstrucao(int endereco) {
		byte[] arr = new byte[16];

		if (((endereco - 1) * TAMANHO_RAM) < valorDaSequencia) {

			for (int i = 0; i < TAMANHO_RAM; i++)
				arr[i] = valorDaMemoria[((endereco - 1) * TAMANHO_RAM) + i];

		}

		return arr;
	}

	public void salvarEndereco(Sinal sinal) {

		int endereco = sinal.getEndInt();
		byte[] val = sinal.instrucao;

		for (int i = 0; i < val.length; i++)
			valorDaMemoria[(((endereco - 1) * 4) + i) + OFFSET] = val[i];

	}

	public void limparSinal(Sinal sinal) {

		int endereco = sinal.getEndInt();
		byte[] val = sinal.instrucao;

		System.out.println(endereco);
		for (int i = 0; i < val.length; i++)
			valorDaMemoria[(endereco + i) + OFFSET] = val[i];

	}

	public void setEndereco(int endereco, byte[] val) {

		for (int i = 0; i < val.length; i++) {
			valorDaMemoria[(((endereco - 1) * 4) + i) + OFFSET] = val[i];
		}

	}

	public byte[] getEndereco(int endereco) {

		byte[] arr = new byte[4];

		for (int i = 0; i < 4; i++) {
			arr[i] = valorDaMemoria[(((endereco - 1) * 4) + i) + OFFSET];
		}

		return arr;
	}

	public void send(Sinal sinal) {

		if (sinal.origem.equals(ArquiteturaBase.ES)) {
			if (sinal.tipoBarramento.equals(BarramentoBase.CONTROLE)) {
				enviarEndercoES();
			} else if (sinal.tipoBarramento.equals(BarramentoBase.DADO)) {
				adicionarInstrucao(sinal.getEndInt(), sinal.instrucao);
			}
		} else if (sinal.origem.equals(ArquiteturaBase.CPU)) {
			if (sinal.tipoBarramento.equals(BarramentoBase.CONTROLE)) {
				if (sinal.tipoSinalControle.equals(SinalBase.SEQ)) {
					enviarSeqCPU();
				} else if (sinal.tipoSinalControle.equals(SinalBase.INST)) {
					enviarInstrucaoCPU(sinal);
				} else if (sinal.tipoSinalControle.equals(SinalBase.ENDG)) {
					enviarEnderecoCPU(sinal);

				} else if (sinal.tipoSinalControle.equals(SinalBase.ENDS)) {
					salvarEndereco(sinal);
				}
			} else if (sinal.tipoBarramento.equals(BarramentoBase.DADO)) {
				if (sinal.tipoSinalControle.equals(SinalBase.SETE)) {
					salvarEndereco(sinal);
				} else if (sinal.tipoSinalControle.equals(SinalBase.CLEA)) {
					limparSinal(sinal);
				}
			}
		}

	}

	private void enviarEnderecoCPU(Sinal sinal) {

		Sinal sinalAux = new Sinal();
		sinalAux.tipoBarramento = BarramentoBase.DADO;
		sinalAux.origem = ArquiteturaBase.RAM;
		sinalAux.destino = ArquiteturaBase.CPU;
		sinalAux.instrucao = getEndereco(Parser.parseToInt(sinal.instrucao));

		barramento.addDadoList(sinalAux);

	}

	private void enviarInstrucaoCPU(Sinal sinal) {

		Sinal sinalAux = new Sinal();
		sinalAux.tipoBarramento = BarramentoBase.DADO;
		sinalAux.origem = ArquiteturaBase.RAM;
		sinalAux.destino = ArquiteturaBase.CPU;
		sinalAux.instrucao = pegarInstrucao(Parser.parseToInt(sinal.instrucao));

		barramento.addDadoList(sinalAux);

	}

	public void enviarSeqCPU() {

		Sinal sinal = new Sinal();
		sinal.tipoBarramento = BarramentoBase.DADO;
		sinal.origem = ArquiteturaBase.RAM;
		sinal.destino = ArquiteturaBase.CPU;

		sinal.instrucao = Parser.enviarParaBytes(valorDaSequencia);

		barramento.addDadoList(sinal);
		System.out.println("Ram enviou dado para CPU.");
	}

	public void enviarEndercoES() {

		Sinal sinal = new Sinal();
		sinal.tipoBarramento = BarramentoBase.ENDERECO;
		sinal.origem = ArquiteturaBase.RAM;
		sinal.destino = ArquiteturaBase.ES;
		sinal.endereco = "0x" + valorDaSequencia;

		barramento.addEnderecoList(sinal);
		System.out.println("Ram enviou endereço para E/S.");
	}

	@Override
	public void run() {

		System.out.println("RAM iniciou");
		while (true) {

			if (semaforoControleList.tryAcquire()) {
				send(listaDeControle.get(0));
				listaDeControle.remove(0);
			}
			if (semaforoDadoList.tryAcquire()) {
				send(listaDeDados.get(0));
				listaDeDados.remove(0);
			}

		}
	}

}
