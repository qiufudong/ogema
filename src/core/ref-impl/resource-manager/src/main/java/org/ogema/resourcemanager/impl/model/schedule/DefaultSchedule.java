/**
 * This file is part of OGEMA.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ogema.resourcemanager.impl.model.schedule;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.schedule.RelativeSchedule;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.persistence.impl.faketree.ScheduleTreeElement;
import org.ogema.resourcemanager.impl.ApplicationResourceManager;
import org.ogema.resourcemanager.impl.ResourceBase;
import org.ogema.resourcemanager.impl.ResourceDBManager;
import org.ogema.resourcemanager.virtual.VirtualTreeElement;

/**
 * Resource object representing a schedule. Extends ResourceBase and utilizes
 * the schedule functionality from the underlying ScheduleTreeElement.
 *
 * @author Timo Fischer, Fraunhofer IWES
 */
@SuppressWarnings("deprecation")
public class DefaultSchedule extends ResourceBase implements org.ogema.core.model.schedule.DefinitionSchedule,
		org.ogema.core.model.schedule.ForecastSchedule, AbsoluteSchedule, RelativeSchedule {

	private final ApplicationManager m_appMan;
	// FIXME this needs to change when a reference is set or replaced; need a ScheduleTreeElementRegistry!
	private final ScheduleTreeElement m_scheduleElement;
	private final ResourceDBManager m_dbMan;

	public DefaultSchedule(VirtualTreeElement el, ScheduleTreeElement scheduleElement, String path,
			ApplicationResourceManager resMan, ApplicationManager appMan, ResourceDBManager dbMan) {
		super(el, path, resMan);
		m_appMan = appMan;
		m_scheduleElement = scheduleElement;
		m_dbMan = dbMan;
	}

	final ScheduleTreeElement getSchedule() {
		return m_scheduleElement;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Resource> T create() {
		getResourceDB().lockStructureWrite();
		m_dbMan.startTransaction();
		try {
			DefaultSchedule s = super.create();
			m_scheduleElement.create();
			return (T) s;
		} finally {
			m_dbMan.finishTransaction();
			getResourceDB().unlockStructureWrite();
		}
	}

    @Override
    protected void deleteTreeElement() {
    	m_dbMan.startTransaction();
		try {
			if (!isReference(false)) {
		        getSchedule().deleteValues();
		        getSchedule().setLastModified(-1);
		        m_scheduleElement.getScheduleElement().delete();
			} else {
				// TODO this is not working!
			}
	        super.deleteTreeElement();
		} finally {
			m_dbMan.finishTransaction();
		}
		
    }

	@Override
	public boolean addValue(long timestamp, Value value) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().addValue(timestamp, value);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean addValues(Collection<SampledValue> values) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().addValues(values);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	@Deprecated
	public final boolean addValueSchedule(long startTime, long stepSize, List<Value> values) {
		return replaceValuesFixedStep(startTime, values, stepSize);
	}

	@Override
	public boolean replaceValuesFixedStep(long startTime, List<Value> values, long stepSize) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().replaceValuesFixedStep(startTime, values, stepSize);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean addValue(long timestamp, Value value, long timeOfCalculation) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().addValue(timestamp, value, timeOfCalculation);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean addValues(Collection<SampledValue> values, long timeOfCalculation) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().addValues(values, timeOfCalculation);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	@Deprecated
	public final boolean addValueSchedule(long startTime, long stepSize, List<Value> values, long timeOfCalculation) {
		return replaceValuesFixedStep(startTime, values, stepSize, timeOfCalculation);
	}

	@Override
	public boolean replaceValuesFixedStep(long startTime, List<Value> values, long stepSize, long timeOfCalculation) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().replaceValuesFixedStep(startTime, values, stepSize, timeOfCalculation);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public Long getLastCalculationTime() {
		return getSchedule().getLastCalculationTime();
	}

	@Override
	public boolean deleteValues() {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().deleteValues();
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean deleteValues(long endTime) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().deleteValues(endTime);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean deleteValues(long startTime, long endTime) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().deleteValues(startTime, endTime);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean replaceValues(long startTime, long endTime, Collection<SampledValue> values) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		m_dbMan.startTransaction();
		try {
			getSchedule().replaceValues(startTime, endTime, values);
		} finally {
			m_dbMan.finishTransaction();
		}
		getSchedule().setLastUpdateTime(m_appMan.getFrameworkTime());
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public boolean setInterpolationMode(InterpolationMode mode) {
		if (!exists() || !hasWriteAccess()) {
			return false;
		}
		checkWritePermission();
		getSchedule().setInterpolationMode(mode);
		handleResourceUpdate(true);
		return true;
	}

	@Override
	public SampledValue getValue(long time) {
		return getSchedule().getValue(time);
	}

	@Override
	public SampledValue getNextValue(long time) {
		return getSchedule().getNextValue(time);
	}

	@Override
	public List<SampledValue> getValues(long startTime) {
		return getSchedule().getValues(startTime);
	}

	@Override
	public List<SampledValue> getValues(long startTime, long endTime) {
		return getSchedule().getValues(startTime, endTime);
	}

	@Override
	public InterpolationMode getInterpolationMode() {
		return getSchedule().getInterpolationMode();
	}

	@Override
	@Deprecated
	public Long getTimeOfLatestEntry() {
		return getSchedule().getLastCalculationTime();
	}

	@Override
	public long getLastUpdateTime() {
		return getSchedule().getLastModified();
	}
	
	@Override
	public SampledValue getPreviousValue(long time) {
		return getSchedule().getPreviousValue(time);
	}

	@Override
	public boolean isEmpty() {
		return getSchedule().isEmpty();
	}

	@Override
	public boolean isEmpty(long startTime, long endTime) {
		return getSchedule().isEmpty(startTime, endTime);
	}

	@Override
	public int size() {
		return getSchedule().size();
	}

	@Override
	public int size(long startTime, long endTime) {
		return getSchedule().size(startTime, endTime);
	}

	@Override
	public Iterator<SampledValue> iterator() {
		return getSchedule().iterator();
	}

	@Override
	public Iterator<SampledValue> iterator(long startTime, long endTime) {
		return getSchedule().iterator(startTime, endTime);
	}

}
