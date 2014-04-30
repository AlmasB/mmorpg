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
import uk.ac.brighton.uni.ab607.libs.ui.DoubleBufferWindow;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;

public class StatsGUI extends DoubleBufferWindow {
    /**
     *
     */
    private static final long serialVersionUID = 4879390243081905006L;

    private JLabel attributes = new JLabel();
    private JLabel stats = new JLabel();

    private JButton[] buttons = new JButton[9];
    private JButton[] skillButtons = new JButton[9];

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

        ToolTipManager.sharedInstance().setInitialDelay(0);

        for (int i = 0; i < 9; i++) {
            final int skillValue = i;
            skillButtons[i] = new JButton("");
            skillButtons[i].setLocation(180 + 50 * (i % 3), 25 + 50 * (i / 3));
            skillButtons[i].setSize(40, 40);
            skillButtons[i].setIcon(new ImageIcon(Resources.getImage("enemy.png")));
            skillButtons[i].setEnabled(false);
            skillButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actions.add("SKILL_UP," + p.name + "," + skillValue);
                }
            });
            this.add(skillButtons[i]);
        }

        setVisible(true);
    }

    private boolean equal(Player p) {
        return attributes.getText().equals(p.attributesToPseudoHTML()) && stats.getText().equals(p.statsToPseudoHTML());
    }

    public void update(final Player p) {
        // TODO: refactor when player is no longer created on client side
        if (p != null) {
            Skill[] skills = p.getSkills();
            for (int i = 0; i < skills.length; i++) {
                skillButtons[i].setToolTipText(skills[i].name + " " + "Level: " + skills[i].getLevel() + " " + skills[i].description);
                if (skills[i].getLevel() < Skill.MAX_LEVEL) {
                    skillButtons[i].setEnabled(p.hasSkillPoints());
                }
            }
        }

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
