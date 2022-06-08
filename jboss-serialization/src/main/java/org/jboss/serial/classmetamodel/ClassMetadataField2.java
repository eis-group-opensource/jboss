/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.serial.classmetamodel;

import java.io.ObjectStreamField;
import java.lang.reflect.Field;

import org.jboss.serial.references.FieldPersistentReference;
import org.jboss.serial.util.HashStringUtil;

/**
 * @author clebert suconic
 */
public class ClassMetadataField2 extends ClassMetadataField
{
	public ClassMetadataField2(Field field)
	{
        this.setField(field);
        this.setFieldName(field.getName());
        this.shaHash = HashStringUtil.hashName(field.getType().getName() +"$"+ field.getName());
        this.setObject(!ClassMetamodelFactory.isPrimitive(field.getType()));
        this.clazzType = field.getType();
	}
	
	public ClassMetadataField2(ObjectStreamField field)
	{
		this.setField(null);
		this.setFieldName(field.getName());
        this.shaHash = HashStringUtil.hashName(field.getType().getName() +"$"+ field.getName());
		this.setObject(!ClassMetamodelFactory.isPrimitive(field.getType()));
		this.clazzType = field.getType();
	}
	
    String fieldName;
    
    Class clazzType;

    FieldPersistentReference field;

    /** Used only by {@link UnsafeFieldsManager} */
    long unsafeKey;

    boolean isObject;
    
    long shaHash;
    
    /** Order the field appears on the slot */
    short order;

    
    public Class getType()
    {
    	return clazzType;
    }

    /**
     * @return Returns the field.
     */
    public Field getField()
    {
    	if (field == null)
    	{
    		return null;
    	}
    	else
    	{
    		return (Field)field.get();
    	}
    }

    /**
     * @param field
     *            The field to set.
     */
    public void setField(Field afield)
    {
    	if (afield == null)
    	{
    		this.field = null;
    	}
    	else
    	{
    		this.field = new FieldPersistentReference(afield,ClassMetaData.REFERENCE_TYPE_IN_USE);
    	}
    }

    /**
     * @return Returns the fieldName.
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * @param fieldName
     *            The fieldName to set.
     */
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    /**
     * @return Returns the isObject.
     */
    public boolean isObject()
    {
        return isObject;
    }

    /**
     * @param isObject
     *            The isObject to set.
     */
    public void setObject(boolean isObject)
    {
        this.isObject = isObject;
    }

    public long getUnsafeKey() {
        return unsafeKey;
    }

    public void setUnsafeKey(long unsafeKey) {
        this.unsafeKey = unsafeKey;
    }

	public long getShaHash() {
		return shaHash;
	}

	public void setShaHash(long shaHash) {
		this.shaHash = shaHash;
	}

	public short getOrder() {
		return order;
	}

	public void setOrder(short order) {
		this.order = order;
	}

	public String toString() {
		return "ClassMetadataField [fieldName=" + fieldName + ", field="
				+ field + ", unsafeKey=" + unsafeKey + ", isObject=" + isObject
				+ ", shaHash=" + shaHash + ", order=" + order + "]";
	}

	
    

}
