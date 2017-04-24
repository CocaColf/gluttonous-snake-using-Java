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
 * 这个类代表贪吃蛇的活动场所，同时主函数也放在这个类里面
 */
public class Yard extends Frame {
	PaintThread paintThread = new PaintThread();
	private boolean gameOver = false; //游戏是否结束
	
	public static final int ROWS = 30;		//行列格子数目设置
	public static final int COLS = 30;
	public static final int BLOCK_SIZE = 15;	//格子的大小
	
	private Font fontGameOver = new Font("宋体", Font.BOLD, 40);		//游戏结束的字体样式设置
	private Font fontScoreGet = new Font("SansSerif", Font.BOLD, 30);		//游戏结束的字体样式设置
	private Font fontStratwords = new Font("SansSerif", Font.BOLD,20);
	
	private int score = 0;
	
	Snake s = new Snake(this);			//蛇
	Egg e = new Egg();					//蛋
	
	
	
	//窗口及事件监听器
	public void launch() {
		this.setLocation(300, 200);		//设置窗口的位置
		this.setSize(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);			//设置窗口的大小，格子数乘上格子大小
		//适配器设置。捕捉用户关闭窗口的指令。
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {				//用户发出窗口管理器命令关闭窗口时调用这个方法
				System.exit(0);
			}
			
		});
		this.setVisible(true);
		this.addKeyListener(new KeyMonitor());				//按键事件监听器，接收按键的变化
		
		new Thread(paintThread).start();
	}
	
	
	//主程序入口
	public static void main(String[] args) {
		
		new Yard().launch();
	}
	
	//游戏结束的方法
	public void stop() {
		gameOver = true;
	}
	
	//覆盖绘制画面的方法
	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();				//获取所有设置的当前颜色
		g.setColor(Color.LIGHT_GRAY);		//面板背景的颜色
		g.fillRect(0, 0, COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);		//填充矩形，把面板用设置的格子以及颜色填充完
		g.setColor(Color.magenta);			//线条颜色
		
		//画出横线，即创建各自
		for(int i=1; i<ROWS; i++) {
			g.drawLine(0, BLOCK_SIZE * i, COLS * BLOCK_SIZE, BLOCK_SIZE * i);
		}
		for(int i=1; i<COLS; i++) {
			g.drawLine(BLOCK_SIZE * i, 0, BLOCK_SIZE * i, BLOCK_SIZE * ROWS);
		}
		
		g.setColor(Color.BLACK);			//分数的颜色显示设置
		g.drawString("score:" + score, 10, 60);		//文字显示及位置
		
		/*
		try{
			g.setFont(fontStratwords);
			g.drawString("此游戏献给我芳~", 20, 200);
			Thread.sleep(5000);
			g.drawString("",20,200);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		*/
		
		//游戏结束的设计
		if(gameOver) {
			g.setFont(fontGameOver);
			g.drawString("游戏结束", 120, 180);		
			
			g.setFont(fontScoreGet);
			if(score >=0 && score <= 10)
			g.drawString(score + "分，" + "段位：英勇黄铜", 80, 250);
			else if(score > 10 && score <= 20)
			g.drawString(score + "分," + "段位：不屈白银", 80, 250);
			else if(score > 20 && score <= 30)
			g.drawString(score + "分," + "段位：荣耀黄金！", 80, 250);
			else if(score > 30 && score <= 40)
			g.drawString(score + "分," + "段位：华贵铂金！", 80, 250);
			else if(score > 40 && score <= 50)
			g.drawString(score + "分," + "段位：璀璨钻石", 80, 250);
			else if(score >50 && score <= 60)
			g.drawString(score + "分," + "你就是大师", 80, 250);
			else if(score > 60)
			g.drawString(score + "分," + "你就是最强王者！", 80, 250);
			
			paintThread.pause();
		}
		
		g.setColor(c);							//设置颜色
		
		s.eat(e);
		e.draw(g);
		s.draw(g);
		
		
	}
	//用双缓冲技术消除画面闪烁的感觉。因为显示绘制图像的时候调用了repaint方法，它被调用的时候需要清楚整个背景
		//然后才调用repaint显示画面，于是这个时间间隔内会产生画面闪烁。不消除的话如果绘制画面较多，就会出现不断闪烁。
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
	
	//绘图线程。包括蛇的移动，蛇是否死亡的判断。
	private class PaintThread implements Runnable {
		private boolean running = true;
		private boolean pause = false;
		public void run() {
			while(running) {
				if(pause) continue; 				
				else repaint();
				
				try {
					Thread.sleep(200);						//绘图的速度，显示出来的效果就是蛇移动起来的速度
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void pause() {
			this.pause = true;
		}
		
		//当没有暂停和没有出界，那么游戏继续
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
	
	//得到所得的分数
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
            	firstwords.setText("此游戏献给我芳~");
                try {
                    Thread.sleep(5000);//该线程睡眠5秒
                } catch (InterruptedException ex) {
                }
                firstwords.setText("");//清空文字
            }
        });
        t.start();//启动线程
    }
}
*/
