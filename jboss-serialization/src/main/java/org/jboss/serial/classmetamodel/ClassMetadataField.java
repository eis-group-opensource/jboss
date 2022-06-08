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

import org.jboss.serial.Compatability;
import org.jboss.serial.references.FieldPersistentReference;
import org.jboss.serial.util.HashStringUtil;

/**
 * @author clebert suconic
 */
public class ClassMetadataField
{
    private ClassMetadataField delegate;
	
    protected ClassMetadataField() 
    {
        // only called by ClassMetadataField1 / ClassMetadataField2
        // cannot create delegates here else it will cause a loop  
    }

    public ClassMetadataField(Field field)
    {
        if(Compatability.SYNC_BINARY_FORMATS2) {
            delegate = new ClassMetadataField2(field);
        } else {
            delegate = new ClassMetadataField1(field);
        }		
    }
	
    public ClassMetadataField(ObjectStreamField field)
    {
        if(Compatability.SYNC_BINARY_FORMATS2) {
            delegate = new ClassMetadataField2(field);
        } else {
            delegate = new ClassMetadataField1(field);
        }		
    }

    public Class getType()
    {
    	return delegate.getType();
    }
	
    /**
     * @return Returns the field.
     */
    public Field getField()
    {
    	return delegate.getField();
    }

    /**
     * @param field
     *            The field to set.
     */
    public void setField(Field afield)
    {
   		delegate.setField(afield);
    }

    /**
     * @return Returns the fieldName.
     */
    public String getFieldName()
    {
        return delegate.getFieldName();
    }

    /**
     * @param fieldName
     *            The fieldName to set.
     */
    public void setFieldName(String fieldName)
    {
        delegate.setFieldName(fieldName);
    }

    /**
     * @return Returns the isObject.
     */
    public boolean isObject()
    {
        return delegate.isObject();
    }

    /**
     * @param isObject
     *            The isObject to set.
     */
    public void setObject(boolean isObject)
    {
        delegate.setObject(isObject);
    }

    public long getUnsafeKey() {
        return delegate.getUnsafeKey();
    }

    public void setUnsafeKey(long unsafeKey) {
        delegate.setUnsafeKey(unsafeKey);
    }

    public long getShaHash() {
        return delegate.getShaHash();
    }

    public void setShaHash(long shaHash) {
        delegate.setShaHash(shaHash);
    }

    public short getOrder() {
        return delegate.getOrder();
    }

    public void setOrder(short order) {
        delegate.setOrder(order);
    }
}
