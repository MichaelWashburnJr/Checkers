import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * This class creates an instructions pane for the user.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class Instructions extends JFrame{

	public Instructions(GUIView root){
		super("Instructions:");
		this.setSize(400, 200);
		this.setResizable(false);
		
		//center this window on the root pane
		int x = root.getX() + root.getWidth()/2 - this.getWidth()/2;
		int y = root.getY() + root.getHeight()/2 - this.getHeight()/2;
		this.setLocation(x, y);
		
		//create text area
		JTextArea text = new JTextArea();
		text.setText(
				"How To Play:\n" +
				"Pieces can only be moved in diagonal directions." +
				" They may jump over an opponents piece by moving" +
				" to the space behind the opponent, if your piece," +
				" the opponents piece, and the empty square are all" +
				" in a diagonal line.  You may also make multiple" +
				" jumps with the same piece in a single turn if the" +
				" game permits you to. To move a piece select the" +
				" space on the board that the piece is in. Then Select" +
				" the space you would like to move this piece to. If" +
				" it is a valid move, the board will update. If not," +
				" then you will have to select another piece to move." +
				" At the end of your turn (when you have moved the" +
				" piece you want), click the Next Move button to tell" +
				" your opponent to move.  When you reach the row" +
				" farthest from your starting row, the piece that" +
				" lands there will be kinged. This means that this" +
				" piece can now move backwards. To win, capture all" +
				" of your opponents pieces by jumping over them to the" +
				" space behind them."
					);
		text.setLineWrap(true);
		text.setEditable(false);
		text.setFont(new Font("Arial",Font.BOLD,12));
		//create scrolling feature
		JScrollPane pane = new JScrollPane(text);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(pane);
		
		//create start button
		JButton start = new JButton("Start");
		start.addActionListener(new Start(this));
		this.add(start,BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
}

/**
 * Listener for the start button. When this button is
 *  pressed this window will be disposed.
 */
class Start implements ActionListener{
	private Instructions pane;
	public Start(Instructions pane){
		this.pane = pane;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		this.pane.dispose();	
	}
	
}
