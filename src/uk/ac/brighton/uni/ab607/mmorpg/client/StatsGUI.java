package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.ui.DoubleBufferWindow;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Skill;

public class StatsGUI extends DoubleBufferWindow {

    /**
     *
     */
    private static final long serialVersionUID = 4879390243081905006L;

    //private Player player;

    private JLabel attributes = new JLabel();
    private JLabel stats = new JLabel();

    private JButton[] buttons = new JButton[9];

    private JButton btn = new JButton();    // skill icon test

    //make it private
    public ArrayList<String> actions = new ArrayList<String>();

    public StatsGUI(final Player p) {
        super(640, 304, "Char Stats/Skills Window", true);
        this.setLocation(0, 720);

        attributes.setBounds(0, 0, 320, 304);
        stats.setBounds(320, 0, 320, 304);

        attributes.setFocusable(false);
        stats.setFocusable(false);

        attributes.setVerticalAlignment(SwingConstants.TOP);
        stats.setVerticalAlignment(SwingConstants.TOP);

        attributes.setFont(new Font("Courier", Font.PLAIN, 22));
        stats.setFont(new Font("Courier", Font.PLAIN, 18));

        this.add(attributes);
        this.add(stats);

        for (int i = GameCharacter.STR; i <= GameCharacter.LUC; i++) {
            final int attr = i;
            buttons[i] = new JButton("+");
            buttons[i].setBounds(125, 3 + 30 * i, 45, 28);
            buttons[i].setFont(new Font("Courier", Font.PLAIN, 18));
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actions.add("ATTR_UP," + p.name + "," + attr);
                }
            });
            add(buttons[i]);
        }

        //player = p;

        // only keep buttons enabled when there is at least 1 attribute point

        // test
        ToolTipManager.sharedInstance().setInitialDelay(0);


        btn.setLocation(200, 150);
        btn.setSize(40, 40);
        btn.setIcon(new ImageIcon(Resources.getImage("enemy.png")));
        btn.setToolTipText("Heal. Restores HP to target");

        this.add(btn);

        // end of test

        setVisible(true);
    }

    private boolean equal(Player p) {
        return attributes.getText().equals(p.attributesToPseudoHTML()) && stats.getText().equals(p.statsToPseudoHTML());
    }

    public void update(final Player p) {
        if (p == null || equal(p))
            return;

        if (!p.hasAttributePoints()) {
            for (JButton btn : buttons)
                btn.setVisible(false);
        }
        else {
            for (int i = GameCharacter.STR; i <= GameCharacter.LUC; i++) {
                buttons[i].setVisible(p.getBaseAttribute(i) < 100);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                attributes.setText(p.attributesToPseudoHTML());
                stats.setText(p.statsToPseudoHTML());
            }
        });
    }

    @Override
    protected void createPicture(Graphics2D g) {
        attributes.repaint();
        stats.repaint();
    }
}
