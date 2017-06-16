package unifor.br.arquitetura;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unifor.br.base.ArquiteturaBase;
import unifor.br.base.BarramentoBase;
import unifor.br.base.SinalBase;

public class ModuloES implements Runnable {

	String nome = System.getProperty("user.dir") + "/src/code.txt";

	List<String> instrucoes;
	List<Instrucao> instrucoesList;
	Barramento barramento;

	int inst = 1;

	private final Semaphore semaforoControleList = new Semaphore(0, true);
	private final Semaphore semaforoEnderecoList = new Semaphore(0, true);
	private final Semaphore semaforoDadoList = new Semaphore(0, true);

	List<Sinal> controleList;
	List<Sinal> enderecoList;
	List<Sinal> dadoList;
	private BufferedReader lerArq;

	public ModuloES(Barramento barramento) {
		super();
		instrucoes = new ArrayList<>();
		instrucoesList = new ArrayList<>();
		this.barramento = barramento;
		this.controleList = new ArrayList<>();
		this.enderecoList = new ArrayList<>();
		this.dadoList = new ArrayList<>();

	}

	public void addControleList(Sinal sinal) {
		this.controleList.add(sinal);
		semaforoControleList.release();
	}

	public void addEnderecoList(Sinal sinal) {
		this.enderecoList.add(sinal);
		semaforoEnderecoList.release(2);
	}

	public void addDadoList(Sinal sinal) {
		this.dadoList.add(sinal);
		semaforoDadoList.release();
	}

	public void getEnderecoRam() {

		Sinal sinalCont = new Sinal();
		sinalCont.tipoBarramento = BarramentoBase.CONTROLE;
		sinalCont.tipoSinalControle = SinalBase.ENDG;
		sinalCont.origem = ArquiteturaBase.ES;
		sinalCont.destino = ArquiteturaBase.RAM;
		barramento.addControleList(sinalCont);

	}

	public void enviarInicioCPU() {

		Sinal sinalCont = new Sinal();
		sinalCont.tipoBarramento = BarramentoBase.CONTROLE;
		sinalCont.origem = ArquiteturaBase.ES;
		sinalCont.destino = ArquiteturaBase.CPU;
		System.out.println("E/S enviou sinal de controle para cpu.");
		barramento.addControleList(sinalCont);

	}

	public void enviarInstrucaoRam(Instrucao instrucao) {

		if (semaforoEnderecoList.tryAcquire()) {
			Sinal sinal = enderecoList.get(0);
			if (sinal.tipoBarramento.equals(BarramentoBase.ENDERECO) && sinal.origem.equals(ArquiteturaBase.RAM)
					&& sinal.endereco != "") {
				Sinal sinalAux = new Sinal();
				sinalAux.tipoBarramento = BarramentoBase.DADO;
				sinalAux.origem = ArquiteturaBase.ES;
				sinalAux.destino = ArquiteturaBase.RAM;
				sinalAux.endereco = sinal.endereco;
				sinalAux.instrucao = instrucao.toBytes();
				barramento.addDadoList(sinalAux);
			}
		}

	}

	public void enviarInstrucaoRam(List<Instrucao> instrucoes) {

		if (semaforoEnderecoList.tryAcquire()) {

			Sinal sinal = enderecoList.get(0);
			if (sinal.tipoBarramento.equals(BarramentoBase.ENDERECO) && sinal.origem.equals(ArquiteturaBase.RAM)
					&& sinal.endereco != "") {

				Sinal sinalAux = new Sinal();
				sinalAux.tipoBarramento = BarramentoBase.DADO;
				sinalAux.origem = ArquiteturaBase.ES;
				sinalAux.destino = ArquiteturaBase.RAM;
				sinalAux.endereco = sinal.endereco;

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				for (Instrucao instru : instrucoes) {
					try {
						outputStream.write(instru.toBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				sinalAux.instrucao = outputStream.toByteArray();
				barramento.addDadoList(sinalAux);

			}
		}

	}

	@Override
	public void run() {

		try {

			FileReader arq = new FileReader(nome);
			lerArq = new BufferedReader(arq);

			String regex = "(\\w*)\\s*([ABCDabcd]|0x[\\d]+|[\\d]+)+\\s*(?:,\\s+)?([ABCDabcd]|0x[\\d]+|[\\d]+)*\\s*(?:,\\s*)?([ABCDabcd]|0x[\\d]+|[\\d]+)?";
			String regexL = "^(.*)\\s*\\?\\s*(jump)\\s*(\\d+)\\s*\\:\\s*(\\d+)?";

			Pattern pattern = Pattern.compile(regex);
			Pattern patternL = Pattern.compile(regexL);

			String linha = lerArq.readLine();
			while (linha != null) {

				Matcher matcher = null;
				if (linha.toLowerCase().contains("jump")) {
					matcher = patternL.matcher(linha);
					if (matcher.matches()) {
						Instrucao instrucao = new Instrucao(matcher.group(1), matcher.group(3), matcher.group(4));
						this.instrucoes.add(linha);
						this.instrucoesList.add(instrucao);
					}
				} else if (linha.toLowerCase().contains("loop")) {

					String[] s = linha.split(" ");
					Instrucao instrucao = new Instrucao(s[0], s[1]);
					this.instrucoes.add(linha);
					this.instrucoesList.add(instrucao);

				} else {

					matcher = pattern.matcher(linha);
					if (matcher.matches()) {
						Instrucao instrucao = new Instrucao(matcher.group(1), matcher.group(2), matcher.group(3),
								matcher.group(4));
						this.instrucoes.add(linha);
						this.instrucoesList.add(instrucao);
					}
				}

				linha = lerArq.readLine();
			}

			enviarInstrucoes();
			System.out.println("E/S vai iniciar a cpu.");
			enviarInicioCPU();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void enviarInstrucaoMemoria(List<Instrucao> instrucoes) {
		getEnderecoRam();

		while (true) {
			if (semaforoEnderecoList.tryAcquire()) {
				enviarInstrucaoRam(instrucoes);
				enderecoList.remove(0);
				break;
			}
		}

	}

	private void enviarInstrucoes() {

		int size = 0;
		List<Instrucao> auxList = new ArrayList<>();

		for (Instrucao instrucao : instrucoesList) {

			size = size + 16;
			if (size <= Main.valorDaTransferencia) {
				auxList.add(instrucao);
			} else {

				size = 0;
				enviarInstrucaoMemoria(auxList);
				auxList = new ArrayList<>();
			}

		}

		if (size > 0 && !auxList.isEmpty()) {
			enviarInstrucaoMemoria(auxList);
		}

		System.out.println("E/S enviou todas instrucoes para RAM");

	}

}
