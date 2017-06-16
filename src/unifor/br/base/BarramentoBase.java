package unifor.br.base;

public enum BarramentoBase {
	

	CONTROLE(1,"CONTROLE"),
	ENDERECO(2,"ENDERECO"),
	DADO(3,"DADO"),
	RODAR(4,"RODAR"),
	;

	int codigo;
	String descricao;
	
	BarramentoBase(int codigo,String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo(){
		return this.codigo;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	public static BarramentoBase setType(int codigo){
		BarramentoBase aux = null;
		switch( codigo ){
			case 1:
				aux = BarramentoBase.CONTROLE;
				break;
			case 2:
				aux = BarramentoBase.ENDERECO;
				break;
			case 3:
				aux = BarramentoBase.DADO;
				break;
			case 4:
				aux = BarramentoBase.RODAR;
				break;
		    default:
            	return null;
        }
		
		return aux;
    }
	
	public static BarramentoBase setType(String descricao){
		
        descricao = descricao.toUpperCase();
		BarramentoBase aux = null;
        switch( descricao ){
        
        case "CONTROLE":
			aux = BarramentoBase.CONTROLE;
			break;
		case "ENDERECO":
			aux = BarramentoBase.ENDERECO;
			break;
		case "DADO":
			aux = BarramentoBase.DADO;
			break;
		case "RODAR":
			aux = BarramentoBase.RODAR;
			break;
        default:
        	return null;
        }
        
        return aux;
		
		
    }
	
	
}
