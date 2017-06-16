package unifor.br.arquitetura;

public class Instrucao {
	
	Operacao operacao;
	
	String param_1;
	String param_2;
	String param_3;
	
	public Instrucao(String instrucao, String param1, String param2, String param3) {
		super();
		this.param_1 = param1;
		this.param_2 = param2;
		this.param_3 = param3;
		popularParametros(instrucao);
	}
	
	public static Instrucao parse(byte[] instrucao){
		
		byte[] arr = new byte[4];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = instrucao[i];
		}
		Operacao op = Operacao.setType(Bytes.parseToInt(arr));
		String pa_1 = null;
		String pa_2 = null;
		String pa_3 = null;
		String[] inst = op.getDescricao().split("_");
		
		arr = new byte[4];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = instrucao[i+4];
		}
		
		if(inst[1].equals("REG")){
			pa_1 = Bytes.parseToString(arr);
		}else{
			if(inst[1].equals("END")){			
				pa_1 = "0x"+Bytes.parseToInt(arr);
			}else{
				pa_1 = String.valueOf(Bytes.parseToInt(arr));
			}
		}
		
		if(inst.length > 2){
			arr = new byte[4];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = instrucao[i+8];
			}
			if(inst[2].equals("REG")){
				pa_2 = Bytes.parseToString(arr);
			}else{
				if(inst[2].equals("END")){			
					pa_2 = "0x"+Bytes.parseToInt(arr);
				}else{
					pa_2 = String.valueOf(Bytes.parseToInt(arr));
				}
			}
		}
		
		if(inst.length > 3){
			arr = new byte[4];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = instrucao[i+12];
			}
			if(inst[3].equals("REG")){
				pa_3 = Bytes.parseToString(arr);
			}else{
				if(inst[3].equals("END")){			
					pa_3 = "0x"+Bytes.parseToInt(arr);
				}else{
					pa_3 = String.valueOf(Bytes.parseToInt(arr));
				}
			}
		}
		
		return new Instrucao(inst[0], pa_1, pa_2, pa_3);
	}
	
	public void popularParametros(String instrucao){
		instrucao = instrucao.toUpperCase();
		
		String op = instrucao;
		
		if(param_1 != null){
			op = op+"_"+type(param_1);
		}
		if(param_2 != null){
			op = op+"_"+type(param_2);
		}
		if(param_3 != null){
			op = op+"_"+type(param_3);
		}
				
		this.operacao = Operacao.setType(op);
	}
	
	public String type(String param){
		
		if(isAdress(param)){
			return "END";
		}else if(isRegister(param)){
			return "REG";
		}else if(isNumber(param)){
			return "VAL";
		}
		
		return null;
	}
	
	public String typeOperacao(){	
		return operacao.getDescricao().split("_")[0];
	}
	
	public boolean isNumber(String number){
		return number.matches("-?\\d+(\\.\\d+)?");
	}
	
	public boolean isAdress(String adress){
		return adress.startsWith("0x");
	}
	
	public boolean isRegister(String register){
		return Character.isLetter(register.charAt(0));
	}
	
	public byte[] toBytes(){
		
		byte[] arr = new byte[16];
		byte[] aux = new byte[4];
		
		String[] inst = operacao.getDescricao().split("_");
		
		aux = Bytes.parseToBytes(operacao.getCodigo());
		for (int i = 0; i < aux.length; i++) {
			arr[i] = aux[i];
		}
		
		if(inst[1].equals("REG")){
			aux = Bytes.parseToBytes(param_1);
		}else{
			aux = Bytes.parseToBytes(Integer.parseInt(param_1.replace("0x","")));			
		}
		
		for (int i = 4; i < aux.length+4; i++) {
			arr[i] = aux[i-4];
		}
		
		if(inst.length > 2){
			
			if(inst[2].equals("REG")){
				aux = Bytes.parseToBytes(param_2);
			}else{
				aux = Bytes.parseToBytes(Integer.parseInt(param_2.replace("0x","")));			
			}
			
			for (int i = 8; i < aux.length+8; i++) {
				arr[i] = aux[i-8];
			}
			
		}
		
		if(inst.length > 3){
			
			if(inst[3].equals("REG")){
				aux = Bytes.parseToBytes(param_3);
			}else{
				aux = Bytes.parseToBytes(Integer.parseInt(param_3.replace("0x","")));			
			}
			
			for (int i = 12; i < aux.length+12; i++) {
				arr[i] = aux[i-12];
			}
		}
		
		return arr;
	}
	
	public String toString() { 
	    return this.operacao.descricao.split("_")[0] + ", " + this.param_1 + ", " + this.param_2 + ", " + this.param_3;
	} 
}
