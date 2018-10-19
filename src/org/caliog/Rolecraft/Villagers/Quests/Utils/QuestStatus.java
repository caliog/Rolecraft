package org.caliog.Rolecraft.Villagers.Quests.Utils;

public enum QuestStatus {
	COMPLETED(0), FIRST(1), SECOND(2), THIRD(3), FOURTH(4), UNACCEPTED(-1);

	private final int i;

	private QuestStatus(int i) {
		this.i = i;
	}

	public boolean isLowerThan(QuestStatus qs) {
		return getInt() < qs.getInt();
	}

	public int getInt() {
		return this.i;
	}

	public static QuestStatus fromInt(int i) {
		switch (i) {
		case 0:
			return COMPLETED;
		case 1:
			return FIRST;
		case 2:
			return SECOND;
		case 3:
			return THIRD;
		case 4:
			return FOURTH;
		}
		return FIRST;
	}

	public QuestStatus raise() {
		switch (this) {
		case COMPLETED:
			return COMPLETED;
		case FIRST:
			return SECOND;
		case SECOND:
			return THIRD;
		case THIRD:
			return FOURTH;
		case FOURTH:
			return COMPLETED;
		case UNACCEPTED:
			return FIRST;
		default:
			return FIRST;
		}
	}
}
