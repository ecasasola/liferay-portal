/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.model.impl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.OrganizationConstants;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="OrganizationImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class OrganizationImpl
	extends OrganizationModelImpl implements Organization {

	public static String[] getChildrenTypes(String type) {
		return PropsUtil.getArray(
			PropsKeys.ORGANIZATIONS_TYPES_CHILDREN_TYPES,
			new Filter(type));
	}

	public static String[] getParentTypes(String type) {
		String[] types = PropsUtil.getArray(
			PropsKeys.ORGANIZATIONS_TYPES,
			new Filter(type));

		List<String> parentTypes = new ArrayList<String>();

		for (String curType : types) {
			if (ArrayUtil.contains(getChildrenTypes(curType), type)) {
				parentTypes.add(curType);
			}
		}

		return parentTypes.toArray(new String[0]);
	}

	public static boolean isParentable(String type) {
		String[] childrenTypes = getChildrenTypes(type);

		if (childrenTypes.length > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public OrganizationImpl() {
	}

	public static boolean isRootable(String type) {
		return GetterUtil.getBoolean(PropsUtil.get(
			PropsKeys.ORGANIZATIONS_TYPES_ROOTABLE,
			new Filter(type)));
	}

	public boolean isParentable() {
		return isParentable(getType());
	}

	public boolean isRoot() {
		if (getParentOrganizationId() ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			return true;
		}
		else {
			return false;
		}
	}

	public String[] getChildrenTypes() {
		return getChildrenTypes(getType());
	}

	public Group getGroup() {
		if (getOrganizationId() > 0) {
			try {
				return GroupLocalServiceUtil.getOrganizationGroup(
					getCompanyId(), getOrganizationId());
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		return new GroupImpl();
	}

	public int getPrivateLayoutsPageCount() {
		try {
			Group group = getGroup();

			if (group == null) {
				return 0;
			}
			else {
				return group.getPrivateLayoutsPageCount();
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return 0;
	}

	public int getSuborganizationsCount() throws SystemException {
		return OrganizationLocalServiceUtil.searchCount(
			getCompanyId(), getOrganizationId(), null, null, null, null, null,
			null, null, null, true);
	}

	public int getTypeOrder() {
		String[] types = PropsValues.ORGANIZATIONS_TYPES;

		for (int i = 0; i < types.length; i++) {
			if (types[i].equals(getType())) {
				return i + 1;
			}
		}

		return 0;
	}

	public boolean hasPrivateLayouts() {
		if (getPrivateLayoutsPageCount() > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public int getPublicLayoutsPageCount() {
		try {
			Group group = getGroup();

			if (group == null) {
				return 0;
			}
			else {
				return group.getPublicLayoutsPageCount();
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return 0;
	}

	public boolean hasPublicLayouts() {
		if (getPublicLayoutsPageCount() > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean hasSuborganizations() throws SystemException {
		if (getSuborganizationsCount() > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public Address getAddress() {
		Address address = null;

		try {
			List<Address> addresses = getAddresses();

			if (addresses.size() > 0) {
				address = addresses.get(0);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		if (address == null) {
			address = new AddressImpl();
		}

		return address;
	}

	public List<Address> getAddresses() throws SystemException {
		return AddressLocalServiceUtil.getAddresses(
			getCompanyId(), Organization.class.getName(), getOrganizationId());
	}

	private static Log _log = LogFactory.getLog(Organization.class);

}