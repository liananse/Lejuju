package com.medialab.lejuju.main.joinevent;

import java.util.Comparator;

import com.medialab.lejuju.main.joinevent.model.JContactsModel;

public class PinyinComparator implements Comparator<JContactsModel> {

	@Override
	public int compare(JContactsModel o1, JContactsModel o2) {
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
