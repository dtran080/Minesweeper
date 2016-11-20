package mainCode;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Minesweeper extends JFrame {
	//Board properties
	private int ver,hor; // numbers of button hor and ver
	private char[][] board;	//draft boards
	private int bombNum,flagCount;	//numbers of bomb
	private GamesCategory ctgr;
	private Random rand;
	private boolean[][] checkBoard;
	private boolean firstPlay, explode, gameover;
	//Design properties
	private int btnSize;
	private JButton[][] gameBoard;
	private JComboBox selectBox;
	private ImageIcon[] tileIconArr,smileyIconArr;
	private JLabel flagCountLabel,timerLabel;
	private JButton resetButton;
	private static Timer timer;

	public Minesweeper(GamesCategory ctgr){
		explode = false;
		this.ctgr = ctgr;
		setFirstPlay(true);
		rand = new Random(); //random class
		generateDimension();	//getting Dimension
		createView();		//create Board and Other
		createIcon();
		setBtnFunc();	
	}
	
	private void createIcon(){
		int scalesmooth = java.awt.Image.SCALE_SMOOTH;
        BufferedImage img;
        Image newimg;
        String imgSource = "images//";
        String[] imgfNameArr = {"tile_clicked.gif","tile_1.gif",
        		"tile_2.gif","tile_3.gif","tile_4.gif",
        		"tile_5.gif","tile_6.gif","tile_7.gif","tile_8.gif",
        		"tile_plain.gif","flag.png","mine.png","mine_source.png","flag_wrong.png"};
        String[] smileyImgArr = {"new_game_button.png","lost.png","won.png"};
        tileIconArr = new ImageIcon[imgfNameArr.length]; 
        smileyIconArr = new ImageIcon[smileyImgArr.length];
        //	[9] -> "plain tile"; 10 -> flag; 11->mine; 12-> minesource 
	    try{	//tile icon
	    	for(int i=0;i<tileIconArr.length;i++){
	    		//System.out.println(imgSource+imgfNameArr[i]);
	    		img= ImageIO.read(new File(imgSource+imgfNameArr[i]));
		        newimg = img.getScaledInstance(btnSize, btnSize, scalesmooth);
		        tileIconArr[i] = new ImageIcon(newimg);
	    	}
	    } catch (IOException ioe){
	    	System.out.println("Can't find image "+ioe.getMessage());
	    }
	    try{
	    	for(int i=0;i<smileyImgArr.length;i++){
	    		//System.out.println(imgSource+imgfNameArr[i]);
	    		img= ImageIO.read(new File(imgSource+smileyImgArr[i]));
		        newimg = img.getScaledInstance(btnSize+7, btnSize+7, scalesmooth);
		        smileyIconArr[i] = new ImageIcon(newimg);
	    	}
	    } catch (IOException ioe){
	    	System.out.println("Can't find image "+ioe.getMessage());
	    }
	}  
	private void setBtnFunc(){
		resetButton.setIcon(smileyIconArr[0]);
		for (int i=0;i<ver;i++){
			for (int j=0;j<hor;j++){
				checkBoard[i][j] = false;
				gameBoard[i][j].putClientProperty("row", i);
				gameBoard[i][j].putClientProperty("col", j);
				gameBoard[i][j].putClientProperty("Minesweeper",this);
				gameBoard[i][j].addMouseListener(new GameButtonMouseListener());
				gameBoard[i][j].putClientProperty("flag", false);
				gameBoard[i][j].setIcon(tileIconArr[9]);
			}
		}
	}
	private void generateDimension(){	//GENERATE BOARD()
		//init ver, hor, and bombNum base on categories
		if (ctgr.equals(GamesCategory.EASY)){ 
			ver = 9;
			hor = 9;
			bombNum = 10;
		} else if (ctgr.equals(GamesCategory.INTRMD)){
			ver = 16;
			hor = 16;
			bombNum = 40;
		} else if (ctgr.equals(GamesCategory.HARD)){
			ver = 16;
			hor = 30;
			bombNum = 99;
		}
		checkBoard = new boolean[ver][hor];
		flagCount = bombNum;
	}
	private void createView(){
		//set Location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/3-this.getSize().width/3, dim.height/3-this.getSize().height/3);
		//set button size:
		btnSize = 22;
		//get SelectionBox
		String[] ctgrStr = {"Select","EASY","INTRMD","HARD"};
		selectBox= new JComboBox<String>(ctgrStr);
		selectBox.setToolTipText("Select");
		selectBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent ae) {
				JComboBox cbox = (JComboBox)ae.getSource();
				Minesweeper.this.dispose();
				String categories = (String)cbox.getSelectedItem();
				if (!categories.equals("Select Categories"))
					new Minesweeper(GamesCategory.valueOf((String)cbox.getSelectedItem()));
			}
		});
		//container: Border Layout
		JPanel container = new JPanel(new BorderLayout());
		getContentPane().add(container);
		//main game Panel: GridBagLayout, contain buttons
		JPanel gamePnl = new JPanel(new GridBagLayout());
		container.add(gamePnl,BorderLayout.CENTER);
		//init JButton board
		gameBoard = new JButton[ver][hor];
		GridBagConstraints gbc = new GridBagConstraints();
		for (int i=0;i<ver;i++){
			gbc.gridy=i;
			for (int j=0;j<hor;j++){
				gbc.gridx=j;
				gameBoard[i][j] = new JButton();
				gameBoard[i][j].setPreferredSize(new Dimension(btnSize,btnSize));
				gamePnl.add(gameBoard[i][j],gbc);
			}
		}
		//Points panel
		JPanel pointPnl = new JPanel(new FlowLayout());
		container.add(pointPnl,BorderLayout.SOUTH);	
		pointPnl.add(selectBox);
		//Up panel
		timer = new Timer(1000,new TimeClass(this)); //1 seconds.
		JPanel upPnl = new JPanel(new FlowLayout());
		container.add(upPnl,BorderLayout.NORTH);
		timerLabel = new JLabel("Time: 0");
		flagCountLabel = new JLabel("Flag: "+String.valueOf(flagCount));
		resetButton = new JButton();
		resetButton.setPreferredSize(new Dimension(btnSize+7,btnSize+7));
		resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Minesweeper.this.dispose();
				new Minesweeper(ctgr);
			}
		});
		upPnl.add(timerLabel);
		upPnl.add(resetButton);
		upPnl.add(flagCountLabel);
		//main JFrame Component
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setResizable(false);
	    setVisible(true);
	    setTitle("Minesweeper Finalize v3.0");
		pack();
	}
	

	public void generateComponent(int y0, int x0){
		board = new char[ver][hor]; //initialize board
		//RANDOMIZE bombs
		for (int i=0;i<bombNum;i++){
			int y = rand.nextInt(ver);
			int x = rand.nextInt(hor);
			if (board[y][x]!='B'&&(y!=y0||x!=x0)) //B denote bomb
				board[y][x] = 'B';
			 else 
				i--;
		}
		//counting bombs
		for (int i=0;i<ver;i++)
			for (int j=0;j<hor;j++)
				if (board[i][j]!='B')
					board[i][j] = (char)(countSurroundingBomb(i, j)+48);	
	}
	public boolean inBound(int y,int x){ 	//check if not out of range
		if (y<0 || y>=ver || x<0 || x>=hor)
			return false;
		return true;
	}
	private int countSurroundingBomb(int y,int x){
		int bombSurround = 0;
		int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
		for (int[] d:dir){ //loop through each dirr
			int y1 = y+d[0];
			int x1 = x+d[1];
			if (inBound(y1,x1))
				if (board[y1][x1]=='B')
					bombSurround++;
		}
		return bombSurround;
	}
	/*//check board code
	public void printCheckBoard(){
		System.out.println();
		for (int i=0;i<ver;i++){
			for(int j=0;j<hor;j++){
				System.out.print((checkBoard[i][j])?"T  ":"F  ");
			}
			System.out.println();
		}
	}*/
	public void setCheckBoard(int y, int x){
		checkBoard[y][x] = true;
	}
	public boolean isCheck(int y, int x){
		return checkBoard[y][x];
	}
	
	/*public void printBoard(){
		for (int i=0;i<ver;i++){
			for (int j=0;j<hor;j++){
				System.out.print(board[i][j]+"  ");
			}
			System.out.println();
		}
	}*/
	
	public void setColorGameBoard(int y,int x){
		int val = Character.valueOf(board[y][x]) - 48;
		setCheckBoard(y, x);
		if (board[y][x]=='0') //empty tiles
			find0(y,x);
		else if (val<9){ //number from 1->8
			gameBoard[y][x].setDisabledIcon(tileIconArr[val]);
			gameBoard[y][x].setIcon(tileIconArr[val]);
		} else if (board[y][x]=='B')
			if(!isFlagged(y, x)){ //bomb
				explode();
				gameBoard[y][x].setIcon(tileIconArr[12]); //12 --> minesource_tile
				gameBoard[y][x].setDisabledIcon(tileIconArr[12]);		
			}
	}
	private void find0(int y,int x){
		gameBoard[y][x].setEnabled(false);
		gameBoard[y][x].setDisabledIcon(tileIconArr[0]);
		checkBoard[y][x]=true;
		int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
		for (int[] d:dir){ //loop through each dirr
			int y1 = y+d[0];
			int x1 = x+d[1];
			if (inBound(y1,x1)){
				if (!checkBoard[y1][x1])
					if (board[y1][x1] == '0') 
		                 find0(y1,x1);
		             else {
		                 setColorGameBoard(y1,x1);
		                 gameBoard[y1][x1].setEnabled(true);
		                 checkBoard[y1][x1] = true;
		             }
			}
				
		}
	}
	public void flagged(int y,int x){
		if (!isFlagged(y,x)){
			flagCount--;
			gameBoard[y][x].setDisabledIcon(tileIconArr[10]);
			gameBoard[y][x].setIcon(tileIconArr[10]);
			gameBoard[y][x].putClientProperty("flag", true);
		} else {
			flagCount++;
			gameBoard[y][x].setEnabled(true);
	        gameBoard[y][x].setIcon(tileIconArr[9]);
	        gameBoard[y][x].setText(null);
	        gameBoard[y][x].putClientProperty("flag", false);
		}

	}
	
	///game condition
	public boolean isFlagged(int y,int x){
		return (boolean) gameBoard[y][x].getClientProperty("flag");
	}
	boolean isFirstPlay() {
		return firstPlay;
	}

	void setFirstPlay(boolean firstPlay) {
		this.firstPlay = firstPlay;
	}
	
	public void wonGame() {	//
	     gameover = true;
	     for (int i = 0; i < ver; i++) 
	         for (int j = 0; j < hor; j++) 
	             if (!checkBoard[i][j] && !isFlagged(i, j)){
	            	 gameover = false;//either check or flag
	            	 break;
	             }        
	     if (gameover && !explode){ //game complete
	    	 resetButton.setIcon(smileyIconArr[2]);
	    	 timer.stop();
	     }
	}
	public void updateFlagLabel(){
		flagCountLabel.setText("Flag: "+String.valueOf(flagCount));
	}
	 private void explode() {
		 explode = true;
		 timer.stop();
	     for (int i = 0; i < ver; i++) 
	         for (int j = 0; j < hor; j++){     
	        	 if (board[i][j]=='B'&&!isFlagged(i, j)){
	        		 gameBoard[i][j].setIcon(tileIconArr[11]); //12 --> minesource_tile
	     			gameBoard[i][j].setDisabledIcon(tileIconArr[11]);	
	        	 }
	        	 gameBoard[i][j].setEnabled(false);
	         }
	     resetButton.setIcon(smileyIconArr[1]);
	 }
	 public boolean isDisable(int y, int x){ //check if number is 
		 return !gameBoard[y][x].isEnabled();
	 }
	 public void clickNumberTile(int y, int x){
		 int needed_flag = Character.valueOf(board[y][x])-48;
		 boolean correctFlag = true; //count if flags are place correctly
		 int flagSurround = 0;	//count number of flag arround
		 int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
		 int y2 =0;
		 int x2 =0; //coordinate of the wrong flag
		 for (int[] d:dir){ //loop through each dirr
			int y1 = y+d[0];
			int x1 = x+d[1];
			
			if (inBound(y1,x1))
				if(isFlagged(y1, x1)){
					flagSurround++;
					if (board[y1][x1]!='B'){
						correctFlag = false;
						y2=y1;
						x2=x1;
					}
				} 
			}
		 if (needed_flag==flagSurround){
			 if (!correctFlag){
				 explode();
				 gameBoard[y2][x2].setIcon(tileIconArr[13]);  //wrong flag
				 gameBoard[y2][x2].setDisabledIcon(tileIconArr[13]);
			 } else {
				 for (int[] d:dir){ //loop through each dirr
					int y1 = y+d[0];
					int x1 = x+d[1];
					if (inBound(y1,x1))
						setColorGameBoard(y1, x1);
				}
			 }
		 }
	 }
	 //time counter
	 public void setTimeLabel(int time){
		 timerLabel.setText("Time: "+String.valueOf(time));
	 }
	 public void startTimer(){
		 timer.start();
	 }
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Minesweeper mines = new Minesweeper(GamesCategory.EASY);
				mines.setVisible(true);
			}
		});
	}
}

enum GamesCategory{EASY, INTRMD, HARD}
