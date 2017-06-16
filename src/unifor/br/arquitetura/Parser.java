package unifor.br.arquitetura;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Parser {

	
	

	public static byte[] enviarParaBytes(String str) {

		try {
			Charset c = Charset.forName("UTF-8");
			CharsetEncoder ce = c.newEncoder();

			CharBuffer cb = CharBuffer.wrap(str);
			ByteBuffer bb = ce.encode(cb);
			byte[] b = bb.array();
			return verificarZerosString(b);
			// return b;
		} catch (CharacterCodingException x) {
			throw new AssertionError(x);
		}

	}

	
	
	public static int parseToInt(byte[] arr) {

		return ByteBuffer.wrap(arr).getInt();

	}

	public static byte[] enviarParaBytes(int val) {

		byte[] dados = new byte[4];

		dados[0] = (byte) (val >> 24);
		dados[1] = (byte) (val >> 16);
		dados[2] = (byte) (val >> 8);
		dados[3] = (byte) (val >> 0 );

		return dados;

	}
	
	public static String enviarParaString(byte[] dadosArray) {

		String dados = null;

		dados = new String(dadosArray);

		return dados;
	}
	
	private static byte[] verificarZerosString(byte[] array) {

		if (array[array.length - 1] != 0)
			return array;
		
		int inicio = 0, fim = array.length;
		while (inicio != fim && inicio != fim - 1) {
			int m = (inicio + fim) / 2;
			if (array[m] == 0) {
				fim = m;
			} else {
				inicio = m;
			}
		}

		int tamanho = array[inicio] == 0 ? inicio : inicio + 1;
		byte[] resultado = new byte[tamanho];
		System.arraycopy(array, 0, resultado, 0, tamanho);
		return resultado;

	}

}
