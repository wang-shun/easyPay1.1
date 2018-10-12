package com.acewill.ordermachine.model;


/**
 * 菜品分类
 */
public class DishKind extends BaseModelSz {
	public String kindID;
	public String kindName;
	public String english;
	public String imageName;

	public int getKindPosition() {
		return kindPosition;
	}

	public void setKindPosition(int kindPosition) {
		this.kindPosition = kindPosition;
	}

	public int kindPosition;
	public int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "DishKind{" +
				"kindID='" + kindID + '\'' +
				", kindName='" + kindName + '\'' +
				", english='" + english + '\'' +
				", imageName='" + imageName + '\'' +
				", kindPosition=" + kindPosition +
				", count=" + count +
				'}';
	}

}
