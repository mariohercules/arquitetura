package unifor.br.arquitetura;

public class CPU {

	Barramento barramento;
	Registradores registradores;
	
	public CPU(Barramento barramento) {
		this.barramento = barramento;
		this.registradores = new Registradores();
	}
	
	public boolean run() {
		
		try {
			
			int loop = barramento.ram.seq/16;
			
			for (int i = 0; i < loop; i++) {
				
				Instrucao instrucao = Instrucao.parse(barramento.ram.getInstrucao(registradores.ci));
				if(instrucao.typeOperacao().equals("MOV")){
					mov(instrucao);
				}else if(instrucao.typeOperacao().equals("ADD")){
					add(instrucao);
				}else if(instrucao.typeOperacao().equals("IMUL")){
					imul(instrucao);
				}else if(instrucao.typeOperacao().equals("INC")){
					inc(instrucao);
				}
				
				print();
				registradores.ci++;
				
				Thread.sleep(2000);
			}
			
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void add(Instrucao instrucao) {
		
		if(instrucao.operacao.equals(Operacao.ADD_REG_VAL)){
			
			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(instrucao.param_2);
			registradores.setReg(instrucao.param_1, String.valueOf(val1+val2));
			
		}else if(instrucao.operacao.equals(Operacao.ADD_REG_REG)){

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1+val2));
			
		}else if(instrucao.operacao.equals(Operacao.ADD_REG_END)){
			
			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x",""));
			int val2 = Bytes.parseToInt(barramento.ram.getEndereco(end2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1+val2));
			
		}else if(instrucao.operacao.equals(Operacao.ADD_END_END)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val1 = Bytes.parseToInt(barramento.ram.getEndereco(end1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x",""));
			int val2 = Bytes.parseToInt(barramento.ram.getEndereco(end2));
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val1+val2));
			
		}else if(instrucao.operacao.equals(Operacao.ADD_END_VAL)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val1 = Bytes.parseToInt(barramento.ram.getEndereco(end1));
			int val2 = Integer.parseInt(instrucao.param_2);
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val1+val2));
			
		}else if(instrucao.operacao.equals(Operacao.ADD_END_REG)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val1 = Bytes.parseToInt(barramento.ram.getEndereco(end1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val1+val2));
		}
		
	}
	
	public void imul(Instrucao instrucao) {
		if(instrucao.operacao.equals(Operacao.IMUL_REG_REG)){

			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1*val2));
			
		}else if(instrucao.operacao.equals(Operacao.IMUL_REG_END)){
			
			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x",""));
			int val2 = Bytes.parseToInt(barramento.ram.getEndereco(end2));
			registradores.setReg(instrucao.param_1, String.valueOf(val1*val2));
			
		}else if(instrucao.operacao.equals(Operacao.IMUL_REG_REG_VAL)){
			
			int val2 = Integer.parseInt(registradores.getReg(instrucao.param_2));
			int val3 = Integer.parseInt(instrucao.param_3);
			registradores.setReg(instrucao.param_1, String.valueOf(val2*val3));
			
		}else if(instrucao.operacao.equals(Operacao.IMUL_REG_END_VAL)){
			
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x",""));
			int val2 = Bytes.parseToInt(barramento.ram.getEndereco(end2));
			int val3 = Integer.parseInt(instrucao.param_3);
			registradores.setReg(instrucao.param_1, String.valueOf(val2*val3));
			
		}
	}
	
	public void mov(Instrucao instrucao) {
		
		if(instrucao.operacao.equals(Operacao.MOV_REG_VAL)){
			registradores.setReg(instrucao.param_1, instrucao.param_2);			
		}else if(instrucao.operacao.equals(Operacao.MOV_REG_REG)){
			registradores.setReg(instrucao.param_1, registradores.getReg(instrucao.param_2));
		}else if(instrucao.operacao.equals(Operacao.MOV_REG_END)){
			
			int end = Integer.parseInt(instrucao.param_2.replace("0x",""));
			int val = Bytes.parseToInt(barramento.ram.getEndereco(end));
			registradores.setReg(instrucao.param_1, String.valueOf(val));
			
		}else if(instrucao.operacao.equals(Operacao.MOV_END_END)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int end2 = Integer.parseInt(instrucao.param_2.replace("0x",""));
			byte[] val = barramento.ram.getEndereco(end2);
			
			barramento.ram.setEndereco(end1, val);
			
			
		}else if(instrucao.operacao.equals(Operacao.MOV_END_VAL)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val = Integer.parseInt(instrucao.param_2.replace("0x",""));
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val));
			
		}else if(instrucao.operacao.equals(Operacao.MOV_END_REG)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val = Integer.parseInt(registradores.getReg(instrucao.param_2));
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val));
		}	
	}
	
	public void inc(Instrucao instrucao) {
		if(instrucao.operacao.equals(Operacao.INC_REG)){
			
			int val1 = Integer.parseInt(registradores.getReg(instrucao.param_1));
			registradores.setReg(instrucao.param_1, String.valueOf(val1+1));
			
		}else if(instrucao.operacao.equals(Operacao.INC_END)){
			
			int end1 = Integer.parseInt(instrucao.param_1.replace("0x",""));
			int val1 = Bytes.parseToInt(barramento.ram.getEndereco(end1));
			
			barramento.ram.setEndereco(end1, Bytes.parseToBytes(val1+1));
			
		}
	}
	
	public void print(){		
		
		int loop = barramento.ram.seq/16;
		if(registradores.ci < loop){
			
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
			for (int i = 0; i < loop; i++ ) {
				
				System.out.print("| ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.print(barramento.ram.memoria[count++]+"\t | ");
				System.out.println(barramento.ram.memoria[count++]+"\t | ");
				System.out.print("|--------------------------------");
				System.out.print("|-------------------------------");
				System.out.print("|-------------------------------");
				System.out.print("|-------------------------------|\n");
			}
			
			
			System.out.println("\nMemoria Valores\n");
			
			for (int i = 0; i < 80; ) {
				
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.print((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]+"\t");
				System.out.println((1600+(i++))+" : "+barramento.ram.memoria[(1600+i)]);
				
			}
			
			Instrucao instrucao = Instrucao.parse(barramento.ram.getInstrucao(registradores.ci));
			System.out.print("\nRegistrador\t\t\t\t\t\t\t");
			System.out.println("Instrução = "+instrucao.toString());
			
			System.out.print("A : "+registradores.a+"\t");
			System.out.print("B : "+registradores.b+"\t");
			System.out.print("C : "+registradores.c+"\t");
			System.out.print("D : "+registradores.d+"\t");
			System.out.println("CI : "+registradores.ci);
		}else{
			
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

		}
			
		
	}
	

}
