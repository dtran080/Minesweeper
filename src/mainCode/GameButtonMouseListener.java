package mainCode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class GameButtonMouseListener extends MouseAdapter implements MouseListener {
	private int y,x;
	 @Override
     public void mouseClicked(MouseEvent e) {
		 JButton btn = (JButton) e.getSource();
		 Minesweeper mines = (Minesweeper) btn.getClientProperty("Minesweeper");
		 y = (int) btn.getClientProperty("row");
		 x = (int) btn.getClientProperty("col");
		 if (!mines.isDisable(y, x))
			 if(SwingUtilities.isLeftMouseButton(e)){  //left - click			 
				if (mines.isFirstPlay()){ //first click --> generate Board
					mines.startTimer();
					mines.setCheckBoard(y,x);
					mines.generateComponent(y,x);
					mines.setFirstPlay(false);	
					//mines.printBoard();
					mines.setColorGameBoard(y, x);
				} else {
					if (!mines.isCheck(y, x)&&!mines.isFlagged(y, x)){ //click on Button
						mines.setColorGameBoard(y, x);
					} else  //click on Tile
						mines.clickNumberTile(y, x);					
				}
				//mines.printCheckBoard();
			 }
			 else if (SwingUtilities.isRightMouseButton(e)&& !mines.isCheck(y, x) && !mines.isFirstPlay()){		 
				 mines.flagged(y, x); //right click
				 mines.updateFlagLabel();
			 }
		 mines.wonGame();
	 }
}
