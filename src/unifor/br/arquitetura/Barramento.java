package unifor.br.arquitetura;

public class Barramento {
	
	RAM ram;
	ModuloES moduloES;
	CPU cpu;
			
	public Barramento(ModuloES moduloES) {
		this.moduloES = moduloES;
		this.ram = new RAM(this);
		this.cpu = new CPU(this);
	}

	public void addInstrucao(byte[] instrucao){
		ram.addInstrucao(instrucao);
	}
	
}
