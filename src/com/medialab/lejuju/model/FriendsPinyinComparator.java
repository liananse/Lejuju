package com.medialab.lejuju.model;

import java.util.Comparator;

public class FriendsPinyinComparator implements Comparator<FriendsModel> {

	@Override
	public int compare(FriendsModel o1, FriendsModel o2) {
		// TODO Auto-generated method stub
		if (o1.namePinYinFirst.equals("@")
				|| o2.namePinYinFirst.equals("#")) {
			return -1;
		} else if (o1.namePinYinFirst.equals("#")
				|| o2.namePinYinFirst.equals("@")) {
			return 1;
		} else {
			return o1.namePinYinFirst.compareTo(o2.namePinYinFirst);
		}
	}

}