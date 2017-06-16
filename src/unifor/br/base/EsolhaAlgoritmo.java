package unifor.br.base;

public enum EsolhaAlgoritmo {

	LRU(1, "LRU"), LFU(2, "LFU"), COOLDOWN(3, "COOLDOWN"),;

	int codigo;
	String descricao;

	EsolhaAlgoritmo(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public int getCodigo() {
		return this.codigo;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public static EsolhaAlgoritmo setType(int codigo) {
		EsolhaAlgoritmo aux = null;
		switch (codigo) {
		case 1:
			aux = EsolhaAlgoritmo.LRU;
			break;
		case 2:
			aux = EsolhaAlgoritmo.LFU;
			break;
		case 3:
			aux = EsolhaAlgoritmo.COOLDOWN;
			break;
		default:
			return null;
		}

		return aux;
	}

	public static EsolhaAlgoritmo setType(String descricao) {
		descricao = descricao.toUpperCase();
		EsolhaAlgoritmo aux = null;
		switch (descricao) {

		case "LRU":
			aux = EsolhaAlgoritmo.LRU;
			break;
		case "LFU":
			aux = EsolhaAlgoritmo.LFU;
			break;
		case "COOLDOWN":
			aux = EsolhaAlgoritmo.COOLDOWN;
			break;
		default:
			return null;
		}

		return aux;

	}

}
