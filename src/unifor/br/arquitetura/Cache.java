package unifor.br.arquitetura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import unifor.br.base.EsolhaAlgoritmo;

public class Cache implements Runnable {

	HashMap<Integer, byte[]> listaDaCache;
	HashMap<Integer, Integer> listaTemporaria;
	List<Integer> lruList;

	CPU cpu;

	public synchronized byte[] pegaInstrucao(int id) {

		adicionaAcessoSincronia(id);

		if (listaDaCache.containsKey(id)) {
			return listaDaCache.get(id);
		}

		return null;
	}

	public synchronized boolean setInstrucao(int id, byte[] instrucao) {
		adicionaAcessoSincronia(id);
		if (listaDaCache.containsKey(id)) {
			listaDaCache.put(id, instrucao);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void run() {

		if (Main.algoritmo.equals(EsolhaAlgoritmo.LRU)) {

			try {
				Thread.sleep(Main.valorDoCoolDown * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int count = 0;

			synchronized (listaDaCache) {
				for (Integer lru : lruList) {
					if (Main.valorDaCache > count) {
						listaDaCache.put(lru, cpu.getInstrucaoRam(lru));
						count = count + 16;
					}
				}
			}

			lruList = new ArrayList<>();

			try {
				Thread.sleep(Main.valorDoCoolDown * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (true) {

				if (!lruList.isEmpty()) {
					enviarCacheParaRam();

					count = 0;

					synchronized (listaDaCache) {
						for (Integer lru : lruList) {
							if (Main.valorDaCache > count) {
								listaDaCache.put(lru, cpu.getInstrucaoRam(lru));
								count = count + 16;
							}
						}
					}

					lruList = new ArrayList<>();
				}

				try {
					Thread.sleep(Main.valorDoCoolDown * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {

			boolean tipo = true;
			try {
				Thread.sleep(Main.valorDoCoolDown * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Map<Integer, Integer> aux = sortByComparatorValue(listaTemporaria, tipo);
			int count = 0;

			synchronized (listaDaCache) {
				for (Entry<Integer, Integer> list : aux.entrySet()) {
					listaDaCache.put(list.getKey(), cpu.getInstrucaoRam(list.getKey()));
					count = count + 16;
				}
			}

			listaTemporaria = new HashMap<>();

			while (true) {

				if (!listaTemporaria.isEmpty()) {
					enviarCacheParaRam();

					aux = sortByComparatorValue(listaTemporaria, tipo);
					count = 0;
					synchronized (listaDaCache) {
						listaDaCache = new HashMap<>();
						for (Entry<Integer, Integer> list : aux.entrySet()) {
							listaDaCache.put(list.getKey(), cpu.getInstrucaoRam(list.getKey()));
							count = count + 16;
						}
					}
					listaTemporaria = new HashMap<>();
				}

				try {
					Thread.sleep(Main.valorDoCoolDown * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void enviarCacheParaRam() {

		for (Entry<Integer, byte[]> entry : listaDaCache.entrySet()) {
			cpu.guardarEnderecoRam(entry.getKey(), entry.getValue());
		}

		listaDaCache = new HashMap<>();
	}

	public Cache(CPU cpu) {
		this.cpu = cpu;
		this.listaDaCache = new HashMap<>();
		listaTemporaria = new HashMap<>();
		lruList = new ArrayList<>();
	}

	private static Map<Integer, Integer> sortByComparatorValue(Map<Integer, Integer> unsortMap, boolean tipo) {

		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				if (tipo) {
					return o2.getValue().compareTo(o1.getValue());
				} else {
					return o1.getValue().compareTo(o2.getValue());
				}
			}
		});

		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public void adicionaAcessoSincronia(int id) {
		int aux = listaTemporaria.get(id) != null ? listaTemporaria.get(id) + 1 : 1;
		listaTemporaria.put(id, aux);
		lruList.add(id);
	}

}
