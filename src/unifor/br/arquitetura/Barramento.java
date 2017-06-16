package unifor.br.arquitetura;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import unifor.br.base.ArquiteturaBase;
import unifor.br.base.BarramentoBase;

public class Barramento implements Runnable {
	
	RAM ram;
	ModuloES moduloES;
	CPU cpu;
	
	private final Semaphore semaforoControleList = new Semaphore(0, true);
	private final Semaphore semaforoEnderecoList = new Semaphore(0, true);
	private final Semaphore semaforoDadoList = new Semaphore(0, true);
	
	List<Sinal> controleList;
	List<Sinal> enderecoList;
	List<Sinal> dadoList;
	
	boolean start = true;
	
	JFrame frame ;
	JTextArea display;
	
	public Barramento() {
		super();
		this.controleList = new ArrayList<>();
		this.enderecoList = new ArrayList<>();
		this.dadoList = new ArrayList<>();
		
	}

	public void addInstrucao(byte[] instrucao){
		ram.adicionarInstrucao(instrucao);
	}
	
	public void addControleList(Sinal sinal){
		this.controleList.add(sinal);
		semaforoControleList.release();
	}
	public void addEnderecoList(Sinal sinal){
		this.enderecoList.add(sinal);
		semaforoEnderecoList.release();
	}
	public void addDadoList(Sinal sinal){
		this.dadoList.add(sinal);
		semaforoDadoList.release();
	}
	
	public void init(ModuloES moduloES, RAM ram, CPU cpu) {
		
		this.moduloES = moduloES; 
		this.ram = ram; 
		this.cpu = cpu;
		
		new Thread(this.cpu).start();
		new Thread(this.ram).start();
		new Thread(this.moduloES).start();
				
		initFrame();
	}
	
	public void initFrame(){
		
		JPanel middlePanel = new JPanel ();
	    middlePanel.setBorder ( new TitledBorder ( new EtchedBorder (), "Barramento" ) );

	    display = new JTextArea ( 16, 58 );
	    display.setEditable ( false ); // set textArea non-editable
	    JScrollPane scroll = new JScrollPane ( display );
	    scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

	    middlePanel.add ( scroll );
	    
	    DefaultCaret caret = (DefaultCaret)display.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    
	    frame = new JFrame ();
	    frame.add ( middlePanel );
	    frame.pack ();
	    frame.setLocationRelativeTo ( null );
	    frame.setVisible ( true );
		
	}
	
	public synchronized void send(Sinal sinal){
		
		if(sinal.destino.equals(ArquiteturaBase.RAM)){
			
			if(sinal.tipoBarramento.equals(BarramentoBase.CONTROLE)){
				System.out.println("BAR: enviou sinal controle para Ram.");
				ram.adicionarControleNaLista(sinal);
				printBytes(sinal);
			}else if(sinal.tipoBarramento.equals(BarramentoBase.DADO)){
				System.out.println("BAR: enviou sinal dado para Ram.");
				ram.adicionarControleNaListaDeDados(sinal);	
				printBytes(sinal);
			}
			
			
		}else if(sinal.destino.equals(ArquiteturaBase.ES)){
			
			if(sinal.tipoBarramento.equals(BarramentoBase.CONTROLE)){
				System.out.println("BAR: enviou sinal controle para E/S.");
				moduloES.addControleList(sinal);
				printBytes(sinal);
			}else if(sinal.tipoBarramento.equals(BarramentoBase.ENDERECO)){
				System.out.println("BAR: enviou sinal endereco para E/S.");
				moduloES.addEnderecoList(sinal);
				printBytes(sinal);
			}else if(sinal.tipoBarramento.equals(BarramentoBase.DADO)){
				System.out.println("BAR: enviou sinal dado para E/S.");
				moduloES.addDadoList(sinal);
				printBytes(sinal);
			}
			
		}else if(sinal.destino.equals(ArquiteturaBase.CPU)){
			System.out.println("BAR: vai iniciar a cpu.");
			if(sinal.tipoBarramento.equals(BarramentoBase.CONTROLE)){				
				if(sinal.origem.equals(ArquiteturaBase.ES)){
					System.out.println("BAR: enviou sinal controle para CPU.");
					cpu.addControleList(sinal);
					printBytes(sinal);
				}
			}else
			if(sinal.tipoBarramento.equals(BarramentoBase.DADO)){				
				if(sinal.origem.equals(ArquiteturaBase.RAM)){
					System.out.println("BAR: enviou sinal dado para CPU.");
					cpu.addDadoList(sinal);
					printBytes(sinal);
				}
			}
		}
		
	}
	
	@Override
	public void run() {		
		
		System.out.println("BARRAMENTO iniciou");
		while (start) {
			
			if(semaforoControleList.tryAcquire()){
				send(controleList.get(0));
				controleList.remove(0);
			}
			
			if(semaforoEnderecoList.tryAcquire()){
				send(enderecoList.get(0));
				enderecoList.remove(0);
			}
			
			if(semaforoDadoList.tryAcquire()){
				send(dadoList.get(0));
				dadoList.remove(0);
			}
		}
		
		System.out.println("BARRAMENTO parou");
		
	}
	
	public void printBytes(Sinal sinal){
		
		int bytes = sinal.instrucao != null ? sinal.instrucao.length : 1;
		
		display.append(sinal.tipoBarramento.getDescricao()+" :\t"+ bytes*8 +"  bits\n");
	}
}
