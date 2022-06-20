package views;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import engine.Game;
import engine.Player;
import engine.PriorityQueue;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Effect;
import model.effects.EffectType;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;
import model.world.Hero;

public class GameplayPanel extends JPanel implements ActionListener, KeyListener, MouseListener {

	int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	JLabel info1, info2, curChamp, order, clickedChamp, curTime;
	Champion lastClicked;
	CardLayoutPanel clp;
	Timer time, time2;
	long timer, sec, min;
	ArrayList<KeyEvent> pressed;
	ArrayList<Object[]> toBeColored;
	ArrayList<Object[]> toBeRemoved;
	Game game;
	ArrayList<JButton> buttons;
	HashMap<Champion, JButton> champToButton;
	HashMap<JButton, Champion> buttonToChamp;
	HashMap<Integer, Direction> keyCode;
	Border border;
	JButton useLeaderAbility, endTurnButton;
	int lastDir = -1, lastSingle = -1;
	String message = "-1";
	boolean attacked, casted, wasSingle;

	public String updateInfo(Player player) {
		String s = "<html>Name : " + player.getName() + "<br>";
		s += "Leader : " + player.getLeader().getName() + "<br>";
		s += "Team : ";
		int cnt = 0;
		for (Champion c : player.getTeam())
			s += c.getName() + (++cnt < 3 ? ", " : "<br>");
		s += "UsedLeaderAbility : " + ((player == game.getFirstPlayer() ? game.isFirstLeaderAbilityUsed()
				: game.isSecondLeaderAbilityUsed()) ? "Yes" : "No") + "<br></html>";
		return s;
	}

	public int getDigits(long min2) {
		int ret = 0;
		while (min2 > 0) {
			ret++;
			min2 /= 10;
		}
		return ret;
	}

	public String updateOrder() {
		PriorityQueue tmp = new PriorityQueue(6);
		String s = "<html>Current Champion : " + ((Champion) game.getTurnOrder().peekMin()).getName() + "<br>";
		tmp.insert(game.getTurnOrder().remove());
		while (!game.getTurnOrder().isEmpty()) {
			s += "Next Champion -> " + ((Champion) game.getTurnOrder().peekMin()).getName() + "<br>";
			tmp.insert(game.getTurnOrder().remove());
		}
		while (!tmp.isEmpty())
			game.getTurnOrder().insert(tmp.remove());
		return s;
	}

	public void updateGrid() {
		int cnt = 0;
		buttonToChamp.clear();
		for (int i = 4; i >= 0; i--) {
			for (int j = 0; j < 5; j++) {
				buttons.get(cnt).setBackground(new Color(27, 41, 68));
				buttons.get(cnt).setBorder(border);
				buttons.get(cnt).repaint();
				buttons.get(cnt).revalidate();
				clp.repaint();
				clp.revalidate();
				this.repaint();
				this.revalidate();
				if (game.getBoard()[i][j] != null) {
					if (game.getBoard()[i][j] instanceof Champion) {
						// buttons.get(cnt).setText(((Champion) game.getBoard()[i][j]).getName());
						champToButton.put((Champion) game.getBoard()[i][j], buttons.get(cnt));
						buttonToChamp.put(buttons.get(cnt), (Champion) game.getBoard()[i][j]);
						BufferedImage img = ImagePanel.resize(
								(BufferedImage) ReadImages.hm.get(((Champion) game.getBoard()[i][j]).getName()), 90,
								120);
						ImageIcon icon = new ImageIcon(img);
						buttons.get(cnt).setIcon(icon);
					} else {
						// buttons.get(cnt).setText("Cover");
						BufferedImage img = ImagePanel.resize((BufferedImage) ReadImages.hm.get("shield2"), 90, 120);
						ImageIcon icon = new ImageIcon(img);
						buttons.get(cnt).setIcon(icon);
					}
					buttons.get(cnt).setText("HP: " + ((Damageable) game.getBoard()[i][j]).getCurrentHP());
				} else {
					buttons.get(cnt).setText("");
					buttons.get(cnt).setIcon(null);
				}
				cnt++;
			}
		}
		champToButton.get(game.getCurrentChampion()).setBorder(new LineBorder(Color.yellow, 5));
		champToButton.get(game.getCurrentChampion()).repaint();
		champToButton.get(game.getCurrentChampion()).revalidate();
		this.repaint();
		this.revalidate();
		clp.repaint();
		clp.revalidate();
		clp.frame.repaint();
		clp.frame.revalidate();
		updateCurrentChampion(lastClicked);
	}

	public void updateCurrentChampion() {
		String s = "<html>Current Champion : " + ((Champion) game.getCurrentChampion()).getName() + "<br>";
		s += "Amoung Player "
				+ (game.getFirstPlayer().getTeam().contains((Champion) game.getCurrentChampion()) ? "1" : "2") + "<br>";
		s += "Current HP : " + ((Champion) game.getCurrentChampion()).getCurrentHP() + "<br>";
		s += "Current Mana : " + ((Champion) game.getCurrentChampion()).getMana() + "<br>";
		s += "Current Action Points : " + ((Champion) game.getCurrentChampion()).getCurrentActionPoints() + "<br>";
		s += "Type : " + (((Champion) game.getCurrentChampion()) instanceof Hero ? "Hero"
				: ((Champion) game.getCurrentChampion()) instanceof AntiHero ? "AntiHero" : "Villain") + "<br>";
		s += "Attack Damage : " + ((Champion) game.getCurrentChampion()).getAttackDamage() + "<br>";
		s += "Attack Range : " + ((Champion) game.getCurrentChampion()).getAttackRange() + "<br>";
		s += "List of Abilities : <br>";
		for (Ability a : ((Champion) game.getCurrentChampion()).getAbilities()) {
			s += "Ability Name : " + a.getName() + "<br>";
			s += "Ability Type : "
					+ (a instanceof HealingAbility ? "HealingAbility"
							: a instanceof DamagingAbility ? "DamageingAbility"
									: "CrowdControlAbility (" + ((CrowdControlAbility) a).getEffect().getType() + ")")
					+ "<br>";
			s += "Area of Effect : " + a.getCastArea() + "<br>";
			s += "Ability Cast Range : " + a.getCastRange() + "<br>";
			s += "Mana Cost : " + a.getManaCost() + "<br>";
			s += "Action Cost : " + a.getRequiredActionPoints() + "<br>";
			s += "Current Cooldown : " + a.getCurrentCooldown() + "<br>";
			s += "Base Cooldown : " + a.getBaseCooldown() + "<br>";
			if (a instanceof HealingAbility) {
				s += "Heal Amount : " + ((HealingAbility) a).getHealAmount();
			} else if (a instanceof DamagingAbility) {
				s += "Damage Amount : " + ((DamagingAbility) a).getDamageAmount();
			} else {
				s += "Effect Name : " + ((CrowdControlAbility) a).getEffect().getName() + "<br>";
				s += "Effect Duration : " + ((CrowdControlAbility) a).getEffect().getDuration() + "<br>";
				s += "Effect Type : " + ((CrowdControlAbility) a).getEffect().getType();
			}
			s += "<br>";
		}

		if (!((Champion) game.getCurrentChampion()).getAppliedEffects().isEmpty())
			s += "List of Applied Effects : <br>";

		for (Effect e : ((Champion) game.getCurrentChampion()).getAppliedEffects()) {
			s += "Effect Name : " + e.getName() + "<br>";
			s += "Effect Duration : " + e.getDuration() + "<br>";
			s += "Effect Type : " + e.getType() + "<br>";
		}
		s += "</html>";

		curChamp.setText(s);
		curChamp.repaint();
		curChamp.revalidate();
		clp.repaint();
		clp.revalidate();
		this.repaint();
		this.revalidate();
	}

	public void updateCurrentChampion(Champion c) {
		lastClicked = c;
		if (game.getCurrentChampion() == c || c == null)
			return;
		String s = "<html>Clicked Champion : " + (c).getName() + "<br>";
		s += "Amoung Player " + (game.getFirstPlayer().getTeam().contains(c) ? "1" : "2") + "<br>";
		s += "Current HP : " + (c).getCurrentHP() + "<br>";
		s += "Current Mana : " + (c).getMana() + "<br>";
		s += "Current Action Points : " + (c).getCurrentActionPoints() + "<br>";
		s += "Type : " + ((c) instanceof Hero ? "Hero" : (c) instanceof AntiHero ? "AntiHero" : "Villain") + "<br>";
		s += "Attack Damage : " + (c).getAttackDamage() + "<br>";
		s += "Attack Range : " + (c).getAttackRange() + "<br>";
		s += "List of Abilities : <br>";
		for (Ability a : (c).getAbilities()) {
			s += "Ability Name : " + a.getName() + "<br>";
			s += "Ability Type : "
					+ (a instanceof HealingAbility ? "HealingAbility"
							: a instanceof DamagingAbility ? "DamageingAbility"
									: "CrowdControlAbility (" + ((CrowdControlAbility) a).getEffect().getType() + ")")
					+ "<br>";
			s += "Area of Effect : " + a.getCastArea() + "<br>";
			s += "Ability Cast Range : " + a.getCastRange() + "<br>";
			s += "Mana Cost : " + a.getManaCost() + "<br>";
			s += "Action Cost : " + a.getRequiredActionPoints() + "<br>";
			s += "Current Cooldown : " + a.getCurrentCooldown() + "<br>";
			s += "Base Cooldown : " + a.getBaseCooldown() + "<br>";
			if (a instanceof HealingAbility) {
				s += "Heal Amount : " + ((HealingAbility) a).getHealAmount();
			} else if (a instanceof DamagingAbility) {
				s += "Damage Amount : " + ((DamagingAbility) a).getDamageAmount();
			} else {
				s += "Effect Name : " + ((CrowdControlAbility) a).getEffect().getName() + "<br>";
				s += "Effect Duration : " + ((CrowdControlAbility) a).getEffect().getDuration() + "<br>";
				s += "Effect Type : " + ((CrowdControlAbility) a).getEffect().getType();

			}
			s += "<br>";
		}

		if (!c.getAppliedEffects().isEmpty())
			s += "List of Applied Effects : <br>";

		for (Effect e : c.getAppliedEffects()) {
			s += "Effect Name : " + e.getName() + "<br>";
			s += "Effect Duration : " + e.getDuration() + "<br>";
			s += "Effect Type : " + e.getType() + "<br>";
		}
		s += "</html>";
		clickedChamp.setText(s);
		clickedChamp.repaint();
		clickedChamp.revalidate();
		clp.repaint();
		clp.revalidate();
		this.repaint();
		this.revalidate();
	}

	public GameplayPanel(CardLayoutPanel clp, Game game) {

		this.clp = clp;
		time = new Timer(1, this);
		time.setInitialDelay(0);
		time.start();

		time2 = new Timer(1000, this);
		time2.setInitialDelay(0);
		time2.start();

		toBeColored = new ArrayList<>();
		toBeRemoved = new ArrayList<>();

		buttons = new ArrayList<>();
		champToButton = new HashMap<>();
		buttonToChamp = new HashMap<>();
		keyCode = new HashMap<>();
		keyCode.put(KeyEvent.VK_UP, Direction.UP);
		keyCode.put(KeyEvent.VK_DOWN, Direction.DOWN);
		keyCode.put(KeyEvent.VK_LEFT, Direction.LEFT);
		keyCode.put(KeyEvent.VK_RIGHT, Direction.RIGHT);
		this.game = game;

		pressed = new ArrayList<>();

		JLabel name1 = new JLabel("Player 1");
		name1.setFont(new Font("Mantessa", Font.BOLD, 25));
		name1.setForeground(Color.white);

		info1 = new JLabel(updateInfo(game.getFirstPlayer()));
		info1.setFont(new Font("Mantessa", Font.PLAIN, 13));
		info1.setForeground(Color.white);

		JPanel player1 = new JPanel();
		player1.setLayout(new FlowLayout(10, 50, 10));
		player1.setBounds(0, 0, 500, 100);
		player1.setBackground(Color.gray);
		player1.add(name1);
		player1.add(info1);
		player1.setOpaque(false);

		JLabel name2 = new JLabel("Player 2");
		name2.setFont(new Font("Mantessa", Font.BOLD, 25));
		name2.setForeground(Color.white);

		info2 = new JLabel(updateInfo(game.getSecondPlayer()));
		info2.setFont(new Font("Mantessa", Font.PLAIN, 13));
		info2.setForeground(Color.white);

		JPanel player2 = new JPanel();
		player2.setLayout(new FlowLayout(10, 50, 10));
		player2.setBounds(width - 500, 0, 500, 100);
		player2.setBackground(Color.gray);
		player2.add(name2);
		player2.add(info2);
		player2.setOpaque(false);

		curTime = new JLabel("00 : 00");
		curTime.setFont(new Font("Mantessa", Font.BOLD, 40));
		curTime.setForeground(Color.white);

		JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		timePanel.setBounds(player1.getWidth(), 0, width - player1.getWidth() - player2.getWidth(), 100);
		timePanel.setBackground(Color.gray);
		timePanel.add(curTime);
		timePanel.setOpaque(false);

		JPanel grid = new JPanel();
		grid.setBounds(width / 5, 100, width * 3 / 5, height - 200);
		grid.setLayout(new GridLayout(5, 1));
		grid.setOpaque(false);

		for (int i = 4; i >= 0; i--) {
			for (int j = 0; j < 5; j++) {
				JButton button = new JButton();
				button.addActionListener(this);
				button.addMouseListener(this);
				button.setFocusable(false);
				button.setLayout(null);
				border = button.getBorder();
				button.setForeground(Color.white);
				button.setBackground(new Color(27, 41, 68));
				if (game.getBoard()[i][j] != null) {
					if (game.getBoard()[i][j] instanceof Champion) {
						// button.setText(((Champion) game.getBoard()[i][j]).getName());
						champToButton.put((Champion) game.getBoard()[i][j], button);
						buttonToChamp.put(button, (Champion) game.getBoard()[i][j]);
						BufferedImage img = ImagePanel.resize(
								(BufferedImage) ReadImages.hm.get(((Champion) game.getBoard()[i][j]).getName()), 90,
								120);
						ImageIcon icon = new ImageIcon(img);
						button.setIcon(icon);
					} else {
						BufferedImage img = ImagePanel.resize((BufferedImage) ReadImages.hm.get("shield2"), 90, 120);
						ImageIcon icon = new ImageIcon(img);
						button.setIcon(icon);
						// button.setText("Cover");
					}
					button.setText("HP: " + ((Damageable) game.getBoard()[i][j]).getCurrentHP());
				}
				grid.add(button);
				buttons.add(button);
			}
		}

		border = buttons.get(0).getBorder();
		champToButton.get(game.getCurrentChampion()).setBorder(new LineBorder(Color.yellow, 5));

		order = new JLabel(updateOrder());
		order.setFont(new Font("Mantessa", Font.PLAIN, 13));
		order.setForeground(Color.white);

		JPanel orderPanel = new JPanel();
		orderPanel.setBounds(0, 100, width / 5, height - 100);
		orderPanel.setBackground(Color.gray);
		orderPanel.add(order);
		orderPanel.setOpaque(false);

		JPanel control = new JPanel();
		control.setBounds(width / 5, height - 100, width * 3 / 5, 100);
		control.setBackground(Color.gray);
		control.setOpaque(false);

		curChamp = new JLabel();
		updateCurrentChampion();
		curChamp.setFont(new Font("Mantessa", Font.BOLD, 10));
		curChamp.setForeground(Color.white);

		JPanel curChampPanel = new JPanel();
		curChampPanel.setBounds(width * 4 / 5, 100, width / 5, height - 100);
		curChampPanel.setBackground(Color.gray);
		curChampPanel.add(curChamp);
		curChampPanel.setOpaque(false);

		endTurnButton = new JButton("End Turn");
		endTurnButton.setSize(300, 300);
		endTurnButton.addActionListener(this);
		endTurnButton.setFocusable(false);
		endTurnButton.setForeground(Color.black);
		endTurnButton.setBackground(Color.lightGray);
		control.add(endTurnButton);

		useLeaderAbility = new JButton("Use Leader Ability");
		useLeaderAbility.setSize(300, 300);
		useLeaderAbility.addActionListener(this);
		useLeaderAbility.setFocusable(false);
		useLeaderAbility.setForeground(Color.black);
		useLeaderAbility.setBackground(Color.lightGray);
		orderPanel.add(useLeaderAbility);

		clickedChamp = new JLabel(
				"<html>Clicked Champion will be displayed here.<br>Just click on the champion that <br> you want to display his/her info.</html>");
		clickedChamp.setFont(new Font("Mantessa", Font.BOLD, 9));
		clickedChamp.setForeground(Color.white);
		orderPanel.add(clickedChamp);

		BufferedImage img = (BufferedImage) ReadImages.hm.get("galaxy3");
		ImagePanel ip = new ImagePanel(img);
		ip.setBounds(0, 0, width, height);

		clp.setFocusable(true);
		clp.requestFocus();
		clp.addKeyListener(this);
		this.setLayout(null);
		this.add(player1);
		this.add(player2);
		this.add(timePanel);
		this.add(orderPanel);
		this.add(curChampPanel);
		this.add(control);
		this.add(grid);
		this.add(ip);
		this.setVisible(true);

	}

	public static class MyButton extends JButton {

		private float opacity;

		public void setOpacity(float opacity) {
			this.opacity = opacity;
		}

		public float getOpacity() {
			return this.opacity;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == endTurnButton) {
			try {
				game.endTurn();
				lastDir = -1;
				lastSingle = -1;
			} catch (CloneNotSupportedException e1) {

			}
			updateGrid();
			updateCurrentChampion();
			order.setText(updateOrder());
			lastClicked = null;
			clickedChamp.setText(
					"<html>Clicked Champion will be displayed here.<br>Just click on the champion that <br> you want to display his/her info.</html>");
		} else if (e.getSource() == useLeaderAbility) {
			try {
				game.useLeaderAbility();
				info1.setText(updateInfo(game.getFirstPlayer()));
				info2.setText(updateInfo(game.getSecondPlayer()));
				info1.repaint();
				info1.revalidate();
				info2.repaint();
				info2.revalidate();
				lastSingle = lastDir = -1;
				clp.repaint();
				clp.revalidate();
				this.repaint();
				this.revalidate();
			} catch (LeaderNotCurrentException e1) {
				message = "The Current Champion is not the Leader!";
			} catch (LeaderAbilityAlreadyUsedException e1) {
				message = "You have already used the Leader Ability!";
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateOrder();
			updateGrid();
			updateCurrentChampion();
			if (game.checkGameOver() != null) {
				clp.add(new WinningPanel(game.checkGameOver(), game.checkGameOver() == game.getFirstPlayer() ? 0 : 1),
						"5");
				clp.cl.show(clp, "5");
			}
		} else if (e.getSource() == time) {
			timer++;
			while (!toBeColored.isEmpty()) {
				Object[] a = new Object[3];
				a[0] = toBeColored.get(0)[0];
				a[1] = ((JButton) (toBeColored.get(0)[0])).getBackground();
				a[2] = timer + (int) toBeColored.get(0)[2];
				toBeRemoved.add(a);
				((JButton) (toBeColored.get(0)[0])).setBackground((Color) toBeColored.get(0)[1]);
				toBeColored.remove(0);
			}
			while (!toBeRemoved.isEmpty() && (long) toBeRemoved.get(0)[2] <= timer) {
				((JButton) (toBeRemoved.get(0)[0])).setBackground((Color) toBeRemoved.get(0)[1]);
				toBeRemoved.remove(0);
				updateCurrentChampion();
				updateGrid();
			}
		} else if (e.getSource() == time2) {
			sec++;
			min += sec / 60;
			sec %= 60;
			curTime.setText((getDigits(min) > 1 ? "" : "0") + min + " : " + (getDigits(sec) > 1 ? "" : "0") + sec);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void attackEffect(int x, int y, Direction dir) {
		for (int i = (dir == Direction.UP ? 0 : 4); (dir == Direction.UP ? i < 5
				: i >= 0); i += (dir == Direction.UP ? 1 : -1)) {
			for (int j = (dir == Direction.RIGHT ? 0 : 4); (dir == Direction.RIGHT ? j < 5
					: j >= 0); j += (dir == Direction.RIGHT ? 1 : -1)) {
				int minx = Math.min(game.getCurrentChampion().getLocation().x, x);
				int maxx = Math.max(game.getCurrentChampion().getLocation().x, x);
				int miny = Math.min(game.getCurrentChampion().getLocation().y, y);
				int maxy = Math.max(game.getCurrentChampion().getLocation().y, y);
				if (minx <= i && i <= maxx && miny <= j && j <= maxy && (i != game.getCurrentChampion().getLocation().x
						|| j != game.getCurrentChampion().getLocation().y)) {
					buttons.get((4 - i) * 5 + j).setBackground(Color.red);
					clp.validate();
					clp.repaint();
				}
			}
		}
	}

	public void abilityDirEffect(int x, int y, Direction dir) {
		for (int i = (dir == Direction.UP ? 0 : 4); (dir == Direction.UP ? i < 5
				: i >= 0); i += (dir == Direction.UP ? 1 : -1)) {
			for (int j = (dir == Direction.RIGHT ? 0 : 4); (dir == Direction.RIGHT ? j < 5
					: j >= 0); j += (dir == Direction.RIGHT ? 1 : -1)) {
				int minx = Math.min(game.getCurrentChampion().getLocation().x, x);
				int maxx = Math.max(game.getCurrentChampion().getLocation().x, x);
				int miny = Math.min(game.getCurrentChampion().getLocation().y, y);
				int maxy = Math.max(game.getCurrentChampion().getLocation().y, y);
				if (minx <= i && i <= maxx && miny <= j && j <= maxy && (i != game.getCurrentChampion().getLocation().x
						|| j != game.getCurrentChampion().getLocation().y)) {
					buttons.get((4 - i) * 5 + j).setBackground(Color.magenta);
					clp.validate();
					clp.repaint();
				}
			}
		}
	}

	public void abilitySurroundEffect() {
		Champion c = game.getCurrentChampion();
		ArrayList<Point> al = new ArrayList<>();
		al.add(new Point(c.getLocation().x - 1, c.getLocation().y));
		al.add(new Point(c.getLocation().x - 1, c.getLocation().y - 1));
		al.add(new Point(c.getLocation().x - 1, c.getLocation().y + 1));
		al.add(new Point(c.getLocation().x + 1, c.getLocation().y));
		al.add(new Point(c.getLocation().x + 1, c.getLocation().y + 1));
		al.add(new Point(c.getLocation().x + 1, c.getLocation().y - 1));
		al.add(new Point(c.getLocation().x, c.getLocation().y + 1));
		al.add(new Point(c.getLocation().x, c.getLocation().y - 1));
		for (Point p : al) {
			if (p.x < 5 && p.x >= 0 && p.y < 5 && p.y >= 0) {
				buttons.get((4 - p.x) * 5 + p.y).setBackground(Color.magenta);
				clp.validate();
				clp.repaint();
			}
		}
	}

	public void abilityTeamTargetEffect(Ability a) {
		boolean good = a instanceof HealingAbility || (a instanceof CrowdControlAbility
				&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF);
		if (good) {
			if (game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())) {
				for (Champion c : game.getFirstPlayer().getTeam()) {
					if (game.inRange(game.getCurrentChampion(), c, a.getCastRange())) {
						buttons.get((4 - c.getLocation().x) * 5 + c.getLocation().y).setBackground(Color.magenta);
						clp.validate();
						clp.repaint();
					}
				}
			} else {
				for (Champion c : game.getSecondPlayer().getTeam()) {
					if (game.inRange(game.getCurrentChampion(), c, a.getCastRange())) {
						buttons.get((4 - c.getLocation().x) * 5 + c.getLocation().y).setBackground(Color.magenta);
						clp.validate();
						clp.repaint();
					}
				}
			}
		} else {
			if (game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())) {
				for (Champion c : game.getSecondPlayer().getTeam()) {
					if (game.inRange(game.getCurrentChampion(), c, a.getCastRange())) {
						buttons.get((4 - c.getLocation().x) * 5 + c.getLocation().y).setBackground(Color.magenta);
						clp.validate();
						clp.repaint();
					}
				}
			} else {
				for (Champion c : game.getFirstPlayer().getTeam()) {
					if (game.inRange(game.getCurrentChampion(), c, a.getCastRange())) {
						buttons.get((4 - c.getLocation().x) * 5 + c.getLocation().y).setBackground(Color.magenta);
						clp.validate();
						clp.repaint();
					}
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!pressed.isEmpty() && pressed.get(pressed.size() - 1).getKeyCode() == e.getKeyCode()
				|| e.getKeyCode() == 18)
			return;
		pressed.add(e);
		lastSingle = -1;
		if (pressed.size() == 1) {
			if (e.getKeyCode() == KeyEvent.VK_UP && lastDir == -1) {
				try {
					game.move(Direction.UP);
					updateCurrentChampion();
					updateGrid();
				} catch (UnallowedMovementException e1) {
					message = "You can't move in this direction";
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN && lastDir == -1) {
				try {
					game.move(Direction.DOWN);
					updateCurrentChampion();
					updateGrid();
				} catch (UnallowedMovementException e1) {
					message = "You can't move in this direction";
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				}
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT && lastDir == -1) {
				try {
					game.move(Direction.LEFT);
					updateCurrentChampion();
					updateGrid();
				} catch (UnallowedMovementException e1) {
					message = "You can't move in this direction";
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				}
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && lastDir == -1) {
				try {
					game.move(Direction.RIGHT);
					updateCurrentChampion();
					updateGrid();
				} catch (UnallowedMovementException e1) {
					message = "You can't move in this direction";
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				}
			} else if (e.getKeyChar() == 'e') {
				try {
					game.endTurn();
					lastSingle = -1;
					updateCurrentChampion();
					updateGrid();
					order.setText(updateOrder());
					lastClicked = null;
					clickedChamp.setText(
							"<html>Clicked Champion will be displayed here.<br>Just click on the champion that <br> you want to display his/her info.</html>");
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if (e.getKeyChar() >= '1' && e.getKeyChar() <= '3'
					&& !game.getCurrentChampion().getAbilities().isEmpty()) {
				if (game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1)
						.getCastArea() == AreaOfEffect.SELFTARGET) {
					try {
						game.castAbility(game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1));
						updateCurrentChampion();
						buttons.get((4 - game.getCurrentChampion().getLocation().x) * 5
								+ game.getCurrentChampion().getLocation().y).setBackground(Color.magenta);
						casted = true;
					} catch (AbilityUseException e1) {
						message = "Ability cannot be casted";
					} catch (NotEnoughResourcesException e1) {
						message = "You don't have enough resources";
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1)
						.getCastArea() == AreaOfEffect.TEAMTARGET) {
					try {
						game.castAbility(game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1));
						abilityTeamTargetEffect(game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1));
						updateCurrentChampion();
						casted = true;
					} catch (AbilityUseException e1) {
						message = "Ability cannot be casted";
					} catch (NotEnoughResourcesException e1) {
						message = "You don't have enough resources";
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1)
						.getCastArea() == AreaOfEffect.SURROUND) {
					try {
						game.castAbility(game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1));
						abilitySurroundEffect();
						updateCurrentChampion();
						casted = true;
					} catch (AbilityUseException e1) {
						message = "Ability cannot be casted";
					} catch (NotEnoughResourcesException e1) {
						message = "You don't have enough resources";
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (game.getCurrentChampion().getAbilities().get(e.getKeyChar() - '0' - 1)
						.getCastArea() == AreaOfEffect.DIRECTIONAL) {
					lastDir = e.getKeyChar() - '0' - 1;
				} else {
					lastSingle = e.getKeyChar() - '0' - 1;
				}
			} else if (keyCode.containsKey(e.getKeyCode()) && lastDir != -1) {
				try {
					game.castAbility(game.getCurrentChampion().getAbilities().get(lastDir),
							keyCode.get(e.getKeyCode()));
					updateCurrentChampion();
					ArrayList<Damageable> al = game.getAllDamageablesInRange(game.getCurrentChampion(),
							game.getCurrentChampion().getAbilities().get(lastDir), keyCode.get(e.getKeyCode()),
							game.getFirstPlayer().getTeam().contains(game.getCurrentChampion()));
					if (al.isEmpty()) {
						if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							al.add(new Cover(
									game.getCurrentChampion().getLocation().x
											- game.getCurrentChampion().getAbilities().get(lastDir).getCastRange(),
									game.getCurrentChampion().getLocation().y));
						} else if (e.getKeyCode() == KeyEvent.VK_UP) {
							al.add(new Cover(
									game.getCurrentChampion().getLocation().x
											+ game.getCurrentChampion().getAbilities().get(lastDir).getCastRange(),
									game.getCurrentChampion().getLocation().y));
						} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							al.add(new Cover(game.getCurrentChampion().getLocation().x,
									game.getCurrentChampion().getLocation().y
											- game.getCurrentChampion().getAbilities().get(lastDir).getCastRange()));
						} else {
							al.add(new Cover(game.getCurrentChampion().getLocation().x,
									game.getCurrentChampion().getLocation().y
											+ game.getCurrentChampion().getAbilities().get(lastDir).getCastRange()));
						}
					}
					abilityDirEffect(al.get(al.size() - 1).getLocation().x, al.get(al.size() - 1).getLocation().y,
							keyCode.get(e.getKeyCode()));
					casted = true;
				} catch (AbilityUseException e1) {
					message = "Ability cannot be casted";
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					lastDir = -1;
				}
			}
			if (!keyCode.containsKey(e.getKeyCode())
					&& (!(e.getKeyChar() >= '1' && e.getKeyChar() <= '3')
							|| (!game.getCurrentChampion().getAbilities().isEmpty() && e.getKeyChar() >= '1'
									&& e.getKeyChar() <= '3' && !(game.getCurrentChampion().getAbilities()
											.get(e.getKeyChar() - '0' - 1).getCastArea() == AreaOfEffect.DIRECTIONAL)))
					&& lastDir != -1) {
				lastDir = -1;
			}
		} else if (pressed.size() == 2) {
			lastDir = -1;
			if (pressed.get(0).getKeyCode() == KeyEvent.VK_SHIFT) {
				try {
					if (pressed.get(1).getKeyCode() == KeyEvent.VK_UP) {
						Damageable p = game.getDamageableInRange(game.getCurrentChampion(), Direction.UP,
								game.getFirstPlayer().getTeam().contains(game.getCurrentChampion()));
						game.attack(Direction.UP);
						if (p == null)
							p = new Cover(
									game.getCurrentChampion().getLocation().x
											+ game.getCurrentChampion().getAttackRange(),
									game.getCurrentChampion().getLocation().y);
						attackEffect(p.getLocation().x, p.getLocation().y, Direction.UP);
						attacked = true;
						updateCurrentChampion();
					} else if (pressed.get(1).getKeyCode() == KeyEvent.VK_DOWN) {
						Damageable p = game.getDamageableInRange(game.getCurrentChampion(), Direction.DOWN,
								game.getFirstPlayer().getTeam().contains(game.getCurrentChampion()));
						game.attack(Direction.DOWN);
						if (p == null)
							p = new Cover(
									game.getCurrentChampion().getLocation().x
											- game.getCurrentChampion().getAttackRange(),
									game.getCurrentChampion().getLocation().y);
						attackEffect(p.getLocation().x, p.getLocation().y, Direction.DOWN);
						attacked = true;
						updateCurrentChampion();
					} else if (pressed.get(1).getKeyCode() == KeyEvent.VK_LEFT) {
						Damageable p = game.getDamageableInRange(game.getCurrentChampion(), Direction.LEFT,
								game.getFirstPlayer().getTeam().contains(game.getCurrentChampion()));
						game.attack(Direction.LEFT);
						if (p == null)
							p = new Cover(game.getCurrentChampion().getLocation().x,
									game.getCurrentChampion().getLocation().y
											- game.getCurrentChampion().getAttackRange());
						attackEffect(p.getLocation().x, p.getLocation().y, Direction.LEFT);
						attacked = true;
						updateCurrentChampion();
					} else {
						Damageable p = game.getDamageableInRange(game.getCurrentChampion(), Direction.RIGHT,
								game.getFirstPlayer().getTeam().contains(game.getCurrentChampion()));
						game.attack(Direction.RIGHT);
						if (p == null)
							p = new Cover(game.getCurrentChampion().getLocation().x,
									game.getCurrentChampion().getLocation().y
											+ game.getCurrentChampion().getAttackRange());
						attackEffect(p.getLocation().x, p.getLocation().y, Direction.RIGHT);
						attacked = true;
						updateCurrentChampion();
					}
				} catch (NotEnoughResourcesException e1) {
					message = "You don't have enough resources";
				} catch (ChampionDisarmedException e1) {
					message = "Champion Disarmed";
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (attacked) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException j) {
				// TODO Auto-generated catch block
				j.printStackTrace();
			}
			attacked = false;
			updateGrid();
			if (game.checkGameOver() != null) {
				clp.add(new WinningPanel(game.checkGameOver(), game.checkGameOver() == game.getFirstPlayer() ? 0 : 1),
						"5");
				clp.cl.show(clp, "5");
			}
		} else if (casted) {
			if (!wasSingle) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException j) {
					// TODO Auto-generated catch block
					j.printStackTrace();
				}
			}
			casted = wasSingle = false;
			updateGrid();
			if (game.checkGameOver() != null) {
				clp.add(new WinningPanel(game.checkGameOver(), game.checkGameOver() == game.getFirstPlayer() ? 0 : 1),
						"5");
				clp.cl.show(clp, "5");
			}
		}
		for (int i = 0; i < pressed.size(); i++) {
			if (e.getKeyCode() == pressed.get(i).getKeyCode()) {
				pressed.remove(i);
				break;
			}
		}
		if (!message.equals("-1") && pressed.isEmpty()) {
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, message, "Alert", JOptionPane.WARNING_MESSAGE);
			message = "-1";
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (buttons.contains(e.getSource()) && lastSingle != -1) {
			try {
				int idx = buttons.indexOf(e.getSource());
				game.castAbility(game.getCurrentChampion().getAbilities().get(lastSingle), 4 - idx / 5, idx % 5);
				Color c = buttons.get(idx).getBackground();
				Object[] a = new Object[3];
				a[0] = buttons.get(idx);
				a[1] = Color.magenta;
				a[2] = 100;
				toBeColored.add(a);
				updateCurrentChampion();
				casted = wasSingle = true;
			} catch (AbilityUseException e1) {
				message = "Ability cannot be casted";
			} catch (NotEnoughResourcesException e1) {
				message = "You don't have enough resources";
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidTargetException e1) {
				message = "Casting ability on this cell is invalid";
			}
		} else if (buttons.contains(e.getSource())) {
			JButton b = (JButton) e.getSource();
			if (buttonToChamp.getOrDefault(b, null) != null) {
				updateCurrentChampion(buttonToChamp.get(b));
			}
		}
		lastSingle = -1;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!message.equals("-1")) {
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, message, "Alert", JOptionPane.WARNING_MESSAGE);
			message = "-1";
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
