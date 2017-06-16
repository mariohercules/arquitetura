package unifor.br.base;
public enum EscolhaOperacao {
	
	MOV_REG_VAL(1,"MOV_REG_VAL"),
	MOV_REG_REG(2,"MOV_REG_REG"),
	MOV_REG_END(3,"MOV_REG_END"),
	MOV_END_END(4,"MOV_END_END"),
	MOV_END_VAL(5,"MOV_END_VAL"),
	MOV_END_REG(6,"MOV_END_REG"),
	
	ADD_END_END(7,"ADD_END_END"),
	ADD_END_VAL(8,"ADD_END_VAL"),
	ADD_END_REG(9,"ADD_END_REG"),
	ADD_REG_VAL(10,"ADD_REG_VAL"),
	ADD_REG_REG(11,"ADD_REG_REG"),
	ADD_REG_END(12,"ADD_REG_END"),
	
	INC_REG(13,"INC_REG"),
	INC_END(14,"INC_END"),
	
	IMUL_REG_REG(15,"IMUL_REG_REG"),
	IMUL_REG_REG_VAL(16,"IMUL_REG_REG_VAL"),
	IMUL_REG_END(17,"IMUL_REG_END"),
	IMUL_REG_END_VAL(18,"IMUL_REG_END_VAL"),
		
	LOOP(19,"LOOP"),
	JUMP(20,"JUMP"),
	
	
	CLEAR_END(21,"CLEAR_END"),
	
	
	;

	int codigo;
	String descricao;
	
	EscolhaOperacao(int codigo,String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo(){
		return this.codigo;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	public static EscolhaOperacao setType(int codigo){
		EscolhaOperacao aux = null;
		switch( codigo ){
			case 1:
				aux = EscolhaOperacao.MOV_REG_VAL;
				break;
			case 2:
				aux = EscolhaOperacao.MOV_REG_REG;
				break;
			case 3:
				aux = EscolhaOperacao.MOV_REG_END;
				break;
			case 4:
				aux = EscolhaOperacao.MOV_END_END;
				break;
			case 5:
				aux = EscolhaOperacao.MOV_END_VAL;
				break;
			case 6:
				aux = EscolhaOperacao.MOV_END_REG;
				break;
			case 7:
				aux = EscolhaOperacao.ADD_END_END;
				break;
			case 8:
				aux = EscolhaOperacao.ADD_END_VAL;
				break;
			case 9:
				aux = EscolhaOperacao.ADD_END_REG;
				break;
			case 10:
				aux = EscolhaOperacao.ADD_REG_VAL;
				break;
			case 11:
				aux = EscolhaOperacao.ADD_REG_REG;
				break;
			case 12:
				aux = EscolhaOperacao.ADD_REG_END;
				break;
			case 13:
				aux = EscolhaOperacao.INC_REG;
				break;
			case 14:
				aux = EscolhaOperacao.INC_END;
				break;
			case 15:
				aux = EscolhaOperacao.IMUL_REG_REG;
				break;
			case 16:
				aux = EscolhaOperacao.IMUL_REG_REG_VAL;
				break;
			case 17:
				aux = EscolhaOperacao.IMUL_REG_END;
				break;
			case 18:
				aux = EscolhaOperacao.IMUL_REG_END_VAL;
				break;
			case 19:
				aux = EscolhaOperacao.LOOP;
				break;
			case 20:
				aux = EscolhaOperacao.JUMP;
				break;
	        case 21:
	        	aux = EscolhaOperacao.CLEAR_END;
	        	break;
	        default:
            	return null;
        }
		
		return aux;
    }
	
	public static EscolhaOperacao setType(String descricao){
        descricao = descricao.toUpperCase();
		EscolhaOperacao aux = null;
        switch( descricao ){
        
        case "MOV_REG_VAL":
			aux = EscolhaOperacao.MOV_REG_VAL;
			break;
		case "MOV_REG_REG":
			aux = EscolhaOperacao.MOV_REG_REG;
			break;
		case "MOV_REG_END":
			aux = EscolhaOperacao.MOV_REG_END;
			break;
		case "MOV_END_END":
			aux = EscolhaOperacao.MOV_END_END;
			break;
		case "MOV_END_VAL":
			aux = EscolhaOperacao.MOV_END_VAL;
			break;
		case "MOV_END_REG":
			aux = EscolhaOperacao.MOV_END_REG;
			break;
		case "ADD_END_END":
			aux = EscolhaOperacao.ADD_END_END;
			break;
		case "ADD_END_VAL":
			aux = EscolhaOperacao.ADD_END_VAL;
			break;
		case "ADD_END_REG":
			aux = EscolhaOperacao.ADD_END_REG;
			break;
		case "ADD_REG_VAL":
			aux = EscolhaOperacao.ADD_REG_VAL;
			break;
		case "ADD_REG_REG":
			aux = EscolhaOperacao.ADD_REG_REG;
			break;
		case "ADD_REG_END":
			aux = EscolhaOperacao.ADD_REG_END;
			break;
		case "INC_REG":
			aux = EscolhaOperacao.INC_REG;
			break;
		case "INC_END":
			aux = EscolhaOperacao.INC_END;
			break;
		case "IMUL_REG_REG":
			aux = EscolhaOperacao.IMUL_REG_REG;
			break;
		case "IMUL_REG_REG_VAL":
			aux = EscolhaOperacao.IMUL_REG_REG_VAL;
			break;
		case "IMUL_REG_END":
			aux = EscolhaOperacao.IMUL_REG_END;
			break;
		case "IMUL_REG_END_VAL":
			aux = EscolhaOperacao.IMUL_REG_END_VAL;
			break;
		case "LOOP":
			aux = EscolhaOperacao.LOOP;
			break;
		case "JUMP":
			aux = EscolhaOperacao.JUMP;
			break;
        case "CLEAR_END":
        	aux = EscolhaOperacao.CLEAR_END;
        	break;
        default:
        	return null;
        }
        
        return aux;
		
		
    }
	

}
