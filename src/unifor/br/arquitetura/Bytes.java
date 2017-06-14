package unifor.br.arquitetura;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Bytes {
	
	public static int parseToInt(byte[] arr) {
		
		return ByteBuffer.wrap(arr).getInt();
		
    }
	
	public static byte[] parseToBytes(int val) {
		
		 byte[] result = new byte[4];

		  result[0] = (byte) (val >> 24);
		  result[1] = (byte) (val >> 16);
		  result[2] = (byte) (val >> 8);
		  result[3] = (byte) (val /*>> 0*/);

		 return result;
        
    }
	
	public static String parseToString(byte[] arr) {
        
		String result = null;
		
//    	try {
			result = new String(arr);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
        
    	return result;
    }
	
	public static byte[] parseToBytes(String str) {
        
    	try {
            Charset c = Charset.forName("UTF-8");
            CharsetEncoder ce = c.newEncoder();

            CharBuffer cb = CharBuffer.wrap(str);
            ByteBuffer bb = ce.encode(cb);
            byte[] b = bb.array();
            return cortaZeros(b);
            //return b;
        } catch (CharacterCodingException x) {
            throw new AssertionError(x);
        }
        
    }           

    private static byte[] cortaZeros(byte[] array) {
    	
        if (array[array.length - 1] != 0) return array;
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
