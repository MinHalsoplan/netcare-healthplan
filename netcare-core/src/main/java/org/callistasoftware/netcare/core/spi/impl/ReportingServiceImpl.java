/**
 * Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.core.spi.impl;

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityItemType;
import org.callistasoftware.netcare.core.api.ReportingValues;
import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.UserBaseView;
import org.callistasoftware.netcare.core.api.impl.ReportingValuesImpl;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.core.api.messages.NoAccessMessage;
import org.callistasoftware.netcare.core.repository.ActivityDefinitionRepository;
import org.callistasoftware.netcare.core.repository.ActivityItemValuesEntityRepository;
import org.callistasoftware.netcare.core.repository.UserRepository;
import org.callistasoftware.netcare.core.spi.ReportingService;
import org.callistasoftware.netcare.model.entity.ActivityDefinitionEntity;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.callistasoftware.netcare.model.entity.UserEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportingServiceImpl implements ReportingService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ActivityDefinitionRepository activityDefintionRepository;

	@Autowired
	private ActivityItemValuesEntityRepository itemValuesRepository;

	@Override
	public ServiceResult<ReportingValues> getAllValuesByActivityItemDefId(UserBaseView user, Long itemDefId) {

		ActivityDefinitionEntity activityDefinition = activityDefintionRepository
				.findByActivityItemDefinitionId(itemDefId);
		UserEntity ue = userRepository.findOne(user.getId());
		if (!activityDefinition.isReadAllowed(ue)) {
			return ServiceResultImpl.createFailedResult(new NoAccessMessage());
		}

		List<ActivityItemValuesEntity> reportedValues = itemValuesRepository.findValuesByActivityItemId(itemDefId);
		ReportingValues report = transform(itemDefId, reportedValues);

		return ServiceResultImpl.createSuccessResult(report, new GenericSuccessMessage());
	}

	protected ReportingValues transform(Long itemDefId, List<ActivityItemValuesEntity> reportedValues) {
		ReportingValues report = null;
		if (reportedValues != null && reportedValues.size() > 0) {
			if (reportedValues.get(0) instanceof YesNoEntity) {
				report = countYesNo(itemDefId, report, reportedValues);
			} else {
				report = transformIntoLists(itemDefId, reportedValues);
			}
		}
		return report;
	}

	protected ReportingValues countYesNo(Long itemDefId, ReportingValues report,
			List<ActivityItemValuesEntity> reportedValues) {
		int yes = 0;
		int no = 0;
		for (ActivityItemValuesEntity value : reportedValues) {
			if (((YesNoEntity) value).getAnswer()) {
				yes++;
			} else {
				no++;
			}
		}
		return new ReportingValuesImpl(itemDefId, ActivityItemType.YESNO_ITEM_TYPE, yes, no);
	}

	protected ReportingValues transformIntoLists(Long itemDefId, List<ActivityItemValuesEntity> reportedValues) {
		ReportingValuesImpl report = null;
		ActivityItemValuesEntity itemValue = reportedValues.get(0);
		if (itemValue instanceof MeasurementEntity) {
			MeasurementValueType valueType = ((MeasurementTypeEntity) itemValue.getActivityItemDefinitionEntity()
					.getActivityItemType()).getValueType();
			String label = itemValue.getActivityItemDefinitionEntity().getActivityItemType().getName();
			String unit = ((MeasurementTypeEntity) itemValue.getActivityItemDefinitionEntity().getActivityItemType())
					.getUnit().getDn();
			report = new ReportingValuesImpl(itemDefId, ActivityItemType.MEASUREMENT_ITEM_TYPE, valueType.name(),
					label, unit, reportedValues.size());
		} else if (itemValue instanceof EstimationEntity) {
			EstimationTypeEntity est = (EstimationTypeEntity) itemValue.getActivityItemDefinitionEntity()
					.getActivityItemType();
			String label = itemValue.getActivityItemDefinitionEntity().getActivityItemType().getName();
			String unit = est.getSenseLabelLow() + " (" + est.getSenseValueLow() + ") - " + est.getSenseLabelHigh()
					+ " (" + est.getSenseValueHigh() + ")";
			report = new ReportingValuesImpl(itemDefId, ActivityItemType.ESTIMATION_ITEM_TYPE, label, unit,
					reportedValues.size());
		} else if (itemValue instanceof TextEntity) {
			report = new ReportingValuesImpl(itemDefId, ActivityItemType.TEXT_ITEM_TYPE, reportedValues.size());
		}
		for (ActivityItemValuesEntity value : reportedValues) {
			report.addItem(value);
		}
		return report;
	}
}
