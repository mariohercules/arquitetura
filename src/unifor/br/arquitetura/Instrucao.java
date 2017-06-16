package unifor.br.arquitetura;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unifor.br.base.EscolhaOperacao;

public class Instrucao {

	EscolhaOperacao operacao;
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

	public Instrucao(String param1, String param2, String param3) {

		this.operacao = EscolhaOperacao.setType("JUMP");

		String regex = "(\\d+|[ABCDabcd])\\s*(==|<=|>=|!=|[<>])\\s*(\\d+)";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(param1);

		if (matcher.find()) {
			this.param_1 = matcher.group(1);
			this.param_2 = matcher.group(2);
			this.param_3 = matcher.group(3);
		}

	}

	public Instrucao(Boolean soparam, String param1, String param2, String param3) {
		this.operacao = EscolhaOperacao.setType("JUMP");

		this.param_1 = param1;
		this.param_2 = param2;
		this.param_3 = param3;

	}

	public Instrucao(String param1, String param2) {

		this.operacao = EscolhaOperacao.setType("LOOP");

		this.param_1 = param2;
		this.param_2 = null;
		this.param_3 = null;

	}

	@SuppressWarnings("unused")
	public static Instrucao parse(byte[] instrucao) {

		String instru = new String(instrucao);

		EscolhaOperacao op = null;
		String pa_1 = null;
		String pa_2 = null;
		String pa_3 = null;
		String[] inst = new String[1];

		byte[] arr1 = new byte[4];
		for (int i = 0; i < arr1.length; i++) {
			arr1[i] = instrucao[i];
		}

		op = EscolhaOperacao.setType(Parser.parseToInt(arr1));

		if (op.equals(EscolhaOperacao.JUMP)) {

			inst[0] = op.getDescricao();

			arr1 = new byte[4];
			for (int i = 0; i < arr1.length; i++) {
				arr1[i] = instrucao[i + 4];
			}

			pa_1 = String.valueOf(Parser.enviarParaString(arr1));

			arr1 = new byte[4];
			for (int i = 0; i < arr1.length; i++) {
				arr1[i] = instrucao[i + 8];
			}

			byte[] aux = new byte[1];
			aux[0] = arr1[2];

			pa_2 = Parser.enviarParaString(arr1).replace(new String(aux), "");

			arr1 = new byte[4];
			for (int i = 0; i < arr1.length; i++) {
				arr1[i] = instrucao[i + 12];
			}
			pa_3 = String.valueOf(Parser.parseToInt(arr1));

			return new Instrucao(pa_1 + pa_2 + pa_3, null, null);

		} else if (op.equals(EscolhaOperacao.LOOP)) {

			inst[0] = op.getDescricao();

			arr1 = new byte[4];
			for (int i = 0; i < arr1.length; i++) {
				arr1[i] = instrucao[i + 4];
			}

			pa_1 = String.valueOf(Parser.parseToInt(arr1));

			return new Instrucao(null, pa_1);

		} else {

			byte[] arr = new byte[4];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = instrucao[i];
			}

			op = EscolhaOperacao.setType(Parser.parseToInt(arr));

			inst = op.getDescricao().split("_");

			arr = new byte[4];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = instrucao[i + 4];
			}

			if (inst[1].equals("REG")) {
				pa_1 = Parser.enviarParaString(arr);
			} else {
				if (inst[1].equals("END")) {
					pa_1 = "0x" + Parser.parseToInt(arr);
				} else {
					pa_1 = String.valueOf(Parser.parseToInt(arr));
				}
			}

			if (inst.length > 2) {
				arr = new byte[4];
				for (int i = 0; i < arr.length; i++) {
					arr[i] = instrucao[i + 8];
				}
				if (inst[2].equals("REG")) {
					pa_2 = Parser.enviarParaString(arr);
				} else {
					if (inst[2].equals("END")) {
						pa_2 = "0x" + Parser.parseToInt(arr);
					} else {
						pa_2 = String.valueOf(Parser.parseToInt(arr));
					}
				}
			}

			if (inst.length > 3) {
				arr = new byte[4];
				for (int i = 0; i < arr.length; i++) {
					arr[i] = instrucao[i + 12];
				}
				if (inst[3].equals("REG")) {
					pa_3 = Parser.enviarParaString(arr);
				} else {
					if (inst[3].equals("END")) {
						pa_3 = "0x" + Parser.parseToInt(arr);
					} else {
						pa_3 = String.valueOf(Parser.parseToInt(arr));
					}
				}
			}
			return new Instrucao(inst[0], pa_1, pa_2, pa_3);
		}

	}

	public void popularParametros(String instrucao) {
		instrucao = instrucao.toUpperCase();

		String op = instrucao;

		if (param_1 != null) {
			op = op + "_" + type(param_1);
		}
		if (param_2 != null) {
			op = op + "_" + type(param_2);
		}
		if (param_3 != null) {
			op = op + "_" + type(param_3);
		}

		this.operacao = EscolhaOperacao.setType(op);
	}

	public static String type(String param) {

		if (isAdress(param)) {
			return "END";
		} else if (isRegister(param)) {
			return "REG";
		} else if (isNumber(param)) {
			return "VAL";
		}

		return null;
	}

	public String typeOperacao() {

		if (operacao.equals(EscolhaOperacao.JUMP)) {
			return operacao.getDescricao();
		} else if (operacao.equals(EscolhaOperacao.LOOP)) {
			return operacao.getDescricao();
		} else {
			return operacao.getDescricao().split("_")[0];
		}
	}

	public static boolean isNumber(String number) {
		return number.matches("-?\\d+(\\.\\d+)?");
	}

	public static boolean isAdress(String adress) {
		return adress.startsWith("0x");
	}

	public static boolean isRegister(String register) {
		return Character.isLetter(register.charAt(0));
	}

	public byte[] toBytes() {

		byte[] arr = new byte[16];
		byte[] aux = new byte[4];

		String[] inst = null;

		if (operacao.equals(EscolhaOperacao.JUMP)) {

			aux = Parser.enviarParaBytes(operacao.getCodigo());
			for (int i = 0; i < aux.length; i++) {
				arr[i] = aux[i];
			}

			if (type(param_1).equals("REG")) {
				aux = Parser.enviarParaBytes(param_1);
			} else {
				aux = Parser.enviarParaBytes(Integer.parseInt(param_1));
			}

			byte[] a = new byte[1];
			int x = aux.length;
			for (int i = 0; i < aux.length + 3; i++) {
				if (aux.length >= 8 - (i + 4)) {
					arr[i + 4] = aux[aux.length - x];
					x--;
				} else {
					arr[i + 4] = a[0];
				}
			}

			aux = Parser.enviarParaBytes(param_2);
			for (int i = 8; i < aux.length + 8; i++) {
				arr[i] = aux[i - 8];
			}

			aux = Parser.enviarParaBytes(Integer.parseInt(param_3));
			for (int i = 12; i < aux.length + 12; i++) {
				arr[i] = aux[i - 12];
			}

		} else if (operacao.equals(EscolhaOperacao.LOOP)) {

			aux = Parser.enviarParaBytes(operacao.getCodigo());
			for (int i = 0; i < aux.length; i++) {
				arr[i] = aux[i];
			}

			aux = Parser.enviarParaBytes(Integer.parseInt(param_1));
			for (int i = 4; i < aux.length + 4; i++) {
				arr[i] = aux[i - 4];
			}

		} else {

			inst = operacao.getDescricao().split("_");

			aux = Parser.enviarParaBytes(operacao.getCodigo());
			for (int i = 0; i < aux.length; i++) {
				arr[i] = aux[i];
			}

			if (inst[1].equals("REG")) {
				aux = Parser.enviarParaBytes(param_1);
			} else {
				aux = Parser.enviarParaBytes(Integer.parseInt(param_1.replace("0x", "")));
			}

			for (int i = 4; i < aux.length + 4; i++) {
				arr[i] = aux[i - 4];
			}

			if (inst.length > 2) {

				if (inst[2].equals("REG")) {
					aux = Parser.enviarParaBytes(param_2);
				} else {
					aux = Parser.enviarParaBytes(Integer.parseInt(param_2.replace("0x", "")));
				}

				for (int i = 8; i < aux.length + 8; i++) {
					arr[i] = aux[i - 8];
				}

			}

			if (inst.length > 3) {

				if (inst[3].equals("REG")) {
					aux = Parser.enviarParaBytes(param_3);
				} else {
					aux = Parser.enviarParaBytes(Integer.parseInt(param_3.replace("0x", "")));
				}

				for (int i = 12; i < aux.length + 12; i++) {
					arr[i] = aux[i - 12];
				}

			}
		}

		return arr;
	}

	public String toString() {
		return this.operacao.getDescricao().split("_")[0] + ", " + this.param_1 + ", " + this.param_2 + ", "
				+ this.param_3;
	}

}
