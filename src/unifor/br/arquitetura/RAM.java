package unifor.br.arquitetura;

public class RAM {

	static final int SIZE = 16;

	static int OFFSET = (16 * 100);

	Barramento barramento;

	int IGB = 536870912;

	int seq = 0;

	byte[] memoria;

	public RAM(Barramento barramento) {

		this.barramento = barramento;
		this.memoria = new byte[IGB];

	}

	public void addInstrucao(byte[] instrucao) {
		for (int i = 0; i < instrucao.length; i++) {
			memoria[seq + i] = instrucao[i];
		}

		seq = seq + SIZE;
	}

	public byte[] getInstrucao(int endereco) {
		byte[] arr = new byte[16];

		if (((endereco - 1) * SIZE) < seq) {

			for (int i = 0; i < SIZE; i++) {
				arr[i] = memoria[((endereco - 1) * SIZE) + i];
			}
		}

		return arr;
	}

	public void setEndereco(int endereco, byte[] val) {

		for (int i = 0; i < val.length; i++) {
			memoria[(((endereco - 1) * 4) + i) + OFFSET] = val[i];
		}

	}

	public byte[] getEndereco(int endereco) {

		byte[] arr = new byte[4];

		for (int i = 0; i < 4; i++) {
			arr[i] = memoria[(((endereco - 1) * 4) + i) + OFFSET];
		}

		return arr;
	}

}
