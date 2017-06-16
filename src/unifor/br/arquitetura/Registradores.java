package unifor.br.arquitetura;

public class Registradores {
	
	String a = "0";
	String b = "0";
	String c = "0";
	String d = "0";
	
	// Contador de Instrucoes
	int ci = 1;
	
	public Registradores() {
		super();
	}
	
	public void setReg(String reg, String val){
		
		if(reg.charAt(0) == 'A'){
			a = val;
		}else if(reg.charAt(0) == 'B'){
			b = val;
		}else if(reg.charAt(0) == 'C'){
			c = val;
		}else if(reg.charAt(0) == 'D'){
			d = val;
		}else{
			System.err.println("Registrador invalido");;
		}
		
	}
	
	public String getReg(String reg){
		
		if(reg.charAt(0) == 'A'){
			return a;
		}else if(reg.charAt(0) == 'B'){
			return b;
		}else if(reg.charAt(0) == 'C'){
			return c;
		}else if(reg.charAt(0) == 'D'){
			return d;
		}else{
			System.err.println("Registrador invalido");;
			return null;			
		}
		
	}
	
}
