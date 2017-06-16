package unifor.br.arquitetura;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

	private static Map<String, Cooldown> cooldowns = new HashMap<String, Cooldown>();
	private long start;
	private final int tempoEmSegundos;
	private final UUID id;
	private final String coolDownNome;

	public Cooldown(UUID id, String cooldownName, int timeInSeconds) {
		this.id = id;
		this.coolDownNome = cooldownName;
		this.tempoEmSegundos = timeInSeconds;
	}

	public static boolean estaEmCoolDown(UUID id, String cooldownName) {
		if (pegarTempoRestante(id, cooldownName) >= 1) {
			return true;
		} else {
			parar(id, cooldownName);
			return false;
		}
	}

	private static void parar(UUID id, String cooldownName) {
		Cooldown.cooldowns.remove(id + cooldownName);
	}

	private static Cooldown pegarCoolDown(UUID id, String cooldownName) {
		return cooldowns.get(id.toString() + cooldownName);
	}

	public static int pegarTempoRestante(UUID id, String cooldownName) {
		Cooldown cooldown = pegarCoolDown(id, cooldownName);
		int f = -1;
		if (cooldown != null) {
			long now = System.currentTimeMillis();
			long cooldownTime = cooldown.start;
			int totalTime = cooldown.tempoEmSegundos;
			int r = (int) (now - cooldownTime) / 1000;
			f = (r - totalTime) * (-1);
		}
		return f;
	}

	public void iniciar() {
		this.start = System.currentTimeMillis();
		cooldowns.put(this.id.toString() + this.coolDownNome, this);
	}

}
