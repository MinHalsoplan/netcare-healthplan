/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.callistasoftware.netcare.core.api.impl;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.callistasoftware.netcare.core.api.ActivityDefinition;
import org.callistasoftware.netcare.core.api.CareGiverBaseView;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.core.api.HealthPlan;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.model.entity.HealthPlanEntity;

/**
 * Implementation of an ordination
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class HealthPlanImpl implements HealthPlan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String startDate;
	private String endDate;
	private int duration;
	private Option durationUnit;
	
	private CareGiverBaseView issuedBy;
	private PatientBaseView patient;
	private ActivityDefinition[] activityDefintions;
	
	public static HealthPlanImpl newFromEntity(final HealthPlanEntity entity, final Locale l) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		final HealthPlanImpl dto = new HealthPlanImpl();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDuration(entity.getDuration());
		dto.setDurationUnit(new Option(entity.getDurationUnit().name(), l));
		dto.setStartDate(sdf.format(entity.getStartDate()));
		
		final CareGiverBaseViewImpl cg = new CareGiverBaseViewImpl(entity.getIssuedBy().getId(), entity.getIssuedBy().getName());
		cg.setHsaId(entity.getIssuedBy().getHsaId());
		
		dto.setIssuedBy(cg);
		dto.setPatient(PatientBaseViewImpl.newFromEntity(entity.getForPatient()));
		
		/*
		 * Process defintions
		 */
		
		
		return dto;
	}

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
	public String getStartDate() {
		return this.startDate;
	}
	
	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	@Override
	public String getEndDate() {
		return this.endDate;
	}

	@Override
	public CareGiverBaseView getIssuedBy() {
		return this.issuedBy;
	}
	
	public void setIssuedBy(final CareGiverBaseView careGiver) {
		this.issuedBy = careGiver;
	}

	@Override
	public int getDuration() {
		return this.duration;
	}
	
	public void setDuration(final int duration) {
		this.duration = duration;
	}

	@Override
	public Option getDurationUnit() {
		return this.durationUnit;
	}
	
	public void setDurationUnit(final Option durationUnit) {
		this.durationUnit = durationUnit;
	}

	@Override
	public ActivityDefinition[] getActivityDefintions() {
		return this.activityDefintions;
	}

	@Override
	public PatientBaseView getPatient() {
		return this.patient;
	}
	
	public void setPatient(final PatientBaseView patient) {
		this.patient = patient;
	}

}
