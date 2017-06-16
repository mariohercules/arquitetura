package unifor.br.base;
public enum ArquiteturaBase {
	
	RAM(1,"RAM"),
	CPU(2,"CPU"),
	ES(3,"ES")
	;

	int codigo;
	String descricao;
	
	ArquiteturaBase(int codigo,String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo(){
		return this.codigo;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	public static ArquiteturaBase setType(int codigo){
		ArquiteturaBase aux = null;
		switch( codigo ){
			case 1:
				aux = ArquiteturaBase.RAM;
				break;
			case 2:
				aux = ArquiteturaBase.CPU;
				break;
			case 3:
				aux = ArquiteturaBase.ES;
				break;
		    default:
            	return null;
        }
		
		return aux;
    }
	
	public static ArquiteturaBase setType(String descricao){
        descricao = descricao.toUpperCase();
		ArquiteturaBase aux = null;
        switch( descricao ){
        
        case "RAM":
			aux = ArquiteturaBase.RAM;
			break;
		case "CPU":
			aux = ArquiteturaBase.CPU;
			break;
		case "ES":
			aux = ArquiteturaBase.ES;
			break;
        default:
        	return null;
        }
        
        return aux;
		
		
    }
	

}
