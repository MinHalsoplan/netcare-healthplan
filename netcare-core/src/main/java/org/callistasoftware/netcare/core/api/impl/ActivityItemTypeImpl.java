package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.ActivityItemType;

public class ActivityItemTypeImpl implements ActivityItemType {

	private Long id;
	private String name;
	private int seqno;

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

}
