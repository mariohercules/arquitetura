package unifor.br.arquitetura;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import unifor.br.base.ArquiteturaBase;
import unifor.br.base.BarramentoBase;
import unifor.br.base.EscolhaOperacao;
import unifor.br.base.SinalBase;

public class CPU implements Runnable {

	Registradores registradores;
	Cache cache;
	Barramento barramento;

	private final Semaphore semaforoControleList = new Semaphore(0, true);
	private final Semaphore semaforoDadoList = new Semaphore(0, true);

	List<Sinal> controleList;
	List<Sinal> enderecoList;
	List<Sinal> dadoList;

	List<Integer> loop;

	boolean start = true;
	boolean stop = true;

	public CPU(Barramento barramento) {
		super();
		this.registradores = new Registradores();
		this.barramento = barramento;
		new Thread(this.cache = new Cache(this)).start();

		this.controleList = new ArrayList<>();
		this.enderecoList = new ArrayList<>();
		this.dadoList = new ArrayList<>();

		this.loop = new ArrayList<>();
	}

	public void addControleList(Sinal sinal) {
		this.controleList.add(sinal);
		semaforoControleList.release();
		System.out.println("CPU recebeu sinal de controle.");
	}

	public void addEnderecoList(Sinal sinal) {
		this.enderecoList.add(sinal);
	}

	public void addDadoList(Sinal sinal) {
		this.dadoList.add(sinal);
		semaforoDadoList.release();
		System.out.println("CPU recebeu sinal de dado.");
	}

	@Override
	public void run() {

		System.out.println("CPU iniciou");
		while (start) {

			if (semaforoControleList.tryAcquire()) {
				Sinal sinal = controleList.get(0);
				if (sinal.origem.equals(ArquiteturaBase.ES)) {
					System.out.println("CPU iniciar execução.");
					execute();
				}
				controleList.remove(0);
			}

		}

	}

	public int getSeq() {

		int ret;

		Sinal sinal = new Sinal();
		sinal.origem = ArquiteturaBase.CPU;
		sinal.destino = ArquiteturaBase.RAM;
		sinal.tipoBarramento = BarramentoBase.CONTROLE;
		sinal.tipoSinalControle = SinalBase.SEQ;
		barramento.addControleList(sinal);

		while (true) {
			if (semaforoDadoList.tryAcquire()) {
				ret = Parser.parseToInt(dadoList.get(0).instrucao);
				dadoList.remove(0);
				break;
			}
		}

		return ret;
	}

	public byte[] getEndereco(int id) {

		byte[] ret;

		Sinal sinal = new Sinal();
		sinal.origem = ArquiteturaBase.CPU;
		sinal.destino = ArquiteturaBase.RAM;
		sinal.tipoBarramento = BarramentoBase.CONTROLE;
		sinal.tipoSinalControle = SinalBase.ENDG;
		sinal.instrucao = Parser.enviarParaBytes(id);

		barramento.addControleList(sinal);

		while (true) {
			if (semaforoDadoList.tryAcquire()) {
				ret = dadoList.get(0).instrucao;
				dadoList.remove(0);
				break;
			}
		}

		return ret;
	}

	public void setEndereco(int id, byte[] arr) {

		if (cache.setInstrucao(id, arr)) {
			Main.hit++;
		} else {
			guardarEnderecoRam(id, arr);
			Main.miss++;
		}

	}

	public void guardarEnderecoRam(int id, byte[] arr) {

		Sinal sinal = new Sinal();
		sinal.origem = ArquiteturaBase.CPU;
		sinal.destino = ArquiteturaBase.RAM;
		sinal.tipoBarramento = BarramentoBase.DADO;
		sinal.tipoSinalControle = SinalBase.SETE;
		sinal.instrucao = arr;
		sinal.endereco = "0x" + id;

		barramento.addDadoList(sinal);

	}

	public void setClear(int id, byte[] arr) {

		Sinal sinal = new Sinal();
		sinal.origem = ArquiteturaBase.CPU;
		sinal.destino = ArquiteturaBase.RAM;
		sinal.tipoBarramento = BarramentoBase.DADO;
		sinal.tipoSinalControle = SinalBase.CLEA;
		sinal.instrucao = arr;
		sinal.endereco = "0x" + id;

		barramento.addDadoList(sinal);

	}

	public byte[] getInstrucao(int id) {

		byte[] ret;

		ret = cache.pegaInstrucao(id);

		if (ret != null) {
			Main.hit++;
		} else {
			ret = getInstrucaoRam(id);
			Main.miss++;
		}

		return ret;
	}

	public byte[] getInstrucaoRam(int id) {

		byte[] ret;

		Sinal sinal = new Sinal();
		sinal.origem = ArquiteturaBase.CPU;
		sinal.destino = ArquiteturaBase.RAM;
		sinal.tipoBarramento = BarramentoBase.CONTROLE;
		sinal.tipoSinalControle = SinalBase.INST;
		sinal.instrucao = Parser.enviarParaBytes(id);

		barramento.addControleList(sinal);

		while (true) {
			if (semaforoDadoList.tryAcquire()) {
				ret = dadoList.get(0).instrucao;
				dadoList.remove(0);
				break;
			}
		}

		return ret;
	}

	@SuppressWarnings("unused")
	public synchronized boolean execute() {

		try {

			int seq = getSeq() / 16;

			// for (int i = 0; i < 100; i++) {
			while (stop) {

				Instrucao instrucao = Instrucao.parse(getInstrucao(registradores.ci));
				System.out.println(
						instrucao.operacao.getDescricao() + " , " + instrucao.param_1 + " , " + instrucao.param_2);
				if (instrucao.typeOperacao().equals("MOV")) {
					mov(instrucao);
				} else if (instrucao.typeOperacao().equals("ADD")) {
					add(instrucao);
				} else if (instrucao.typeOperacao().equals("IMUL")) {
					imul(instrucao);
				} else if (instrucao.typeOperacao().equals("INC")) {
					inc(instrucao);
				} else if (instrucao.typeOperacao().equals("CLEAR")) {
					clear(instrucao);
				} else if (instrucao.typeOperacao().equals("JUMP")) {

					jump(instrucao.param_1, instrucao.param_2, instrucao.param_3);

				} else if (instrucao.typeOperacao().equals("LOOP")) {
					this.loop.add(registradores.ci);
				}

				print();
				registradores.ci++;

				Thread.sleep(Main.sleep);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Hit : " + Main.hit);
		System.out.println("Miss : " + Main.miss);

		long endTime = System.currentTimeMillis();

		long totalTime = endTime - Main.tempoDeInicioEmMilliSegundos;

		NumberFormat formatter = new DecimalFormat("#0.00000");
		System.out.print("Execução " + formatter.format((totalTime) / 1000d) + " segundos");

		// System.out.println(totalTime);

		System.exit(1);
		return true;
	}

	private boolean jump(String param_1, String param_2, String param_3) {

		int pa1 = 0;
		if (Instrucao.type(param_1).equals("REG")) {
			pa1 = Integer.parseInt(registradores.getReg(param_1));
		} else {
			pa1 = Integer.parseInt(param_1);
		}

		int pa2 = Integer.parseInt(param_3);

		boolean jump = true;

		if (param_2.equals(">")) {
			if (pa1 > pa2) {
				jump = false;
			}
		}
		if (param_2.equals("<")) {
			if (pa1 < pa2) {
				jump = false;
			}
		}
		if (param_2.equals(">=")) {

			if (pa1 >= pa2) {
				jump = false;
			}
		}
		if (param_2.equals("<=")) {

			if (pa1 <= pa2) {
				jump = false;
			}
		}
		if (param_2.equals("==")) {
			if (pa1 == pa2) {
				jump = false;
			}

		}
		if (param_2.equals("!=")) {
			if (pa1 != pa2) {
				jump = false;
			}
		}

		if (!jump) {
			registradores.ci = this.loop.get(loop.size() - 1) - 1;
		}

		return jump;
	}

	private void clear(Instrucao instrucao) {

		if (instrucao.operacao.equals(EscolhaOperacao.CLEAR_END)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int size = 0;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			for (int i = 0; i < Main.memoriaUtilizada; i++) {

				size++;
				if (size <= Main.valorDaTransferencia) {
					outputStream.write((byte) -1);
				} else {

					setClear(end1, outputStream.toByteArray());
					outputStream.reset();
					end1 = end1 + size - 1;
					size = 0;
				}
			}

			if (size > 0 && outputStream.size() > 0) {
				setClear(end1, outputStream.toByteArray());
			}

		}

	}

	@SuppressWarnings("unused")
	private void loop(int ci) {

		int ciIni = ci;

		boolean run = true;

		try {

			while (run) {

				Instrucao instrucao = Instrucao.parse(getInstrucao(registradores.ci));

				if (instrucao.typeOperacao().equals("MOV")) {
					mov(instrucao);
				} else if (instrucao.typeOperacao().equals("ADD")) {
					add(instrucao);
				} else if (instrucao.typeOperacao().equals("IMUL")) {
					imul(instrucao);
				} else if (instrucao.typeOperacao().equals("INC")) {
					inc(instrucao);
				} else if (instrucao.typeOperacao().equals("LOOP")) {
					loop(registradores.ci);
				}

				print();
				// registradores.ci++;

				Thread.sleep(Main.sleep);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void add(Instrucao instrucao) {

		if (instrucao.operacao.equals(EscolhaOperacao.ADD_REG_VAL)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(instrucao.param_2);
			registradores.setReg(instrucao.param_1, String.valueOf(val1 + val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.ADD_REG_REG)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1 + val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.ADD_REG_END)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			int val2 = Parser.parseToInt(getEndereco(end2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1 + val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.ADD_END_END)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val1 = Parser.parseToInt(getEndereco(end1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			int val2 = Parser.parseToInt(getEndereco(end2));

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val1 + val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.ADD_END_VAL)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val1 = Parser.parseToInt(getEndereco(end1));
			int val2 = Integer.parseInt(instrucao.param_2);

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val1 + val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.ADD_END_REG)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val1 = Parser.parseToInt(getEndereco(end1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val1 + val2));
		}

	}

	public void imul(Instrucao instrucao) {
		if (instrucao.operacao.equals(EscolhaOperacao.IMUL_REG_REG)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1 * val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.IMUL_REG_END)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			int val2 = Parser.parseToInt(getEndereco(end2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1 * val2));

		} else if (instrucao.operacao.equals(EscolhaOperacao.IMUL_REG_REG_VAL)) {

			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			int val3 = Integer.parseInt(instrucao.param_3);
			registradores.setReg(instrucao.param_1, String.valueOf(val2 * val3));

		} else if (instrucao.operacao.equals(EscolhaOperacao.IMUL_REG_END_VAL)) {

			int end2 = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			int val2 = Parser.parseToInt(getEndereco(end2));
			int val3 = Integer.parseInt(instrucao.param_3);
			registradores.setReg(instrucao.param_1, String.valueOf(val2 * val3));

		}
	}

	public void mov(Instrucao instrucao) {

		if (instrucao.operacao.equals(EscolhaOperacao.MOV_REG_VAL)) {
			registradores.setReg(instrucao.param_1, instrucao.param_2);
		} else if (instrucao.operacao.equals(EscolhaOperacao.MOV_REG_REG)) {
			registradores.setReg(instrucao.param_1, registradores.getReg(instrucao.param_2));
		} else if (instrucao.operacao.equals(EscolhaOperacao.MOV_REG_END)) {

			int end = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			int val = Parser.parseToInt(getEndereco(end));
			registradores.setReg(instrucao.param_1, String.valueOf(val));

		} else if (instrucao.operacao.equals(EscolhaOperacao.MOV_END_END)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x", ""));
			byte[] val = getEndereco(end2);

			barramento.ram.setEndereco(end1, val);

		} else if (instrucao.operacao.equals(EscolhaOperacao.MOV_END_VAL)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val = Integer.parseInt(instrucao.param_2.replace("0x", ""));

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val));

		} else if (instrucao.operacao.equals(EscolhaOperacao.MOV_END_REG)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val = Integer.parseInt(registradores.getReg(instrucao.param_2));

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val));
		}
	}

	public void inc(Instrucao instrucao) {

		if (instrucao.operacao.equals(EscolhaOperacao.INC_REG)) {

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			registradores.setReg(instrucao.param_1, String.valueOf(val1 + 1));

		} else if (instrucao.operacao.equals(EscolhaOperacao.INC_END)) {

			int end1 = Integer.parseInt(instrucao.param_1.replace("0x", ""));
			int val1 = Parser.parseToInt(getEndereco(end1));

			barramento.ram.setEndereco(end1, Parser.enviarParaBytes(val1 + 1));

		}
	}

	public void print() {

		int loop = getSeq() / 16;
		if (registradores.ci < loop) {

			int count = 0;

			System.out.println("\nMemoria Instrucao\n");
			System.out.print("|--------------------------------");
			System.out.print("|-------------------------------");
			System.out.print("|-------------------------------");
			System.out.print("|-------------------------------|\n");
			System.out.print("|            Operacao            ");
			System.out.print("|          Parametro 1          ");
			System.out.print("|          Parametro 2          ");
			System.out.print("|          Parametro 3          |\n");
			System.out.print("|--------------------------------");
			System.out.print("|-------------------------------");
			System.out.print("|-------------------------------");
			System.out.print("|-------------------------------|\n");
			for (int i = 0; i < loop; i++) {

				System.out.print("| ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.println(barramento.ram.valorDaMemoria[count++] + "\t | ");
				System.out.print("|--------------------------------");
				System.out.print("|-------------------------------");
				System.out.print("|-------------------------------");
				System.out.print("|-------------------------------|\n");
			}

			System.out.println("\nMemoria Valores\n");

			for (int i = 0; i < 80;) {

				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.print((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)] + "\t");
				System.out.println((1600 + (i++)) + " : " + barramento.ram.valorDaMemoria[(1600 + i)]);

			}

			Instrucao instrucao = Instrucao.parse(getInstrucao(registradores.ci));
			System.out.print("\nRegistrador\t\t\t\t\t\t\t");
			System.out.println("Instrucao = " + instrucao.toString());

			System.out.print("A : " + registradores.a + "\t");
			System.out.print("B : " + registradores.b + "\t");
			System.out.print("C : " + registradores.c + "\t");
			System.out.print("D : " + registradores.d + "\t");
			System.out.println("CI : " + registradores.ci);
		} else {

			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			System.out.print("|--------------------------------");
			System.out.print("--------------------------------");
			System.out.print("--------------------------------");
			System.out.print("--------------------------------|\n");
			System.out.print("|                               ");
			System.out.print("                                ");
			System.out.print("                                ");
			System.out.print("                                 |\n");
			System.out.print("|                               ");
			System.out.print("                                ");
			System.out.print("                                ");
			System.out.print("                                 |\n");
			System.out.print("|                               ");
			System.out.print("                           BUILD");
			System.out.print(" SUCESS                          ");
			System.out.print("                                |\n");
			System.out.print("|                               ");
			System.out.print("                                ");
			System.out.print("                                ");
			System.out.print("                                 |\n");
			System.out.print("|                               ");
			System.out.print("                                ");
			System.out.print("                                ");
			System.out.print("                                 |\n");
			System.out.print("|--------------------------------");
			System.out.print("--------------------------------");
			System.out.print("--------------------------------");
			System.out.print("--------------------------------|\n");

			stop = false;

		}

	}

}