import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
/*
 * ��������̰���ߵĻ������ͬʱ������Ҳ�������������
 */
public class Yard extends Frame {
	PaintThread paintThread = new PaintThread();
	private boolean gameOver = false; //��Ϸ�Ƿ����
	
	public static final int ROWS = 30;		//���и�����Ŀ����
	public static final int COLS = 30;
	public static final int BLOCK_SIZE = 15;	//���ӵĴ�С
	
	private Font fontGameOver = new Font("����", Font.BOLD, 40);		//��Ϸ������������ʽ����
	private Font fontScoreGet = new Font("SansSerif", Font.BOLD, 30);		//��Ϸ������������ʽ����
	private Font fontStratwords = new Font("SansSerif", Font.BOLD,20);
	
	private int score = 0;
	
	Snake s = new Snake(this);			//��
	Egg e = new Egg();					//��
	
	
	
	//���ڼ��¼�������
	public void launch() {
		this.setLocation(300, 200);		//���ô��ڵ�λ��
		this.setSize(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);			//���ô��ڵĴ�С�����������ϸ��Ӵ�С
		//���������á���׽�û��رմ��ڵ�ָ�
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {				//�û��������ڹ���������رմ���ʱ�����������
				System.exit(0);
			}
			
		});
		this.setVisible(true);
		this.addKeyListener(new KeyMonitor());				//�����¼������������հ����ı仯
		
		new Thread(paintThread).start();
	}
	
	
	//���������
	public static void main(String[] args) {
		
		new Yard().launch();
	}
	
	//��Ϸ�����ķ���
	public void stop() {
		gameOver = true;
	}
	
	//���ǻ��ƻ���ķ���
	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();				//��ȡ�������õĵ�ǰ��ɫ
		g.setColor(Color.LIGHT_GRAY);		//��屳������ɫ
		g.fillRect(0, 0, COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);		//�����Σ�����������õĸ����Լ���ɫ�����
		g.setColor(Color.magenta);			//������ɫ
		
		//�������ߣ�����������
		for(int i=1; i<ROWS; i++) {
			g.drawLine(0, BLOCK_SIZE * i, COLS * BLOCK_SIZE, BLOCK_SIZE * i);
		}
		for(int i=1; i<COLS; i++) {
			g.drawLine(BLOCK_SIZE * i, 0, BLOCK_SIZE * i, BLOCK_SIZE * ROWS);
		}
		
		g.setColor(Color.BLACK);			//��������ɫ��ʾ����
		g.drawString("score:" + score, 10, 60);		//������ʾ��λ��
		
		/*
		try{
			g.setFont(fontStratwords);
			g.drawString("����Ϸ�׸��ҷ�~", 20, 200);
			Thread.sleep(5000);
			g.drawString("",20,200);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		*/
		
		//��Ϸ���������
		if(gameOver) {
			g.setFont(fontGameOver);
			g.drawString("��Ϸ����", 120, 180);		
			
			g.setFont(fontScoreGet);
			if(score >=0 && score <= 10)
			g.drawString(score + "�֣�" + "��λ��Ӣ�»�ͭ", 80, 250);
			else if(score > 10 && score <= 20)
			g.drawString(score + "��," + "��λ����������", 80, 250);
			else if(score > 20 && score <= 30)
			g.drawString(score + "��," + "��λ����ҫ�ƽ�", 80, 250);
			else if(score > 30 && score <= 40)
			g.drawString(score + "��," + "��λ�����󲬽�", 80, 250);
			else if(score > 40 && score <= 50)
			g.drawString(score + "��," + "��λ������ʯ", 80, 250);
			else if(score >50 && score <= 60)
			g.drawString(score + "��," + "����Ǵ�ʦ", 80, 250);
			else if(score > 60)
			g.drawString(score + "��," + "�������ǿ���ߣ�", 80, 250);
			
			paintThread.pause();
		}
		
		g.setColor(c);							//������ɫ
		
		s.eat(e);
		e.draw(g);
		s.draw(g);
		
		
	}
	//��˫���弼������������˸�ĸо�����Ϊ��ʾ����ͼ���ʱ�������repaint�������������õ�ʱ����Ҫ�����������
		//Ȼ��ŵ���repaint��ʾ���棬�������ʱ�����ڻ����������˸���������Ļ�������ƻ���϶࣬�ͻ���ֲ�����˸��
	Image offScreenImage = null;
	@Override
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		}
		Graphics gOff = offScreenImage.getGraphics();
		paint(gOff);
		g.drawImage(offScreenImage, 0, 0,  null);				
	}
	
	//��ͼ�̡߳������ߵ��ƶ������Ƿ��������жϡ�
	private class PaintThread implements Runnable {
		private boolean running = true;
		private boolean pause = false;
		public void run() {
			while(running) {
				if(pause) continue; 				
				else repaint();
				
				try {
					Thread.sleep(200);						//��ͼ���ٶȣ���ʾ������Ч���������ƶ��������ٶ�
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void pause() {
			this.pause = true;
		}
		
		//��û����ͣ��û�г��磬��ô��Ϸ����
		public void reStart() {
			this.pause = false;
			s = new Snake(Yard.this);
			gameOver = false;
		}
		
		public void gameOver() {
			running = false;
		}
		
	}
	
	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_F2) {
				paintThread.reStart();
			}
			s.keyPressed(e);
		}
		
	}
	
	//�õ����õķ���
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}

}

/*
class NewFrame extends JFrame {
	 
    private JLabel firstwords;
 
    public NewFrame() {
 
    	firstwords = new JLabel();
        add(firstwords);
        setSize(500, 500);
        setLocation(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            	firstwords.setText("����Ϸ�׸��ҷ�~");
                try {
                    Thread.sleep(5000);//���߳�˯��5��
                } catch (InterruptedException ex) {
                }
                firstwords.setText("");//�������
            }
        });
        t.start();//�����߳�
    }
}
*/
