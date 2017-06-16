package unifor.br.arquitetura;

import unifor.br.base.EsolhaAlgoritmo;

public class Main {

	public static void main(String[] args) {

		Barramento barramento = new Barramento();
		RAM ram = new RAM(barramento);
		CPU cpu = new CPU(barramento);
		ModuloES moduloES = new ModuloES(barramento);

		barramento.init(moduloES, ram, cpu);
		new Thread(barramento).start();

	}

	static int laguraEmBits = 8 * 8;
	static int larguraBanda = laguraEmBits / 8;
	static int valorDoClok = 10;
	static int valorDaTransferencia = larguraBanda * valorDoClok;
	static int memoriaUtilizada = 536;
	static int valorDaCache = Math.round(new Float(memoriaUtilizada * 0.1));
	static int sleep = 1000;
	static int valorDoCoolDown = 5;
	static int hit = 0;
	static int miss = 0;
	static EsolhaAlgoritmo algoritmo = EsolhaAlgoritmo.LRU;
	// static EsolhaAlgoritmo algoritmo = Algoritmo.LFU;

	static long tempoDeInicioEmMilliSegundos = System.currentTimeMillis();

}
