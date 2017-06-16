package unifor.br.base;
public enum SinalBase {
	
	 SEQ(1,"SEQ")
	,INST(2,"INST")
	,ENDG(3, "ENDG")
	,ENDS(4, "ENDS")
	,CLEA(5, "CLEA")	
	,SETE(6, "SET")	
	;

	int codigo;
	String descricao;
	
	SinalBase(int codigo,String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo(){
		return this.codigo;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	public static SinalBase setType(int codigo){
		SinalBase aux = null;
		switch( codigo ){
			case 1:
				aux = SinalBase.SEQ;
				break;
			case 2:
				aux = SinalBase.INST;
				break;
			case 3:
				aux = SinalBase.ENDG;
				break;
			case 4:
				aux = SinalBase.ENDS;
				break;
			case 5:
				aux = SinalBase.CLEA;
				break;
			case 6:
				aux = SinalBase.SETE;
				break;
		    default:
            	return null;
        }
		
		return aux;
    }
	
	public static SinalBase setType(String descricao){
        descricao = descricao.toUpperCase();
		SinalBase aux = null;
        switch( descricao ){
        
        case "SEQ":
			aux = SinalBase.SEQ;
			break;
        case "INST":
        	aux = SinalBase.INST;
        	break;
        case "ENDG":
        	aux = SinalBase.ENDG;
        	break;
        case "ENDS":
        	aux = SinalBase.ENDS;
        	break;
        case "CLEA":
        	aux = SinalBase.CLEA;
        	break;
		case "SETE":
			aux = SinalBase.SETE;
			break;
        default:
        	return null;
        }
        
        return aux;
		
		
    }
	

}
