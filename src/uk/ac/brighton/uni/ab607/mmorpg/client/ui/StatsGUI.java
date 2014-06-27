package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClassChanger;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;

public class StatsGUI extends GUI {
    /**
     * 
     */
    private static final long serialVersionUID = -5781436876701869521L;
    
    private JLabel attributes = new JLabel();
    private JLabel stats = new JLabel();

    private JButton[] buttons = new JButton[9];
    private JButton[] skillButtons = new JButton[9];
    
    private JButton classChangeButton = new JButton("Change Class");
    
    private Player player;
    
    public StatsGUI(final String playerName) {
        super(640, 304, "Char Stats/Skills Window");
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setFocusableWindowState(false);
        this.setLocation(0, 44);

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
            buttons[i].addActionListener(e 
                    -> addActionRequest(new ActionRequest(Action.ATTR_UP, playerName, attr)));
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
            skillButtons[i].addActionListener(e
                    -> addActionRequest(new ActionRequest(Action.SKILL_UP, playerName, skillValue)));
            this.add(skillButtons[i]);
        }

        classChangeButton.addActionListener(event -> {
            if (player == null) return;
            
            GameCharacterClass[] options = GameCharacterClassChanger.getAscensionClasses(player);
            GameCharacterClass chosen = (GameCharacterClass) this.showInputDialog("Choose your class", "Ascension", (Object[]) options);
            if (chosen != null)
                this.addActionRequest(new ActionRequest(Action.CHANGE_CLASS, player.name, chosen.toString()));
        });
        classChangeButton.setBounds(500, 5, 125, 30);
        classChangeButton.setVisible(false);
        this.add(classChangeButton);
    }
    
    private boolean equal(Player p) {
        return attributes.getText().equals(p.attributesToPseudoHTML()) && stats.getText().equals(p.statsToPseudoHTML());
    }

    public void update(final Player p) {
        if (p != null) {
            player = p;
            
            Skill[] skills = p.getSkills();
            for (int i = 0; i < skills.length; i++) {
                skillButtons[i].setToolTipText("<html><b>" + skills[i].name + "</b><br>"
                        + "Level: <font color=green><b>" + skills[i].getLevel() + "</b></font><br>"
                        + skills[i].description + "</html>");
                
                if (skills[i].getLevel() < Skill.MAX_LEVEL) {
                    skillButtons[i].setEnabled(p.hasSkillPoints());
                }
            }
            
            classChangeButton.setVisible(GameCharacterClassChanger.canChangeClass(p));
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

        SwingUtilities.invokeLater(() -> {
            attributes.setText(p.attributesToPseudoHTML());
            stats.setText(p.statsToPseudoHTML());
        });
    }

    @Override
    protected void createPicture(Graphics2D g) {
        attributes.repaint();
        stats.repaint();
    }
    
    /**
     * Creates and shows <b>input</b> dialog with given message and title and
     * provides with given options.
     * 
     * If no options are provided any user input will be valid
     * 
     * @param message
     *            the dialog message
     * @param title
     *            the dialog title
     * @param options
     *            selectable values
     * @return user chosen option/typed value or {@code null} if dialog was
     *         cancelled
     */
    public Object showInputDialog(String message, String title,
            Object... options) {
        return JOptionPane.showInputDialog(this, message, title,
                JOptionPane.PLAIN_MESSAGE, null, options.length > 0 ? options
                        : null, options.length > 0 ? options[0] : null);
    }
}
