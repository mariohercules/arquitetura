package unifor.br.arquitetura;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuloES {
  	    
    String nome = System.getProperty("user.dir") + "\\src\\code.txt";
    
    List<String> instrucoes;
    
    Barramento barramento;
    
    public ModuloES() {
		super();
		instrucoes = new ArrayList<>();
		this.barramento = new Barramento(this);
	}
    
    public void run(){
    	
	    try {
	    	
	    	FileReader arq = new FileReader(nome);
	    	BufferedReader lerArq = new BufferedReader(arq);
	    	
			String regex = "(\\w*)\\s*([ABCDabcd]|0x[\\d]+|[\\d]+)+\\s*(?:,\\s+)?([ABCDabcd]|0x[\\d]+|[\\d]+)*\\s*(?:,\\s*)?([ABCDabcd]|0x[\\d]+|[\\d]+)?";
			Pattern pattern = Pattern.compile(regex);
			
			String linha  = lerArq.readLine();
	    	while (linha != null) {
	    		
	    		Matcher matcher = pattern.matcher(linha);
	    		if(matcher.matches()){
	    			Instrucao instrucao = new Instrucao(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4));
	    			barramento.addInstrucao(instrucao.toBytes());
	    		}
	    		linha = lerArq.readLine();
	    	}
	    	
	    	barramento.cpu.run();
	    	
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    
    }
    
    public void enviarInstrucao(){    	
    	for (int i = 0; i < instrucoes.size(); i++) {			
    		barramento.addInstrucao(Bytes.parseToBytes(instrucoes.get(i)));
		}
    	
    }
    
        
}
