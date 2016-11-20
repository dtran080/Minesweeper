package mainCode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import mainCode.Minesweeper;

public class TimeClass implements ActionListener {
	private int counter=0;
	private Minesweeper mines;
	
	public TimeClass(Minesweeper mines){
		this.mines = mines;
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        counter++;
        mines.setTimeLabel(counter);
    }
}
