package engine;

import java.awt.Point;
import java.io.*;
import java.util.*;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.GameActionException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {
	private Player firstPlayer;
	private Player secondPlayer;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private Object[][] board;
	private static ArrayList<Champion> availableChampions = new ArrayList<>();
	private static ArrayList<Ability> availableAbilities = new ArrayList<>();
	private PriorityQueue turnOrder;
	private final static int BOARDHEIGHT = 5;
	private final static int BOARDWIDTH = 5;

	public Game(Player first, Player second) {
		firstPlayer = first;
		secondPlayer = second;
		board = new Object[BOARDHEIGHT][BOARDWIDTH];
		availableAbilities = new ArrayList<>();
		availableChampions = new ArrayList<>();
		turnOrder = new PriorityQueue(6);
		placeChampions();
		placeCovers();
	}

	private void placeChampions() {
		ArrayList<Champion> firstList = firstPlayer.getTeam();
		ArrayList<Champion> secondList = secondPlayer.getTeam();
		int counter = 1;
		for (Champion ce : firstList) {
			ce.setLocation(new Point(0, counter));
			board[0][counter++] = ce;
			turnOrder.insert(ce);
		}
		counter = 1;
		for (Champion ce : secondList) {
			ce.setLocation(new Point(BOARDHEIGHT - 1, counter));
			board[BOARDHEIGHT - 1][counter++] = ce;
			turnOrder.insert(ce);
		}
	}

	private boolean validPlace(int row, int column) {
		return row > 0 && row < BOARDHEIGHT - 1 && board[row][column] == null;
	}

	private void placeCovers() {
		for (int cover = 0; cover < 5; cover++) {
			int row, column;
			do {
				row = (int) (Math.random() * BOARDHEIGHT);
				column = (int) (Math.random() * BOARDWIDTH);
			} while (!validPlace(row, column));
			board[row][column] = new Cover(row, column);
		}
	}

	public static Effect getEffect(String s, int x) {
		Effect e;
		if (s.equals("Dodge"))
			e = new Dodge(x);
		else if (s.equals("Disarm"))
			e = new Disarm(x);
		else if (s.equals("Embrace"))
			e = new Embrace(x);
		else if (s.equals("PowerUp"))
			e = new PowerUp(x);
		else if (s.equals("Root"))
			e = new Root(x);
		else if (s.equals("Shield"))
			e = new Shield(x);
		else if (s.equals("Shock"))
			e = new Shock(x);
		else if (s.equals("Silence"))
			e = new Silence(x);
		else if (s.equals("SpeedUp"))
			e = new SpeedUp(x);
		else
			e = new Stun(x);
		return e;
	}

	public static void loadAbilities(String filePath) throws IOException {
		Scanner sc = new Scanner(new FileReader(filePath));
		while (sc.ready()) {
			String[] line = sc.nextLine().split(",");
			if (line[0].equals("DMG")) {
				availableAbilities.add(new DamagingAbility(line[1], Integer.parseInt(line[2]),
						Integer.parseInt(line[4]), Integer.parseInt(line[3]), AreaOfEffect.valueOf(line[5]),
						Integer.parseInt(line[6]), Integer.parseInt(line[7])));
			} else if (line[0].equals("CC")) {
				availableAbilities.add(new CrowdControlAbility(line[1], Integer.parseInt(line[2]),
						Integer.parseInt(line[4]), Integer.parseInt(line[3]), AreaOfEffect.valueOf(line[5]),
						Integer.parseInt(line[6]), getEffect(line[7], Integer.parseInt(line[8]))));
			} else {
				availableAbilities.add(new HealingAbility(line[1], Integer.parseInt(line[2]), Integer.parseInt(line[4]),
						Integer.parseInt(line[3]), AreaOfEffect.valueOf(line[5]), Integer.parseInt(line[6]),
						Integer.parseInt(line[7])));
			}
			assert availableAbilities.get(availableAbilities.size() - 1) != null : "Balabizo";
			Champion.stringToAbility.put(line[1], availableAbilities.get(availableAbilities.size() - 1));
		}

	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	public static void loadChampions(String filePath) throws IOException {
		Scanner sc = new Scanner(new FileReader(filePath));
		while (sc.ready()) {
			String[] line = sc.nextLine().split(",");
			if (line[0].equals("H")) {
				Hero h = new Hero(line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]),
						Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
						Integer.parseInt(line[7]), line[8], line[9], line[10]);
				availableChampions.add(h);
			} else if (line[0].equals("A")) {
				AntiHero h = new AntiHero(line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]),
						Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
						Integer.parseInt(line[7]), line[8], line[9], line[10]);
				availableChampions.add(h);
			} else {
				Villain h = new Villain(line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]),
						Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
						Integer.parseInt(line[7]), line[8], line[9], line[10]);
				availableChampions.add(h);
			}
		}

	}

	public Champion getCurrentChampion() {
		return (Champion) getTurnOrder().peekMin();
	}

	public Player checkGameOver() {
		if (getFirstPlayer().getTeam().size() == 0)
			return getSecondPlayer();

		if (getSecondPlayer().getTeam().size() == 0)
			return getFirstPlayer();

		return null;
	}

	private boolean valid(Champion currentChampion, Direction d) {
		if (currentChampion.getCondition() == Condition.ROOTED)
			return false;
		if (d == Direction.UP) {
			return currentChampion.getLocation().x + 1 < BOARDHEIGHT
					&& board[currentChampion.getLocation().x + 1][currentChampion.getLocation().y] == null;
		} else if (d == Direction.DOWN) {
			return currentChampion.getLocation().x - 1 >= 0
					&& board[currentChampion.getLocation().x - 1][currentChampion.getLocation().y] == null;
		} else if (d == Direction.LEFT) {
			return currentChampion.getLocation().y - 1 >= 0
					&& board[currentChampion.getLocation().x][currentChampion.getLocation().y - 1] == null;
		} else {
			return currentChampion.getLocation().y + 1 < BOARDWIDTH
					&& board[currentChampion.getLocation().x][currentChampion.getLocation().y + 1] == null;
		}
	}

	private void moveIntoDirection(Champion currentChampion, Direction d) throws UnallowedMovementException {
		if (valid(currentChampion, d)) {
			board[currentChampion.getLocation().x][currentChampion.getLocation().y] = null;
			if (d == Direction.UP)
				currentChampion.getLocation().x++;
			else if (d == Direction.DOWN)
				currentChampion.getLocation().x--;
			else if (d == Direction.LEFT)
				currentChampion.getLocation().y--;
			else
				currentChampion.getLocation().y++;
			currentChampion.setCurrentActionPoints(currentChampion.getCurrentActionPoints() - 1);
			board[currentChampion.getLocation().x][currentChampion.getLocation().y] = currentChampion;
			return;
		}
		throw new UnallowedMovementException();
	}

	public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException {
		Champion currentChampion = getCurrentChampion();
		if (currentChampion.getCurrentActionPoints() > 0) {
			moveIntoDirection(currentChampion, d);
			return;
		}
		throw new NotEnoughResourcesException();
	}

	public boolean getWhichTeam(Champion c) {
		for (Champion c2 : firstPlayer.getTeam())
			if (c == c2)
				return true;
		return false;
	}

	public Damageable getDamageableInRange(Champion c, Direction d, boolean team) {
		int range = c.getAttackRange();
		if (d == Direction.DOWN) {
			for (int i = c.getLocation().x - 1; i >= Math.max(0, c.getLocation().x - range); i--)
				if (board[i][c.getLocation().y] != null && (board[i][c.getLocation().y] instanceof Cover
						|| getWhichTeam((Champion) board[i][c.getLocation().y]) != team))
					return (Damageable) board[i][c.getLocation().y];
		} else if (d == Direction.UP) {
			for (int i = c.getLocation().x + 1; i < Math.min(BOARDHEIGHT, c.getLocation().x + range + 1); i++)
				if (board[i][c.getLocation().y] != null && (board[i][c.getLocation().y] instanceof Cover
						|| getWhichTeam((Champion) board[i][c.getLocation().y]) != team))
					return (Damageable) board[i][c.getLocation().y];
		} else if (d == Direction.LEFT) {
			for (int j = c.getLocation().y - 1; j >= Math.max(0, c.getLocation().y - range); j--)
				if (board[c.getLocation().x][j] != null && (board[c.getLocation().x][j] instanceof Cover
						|| getWhichTeam((Champion) board[c.getLocation().x][j]) != team))
					return (Damageable) board[c.getLocation().x][j];
		} else {
			for (int j = c.getLocation().y + 1; j < Math.min(BOARDWIDTH, c.getLocation().y + range + 1); j++)
				if (board[c.getLocation().x][j] != null && (board[c.getLocation().x][j] instanceof Cover
						|| getWhichTeam((Champion) board[c.getLocation().x][j]) != team))
					return (Damageable) board[c.getLocation().x][j];
		}
		return null;
	}

	public void attack(Direction d)
			throws CloneNotSupportedException, NotEnoughResourcesException, ChampionDisarmedException {
		Champion currentChampion = getCurrentChampion();
		boolean disarm = false;
		for (Effect e : currentChampion.getAppliedEffects())
			if (e instanceof Disarm)
				disarm = true;
		if (disarm)
			throw new ChampionDisarmedException();
		boolean team = getWhichTeam(currentChampion);
		if (currentChampion.getCurrentActionPoints() > 1) {
			Damageable firstDamageableInRange = getDamageableInRange(currentChampion, d, team);
			if (firstDamageableInRange != null) {
				if (firstDamageableInRange instanceof Cover) {
					firstDamageableInRange
							.setCurrentHP(firstDamageableInRange.getCurrentHP() - currentChampion.getAttackDamage());
					removeKO();
				} else {
					boolean extraDamage = false;
					if (currentChampion instanceof Hero && !(firstDamageableInRange instanceof Hero))
						extraDamage = true;
					else if (currentChampion instanceof Villain && !(firstDamageableInRange instanceof Villain))
						extraDamage = true;
					else if (currentChampion instanceof AntiHero && !(firstDamageableInRange instanceof AntiHero))
						extraDamage = true;
					firstDamageableInRange.setCurrentHP(firstDamageableInRange.getCurrentHP()
							- (int) (currentChampion.getAttackDamage() * (extraDamage ? (150.0 / 100) : 1)));
					if (firstDamageableInRange.getCurrentHP() == 0)
						((Champion) firstDamageableInRange).setCondition(Condition.KNOCKEDOUT);
					removeKO();
				}
			}
			currentChampion.setCurrentActionPoints(currentChampion.getCurrentActionPoints() - 2);
			return;
		}
		throw new NotEnoughResourcesException();
	}

	private void castAbilityHelper(Champion currentChampion, Ability a, ArrayList<Damageable> x)
			throws CloneNotSupportedException, AbilityUseException, NotEnoughResourcesException {
		if (a.getCurrentCooldown() > 0)
			throw new AbilityUseException();
		if (currentChampion.getCurrentActionPoints() >= a.getRequiredActionPoints()
				&& currentChampion.getMana() >= a.getManaCost()) {
			a.execute(x);
			currentChampion
					.setCurrentActionPoints(currentChampion.getCurrentActionPoints() - a.getRequiredActionPoints());
			currentChampion.setMana(currentChampion.getMana() - a.getManaCost());
			a.setCurrentCooldown(a.getBaseCooldown());
			removeKO();
			return;
		}
		throw new NotEnoughResourcesException();
	}

	public boolean inRange(Damageable c1, Damageable c2, int range) {
		return Math.abs(c1.getLocation().x - c2.getLocation().x)
				+ Math.abs(c1.getLocation().y - c2.getLocation().y) <= range;
	}

	public void castAbility(Ability a)
			throws CloneNotSupportedException, AbilityUseException, NotEnoughResourcesException {
		Champion currentChampion = getCurrentChampion();
		ArrayList<Damageable> x = new ArrayList<>();
		if (currentChampion.getAbilities() == null)
			throw new AbilityUseException();
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			x.add(currentChampion);
		} else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			int i = currentChampion.getLocation().x;
			int j = currentChampion.getLocation().y - 1;
			int cnt = 3;
			while (cnt-- > 0) {
				if (!(i - 1 >= 0 && j < BOARDWIDTH && j >= 0)
						|| (board[i - 1][j] instanceof Cover
								&& (a instanceof CrowdControlAbility || a instanceof HealingAbility))
						|| board[i - 1][j] == null
						|| (!(board[i - 1][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i - 1][j]) != getWhichTeam(currentChampion)
								&& (a instanceof HealingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)))
						|| (!(board[i - 1][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i - 1][j]) == getWhichTeam(currentChampion)
								&& (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)))) {
					j++;
					continue;
				}
				x.add((Damageable) board[i - 1][j]);
				j++;
			}
			j = currentChampion.getLocation().y - 1;
			cnt = 3;
			while (cnt-- > 0) {
				if (!(i + 1 < BOARDHEIGHT && j < BOARDWIDTH && j >= 0)
						|| (board[i + 1][j] instanceof Cover
								&& (a instanceof CrowdControlAbility || a instanceof HealingAbility))
						|| board[i + 1][j] == null
						|| (!(board[i + 1][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i + 1][j]) != getWhichTeam(currentChampion)
								&& (a instanceof HealingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)))
						|| (!(board[i + 1][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i + 1][j]) == getWhichTeam(currentChampion)
								&& (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)))) {
					j++;
					continue;
				}
				x.add((Damageable) board[i + 1][j]);
				j++;
			}
			j = currentChampion.getLocation().y - 1;
			if (j >= 0) {
				if (!((board[i][j] instanceof Cover
						&& (a instanceof CrowdControlAbility || a instanceof HealingAbility))
						|| board[i][j] == null
						|| (!(board[i][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i][j]) != getWhichTeam(currentChampion)
								&& (a instanceof HealingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)))
						|| (!(board[i][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i][j]) == getWhichTeam(currentChampion)
								&& (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)))))
					x.add((Damageable) board[i][j]);
			}
			j += 2;
			if (j < BOARDWIDTH) {
				if (!((board[i][j] instanceof Cover
						&& (a instanceof CrowdControlAbility || a instanceof HealingAbility))
						|| board[i][j] == null
						|| (!(board[i][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i][j]) != getWhichTeam(currentChampion)
								&& (a instanceof HealingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)))
						|| (!(board[i][j] instanceof Cover)
								&& getWhichTeam((Champion) board[i][j]) == getWhichTeam(currentChampion)
								&& (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
										&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)))))
					x.add((Damageable) board[i][j]);
			}
		} else {
			if ((a instanceof CrowdControlAbility
					&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)
					|| a instanceof DamagingAbility) {
				if (getWhichTeam(currentChampion)) {
					for (Champion c : secondPlayer.getTeam())
						if (inRange(currentChampion, c, a.getCastRange()))
							x.add(c);
				} else {
					for (Champion c : firstPlayer.getTeam())
						if (inRange(currentChampion, c, a.getCastRange()))
							x.add(c);

				}
			} else {
				if (!getWhichTeam(currentChampion)) {
					for (Champion c : secondPlayer.getTeam())
						if (inRange(currentChampion, c, a.getCastRange()))
							x.add(c);
				} else {
					for (Champion c : firstPlayer.getTeam())
						if (inRange(currentChampion, c, a.getCastRange()))
							x.add(c);

				}
			}
		}
		castAbilityHelper(currentChampion, a, x);
	}

	private void removeKO() {
		for (int i = 0; i < BOARDHEIGHT; i++)
			for (int j = 0; j < BOARDWIDTH; j++)
				if (board[i][j] instanceof Cover && ((Cover) board[i][j]).getCurrentHP() == 0)
					board[i][j] = null;
		for (Champion c : (ArrayList<Champion>) firstPlayer.getTeam().clone()) {
			if (c.getCondition() == Condition.KNOCKEDOUT || c.getCurrentHP() == 0) {
				board[c.getLocation().x][c.getLocation().y] = null;
				firstPlayer.getTeam().remove(c);
			}
		}
		for (Champion c : (ArrayList<Champion>) secondPlayer.getTeam().clone()) {
			if (c.getCondition() == Condition.KNOCKEDOUT || c.getCurrentHP() == 0) {
				board[c.getLocation().x][c.getLocation().y] = null;
				secondPlayer.getTeam().remove(c);
			}
		}
		PriorityQueue pq = new PriorityQueue(6);
		while (!turnOrder.isEmpty()) {
			Champion c = (Champion) turnOrder.remove();
			if (c.getCondition() != Condition.KNOCKEDOUT && c.getCurrentHP() != 0)
				pq.insert(c);
		}
		while (!pq.isEmpty())
			turnOrder.insert(pq.remove());
	}

	public ArrayList<Damageable> getAllDamageablesInRange(Champion c, Ability a, Direction d, boolean team) {
		int range = a.getCastRange();
		ArrayList<Damageable> target = new ArrayList<>();
		if (d == Direction.DOWN) {
			for (int i = c.getLocation().x - 1; i >= Math.max(0, c.getLocation().x - range); i--) {
				if (board[i][c.getLocation().y] != null) {
					if (board[i][c.getLocation().y] instanceof Cover) {
						if (a instanceof CrowdControlAbility || a instanceof HealingAbility)
							continue;
						target.add((Damageable) board[i][c.getLocation().y]);
					} else {
						if (getWhichTeam((Champion) board[i][c.getLocation().y]) == team) {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								continue;
							target.add((Damageable) board[i][c.getLocation().y]);
						} else {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								target.add((Damageable) board[i][c.getLocation().y]);
						}
					}
				}
			}
		} else if (d == Direction.UP) {
			for (int i = c.getLocation().x + 1; i < Math.min(BOARDHEIGHT, c.getLocation().x + range + 1); i++) {
				if (board[i][c.getLocation().y] != null) {
					if (board[i][c.getLocation().y] instanceof Cover) {
						if (a instanceof CrowdControlAbility || a instanceof HealingAbility)
							continue;
						target.add((Damageable) board[i][c.getLocation().y]);
					} else {
						if (getWhichTeam((Champion) board[i][c.getLocation().y]) == team) {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								continue;
							target.add((Damageable) board[i][c.getLocation().y]);
						} else {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								target.add((Damageable) board[i][c.getLocation().y]);
						}
					}
				}
			}
		} else if (d == Direction.LEFT) {
			for (int j = c.getLocation().y - 1; j >= Math.max(0, c.getLocation().y - range); j--) {
				if (board[c.getLocation().x][j] != null) {
					if (board[c.getLocation().x][j] instanceof Cover) {
						if (a instanceof CrowdControlAbility || a instanceof HealingAbility)
							continue;
						target.add((Damageable) board[c.getLocation().x][j]);
					} else {
						if (getWhichTeam((Champion) board[c.getLocation().x][j]) == team) {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								continue;
							target.add((Damageable) board[c.getLocation().x][j]);
						} else {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								target.add((Damageable) board[c.getLocation().x][j]);
						}
					}
				}
			}
		} else {
			for (int j = c.getLocation().y + 1; j < Math.min(BOARDWIDTH, c.getLocation().y + range + 1); j++) {
				if (board[c.getLocation().x][j] != null) {
					if (board[c.getLocation().x][j] instanceof Cover) {
						if (a instanceof CrowdControlAbility || a instanceof HealingAbility)
							continue;
						target.add((Damageable) board[c.getLocation().x][j]);
					} else {
						if (getWhichTeam((Champion) board[c.getLocation().x][j]) == team) {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								continue;
							target.add((Damageable) board[c.getLocation().x][j]);
						} else {
							if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
									&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
								target.add((Damageable) board[c.getLocation().x][j]);
						}
					}
				}
			}
		}
		return target;
	}

	public void castAbility(Ability a, Direction d)
			throws CloneNotSupportedException, AbilityUseException, NotEnoughResourcesException {
		Champion currentChampion = getCurrentChampion();
		if (currentChampion.getAbilities() == null)
			throw new AbilityUseException();
		ArrayList<Damageable> target = getAllDamageablesInRange(currentChampion, a, d, getWhichTeam(currentChampion));
		castAbilityHelper(currentChampion, a, target);
	}

	public void castAbility(Ability a, int x, int y) throws InvalidTargetException, CloneNotSupportedException,
			AbilityUseException, NotEnoughResourcesException {
		if (getCurrentChampion().getAbilities() == null)
			throw new AbilityUseException();
		if (a.getCurrentCooldown() > 0)
			throw new AbilityUseException();
		if (a.getManaCost() > getCurrentChampion().getMana()
				|| a.getRequiredActionPoints() > getCurrentChampion().getCurrentActionPoints())
			throw new NotEnoughResourcesException();
		if (board[x][y] == null
				|| (board[x][y] instanceof Cover && (a instanceof HealingAbility || a instanceof CrowdControlAbility)))
			throw new InvalidTargetException();
		if (board[x][y] instanceof Champion
				&& getWhichTeam((Champion) board[x][y]) != getWhichTeam(getCurrentChampion())
				&& (a instanceof HealingAbility || (a instanceof CrowdControlAbility
						&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)))
			throw new InvalidTargetException();
		if (board[x][y] instanceof Champion
				&& getWhichTeam((Champion) board[x][y]) == getWhichTeam(getCurrentChampion())
				&& (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
						&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)))
			throw new InvalidTargetException();
		if (!inRange(getCurrentChampion(), (Damageable) board[x][y], a.getCastRange()))
			throw new AbilityUseException();
		ArrayList<Damageable> target = new ArrayList<>();
		target.add((Damageable) board[x][y]);
		castAbilityHelper(getCurrentChampion(), a, target);
	}

	public void useLeaderAbility()
			throws CloneNotSupportedException, LeaderNotCurrentException, LeaderAbilityAlreadyUsedException {
		Champion currentChampion = getCurrentChampion();
		if (firstPlayer.getLeader() != currentChampion && secondPlayer.getLeader() != currentChampion)
			throw new LeaderNotCurrentException();
		ArrayList<Champion> target = new ArrayList<>();
		if (getWhichTeam(currentChampion)) {
			if (currentChampion.equals(firstPlayer.getLeader())) {
				if (firstLeaderAbilityUsed)
					throw new LeaderAbilityAlreadyUsedException();
				firstLeaderAbilityUsed = true;
				if (currentChampion instanceof Hero) {
					currentChampion.useLeaderAbility(firstPlayer.getTeam());
				} else if (currentChampion instanceof Villain) {
					currentChampion.useLeaderAbility(secondPlayer.getTeam());
					removeKO();
				} else {
					for (Champion c : firstPlayer.getTeam())
						if (!c.equals(currentChampion))
							target.add(c);
					for (Champion c : secondPlayer.getTeam())
						if (!c.equals(secondPlayer.getLeader()))
							target.add(c);
					currentChampion.useLeaderAbility(target);
				}
			}
		} else {
			if (currentChampion.equals(secondPlayer.getLeader())) {
				if (secondLeaderAbilityUsed)
					throw new LeaderAbilityAlreadyUsedException();
				secondLeaderAbilityUsed = true;
				if (currentChampion instanceof Hero) {
					currentChampion.useLeaderAbility(secondPlayer.getTeam());
				} else if (currentChampion instanceof Villain) {
					currentChampion.useLeaderAbility(firstPlayer.getTeam());
					removeKO();
				} else {
					for (Champion c : secondPlayer.getTeam())
						if (!c.equals(currentChampion))
							target.add(c);
					for (Champion c : firstPlayer.getTeam())
						if (!c.equals(firstPlayer.getLeader()))
							target.add(c);
					currentChampion.useLeaderAbility(target);
				}
			}
		}
	}

	public void endTurn() throws CloneNotSupportedException {
		turnOrder.remove();
		// TODO Message in the GUI
		if (checkGameOver() != null)
			return;
		while (!turnOrder.isEmpty() && ((Champion) turnOrder.peekMin()).getCondition() == Condition.INACTIVE) {
			Champion c = (Champion) turnOrder.remove();
			c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
			for (Object o : (ArrayList) c.getAppliedEffects().clone()) {
				Effect e = (Effect) o;
				e.setDuration(e.getDuration() - 1);
				if (e.getDuration() == 0) {
					c.getAppliedEffects().remove(e);
					e.remove(c);
				}
			}
			// Doing abilities
			for (Ability a : c.getAbilities())
				if (a.getCurrentCooldown() > 0)
					a.setCurrentCooldown(a.getCurrentCooldown() - 1);
		}
		if (turnOrder.isEmpty())
			prepareChampionTurns();
		while (!turnOrder.isEmpty() && ((Champion) turnOrder.peekMin()).getCondition() == Condition.INACTIVE) {
			Champion c = (Champion) turnOrder.remove();
			c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
			for (Object o : (ArrayList) c.getAppliedEffects().clone()) {
				Effect e = (Effect) o;
				e.setDuration(e.getDuration() - 1);
				if (e.getDuration() == 0) {
					c.getAppliedEffects().remove(e);
					e.remove(c);
				}
			}
			// Doing abilities
			for (Ability a : c.getAbilities())
				if (a.getCurrentCooldown() > 0)
					a.setCurrentCooldown(a.getCurrentCooldown() - 1);
		}
		Champion c = getCurrentChampion();
		c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
		for (Object o : (ArrayList) c.getAppliedEffects().clone()) {
			Effect e = (Effect) o;
			e.setDuration(e.getDuration() - 1);
			if (e.getDuration() == 0) {
				c.getAppliedEffects().remove(e);
				e.remove(c);
			}
		}
		// Doing abilities
		for (Ability a : c.getAbilities())
			if (a.getCurrentCooldown() > 0)
				a.setCurrentCooldown(a.getCurrentCooldown() - 1);
	}

	private void prepareChampionTurns() {
		for (Champion c : firstPlayer.getTeam())
			turnOrder.insert(c);
		for (Champion c : secondPlayer.getTeam())
			turnOrder.insert(c);
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public Object[][] getBoard() {
		return board;
	}

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public static class Scanner {
		private StringTokenizer st;
		private BufferedReader br;

		public Scanner(InputStream s) {
			br = new BufferedReader(new InputStreamReader(s));
		}

		public Scanner(FileReader r) {
			br = new BufferedReader(r);
		}

		public String next() throws IOException {
			while (st == null || !st.hasMoreTokens())
				st = new StringTokenizer(br.readLine());
			return st.nextToken();
		}

		public int nextInt() throws IOException {
			return Integer.parseInt(next());
		}

		public long nextLong() throws IOException {
			return Long.parseLong(next());
		}

		public String nextLine() throws IOException {
			return br.readLine();
		}

		public boolean ready() throws IOException {
			return br.ready();
		}

		public long[] nextlongArray(int n) throws IOException {
			long[] a = new long[n];
			for (int i = 0; i < n; i++)
				a[i] = nextLong();
			return a;
		}

		public Long[] nextLongArray(int n) throws IOException {
			Long[] a = new Long[n];
			for (int i = 0; i < n; i++)
				a[i] = nextLong();
			return a;
		}

		public int[] nextIntArray(int n) throws IOException {
			int[] a = new int[n];
			for (int i = 0; i < n; i++)
				a[i] = nextInt();
			return a;
		}

		public Integer[] nextIntegerArray(int n) throws IOException {
			Integer[] a = new Integer[n];
			for (int i = 0; i < n; i++)
				a[i] = nextInt();
			return a;
		}
	}

}