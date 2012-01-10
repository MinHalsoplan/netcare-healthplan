package org.callistasoftware.netcare.core.api.impl;

import java.util.Date;

import org.callistasoftware.netcare.core.api.Alarm;
import org.callistasoftware.netcare.core.api.CareGiver;
import org.callistasoftware.netcare.core.api.Patient;
import org.callistasoftware.netcare.model.entity.AlarmCause;
import org.callistasoftware.netcare.model.entity.AlarmEntity;

public class AlarmImpl implements Alarm {

	private Long id;
	private String careUnitHsaId;
	private Patient patient;
	private Date createdTime;
	private Date resolvedTime;
	private CareGiver resolvedBy;
	private AlarmCause cause;
	private Long entityReferenceId;
	
	public static Alarm newFromEntity(final AlarmEntity entity) {
		final AlarmImpl alarm = new AlarmImpl();
		alarm.setCareUnitHsaId(entity.getCareUnitHsaId());
		alarm.setCause(entity.getCause());
		alarm.setCreatedTime(entity.getCreatedTime());
		alarm.setResolvedTime(entity.getResolvedTime());
		alarm.setEntityReferenceId(entity.getRefEntityId());
		alarm.setId(entity.getId());
		
		
		return alarm;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCareUnitHsaId(String careUnitHsaId) {
		this.careUnitHsaId = careUnitHsaId;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setResolvedTime(Date resolvedTime) {
		this.resolvedTime = resolvedTime;
	}

	public void setResolvedBy(CareGiver resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public void setCause(AlarmCause cause) {
		this.cause = cause;
	}

	public void setEntityReferenceId(Long entityReferenceId) {
		this.entityReferenceId = entityReferenceId;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getCareUnitHsaId() {
		return this.careUnitHsaId;
	}

	@Override
	public Patient getPatient() {
		return this.patient;
	}

	@Override
	public Date getCreatedTime() {
		return this.createdTime;
	}

	@Override
	public Date getResolvedTime() {
		return this.resolvedTime;
	}

	@Override
	public CareGiver getResolvedBy() {
		return this.resolvedBy;
	}

	@Override
	public AlarmCause getCause() {
		return this.cause;
	}

	@Override
	public Long getEntityReferenceId() {
		return this.entityReferenceId;
	}

}
