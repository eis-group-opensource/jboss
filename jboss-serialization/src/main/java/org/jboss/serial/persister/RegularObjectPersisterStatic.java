/*
* JBoss, Home of Professional Open Source
* Copyright 2013, JBoss Inc., and individual contributors as indicated
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

/**
 * @author bmaxwell
 */
package org.jboss.serial.persister;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jboss.serial.classmetamodel.ClassMetaData;
import org.jboss.serial.classmetamodel.ClassMetaDataSlot;
import org.jboss.serial.classmetamodel.StreamingClass;
import org.jboss.serial.objectmetamodel.FieldsContainer;
import org.jboss.serial.objectmetamodel.ObjectSubstitutionInterface;
import org.jboss.serial.objectmetamodel.ObjectsCache;

/**
 * @author bmaxwell
 *
 */
public interface RegularObjectPersisterStatic {

    public byte getId();
        
    public void setId(byte id);

    public void writeData(ClassMetaData metaData, ObjectOutput out, Object obj, ObjectSubstitutionInterface substitution) throws IOException;

    // static
    public void staticDefaultWrite(ObjectOutput output, Object obj, ClassMetaData metaClass, ObjectSubstitutionInterface substitution) throws IOException;

//    private static void readSlotWithMethod(ClassMetaDataSlot slot, short[] fieldsKey, ObjectInput input, Object obj, ObjectSubstitutionInterface substitution) throws IOException;
    
//    private static void writeSlotWithMethod(ClassMetaDataSlot slot, ObjectOutput out, Object obj, ObjectSubstitutionInterface substitution) throws IOException;
    
	void staticWriteSlotWithFields(ClassMetaDataSlot slot, ObjectOutput output, Object obj, ObjectSubstitutionInterface substitution) throws IOException;
		
//	private static void writeDatatype ( final ObjectOutput out, byte datatype ) throws IOException;


//    private static void writeOnPrimitive(final ObjectOutput out, final Object obj, final ClassMetadataField metaField) throws IOException;

    public Object readData (ClassLoader loader, StreamingClass streaming, ClassMetaData metaData, int referenceId, ObjectsCache cache, ObjectInput input, ObjectSubstitutionInterface substitution) throws IOException;

    public Object staticDefaultRead(ObjectInput input, Object obj, StreamingClass streaming, ClassMetaData metaData, ObjectSubstitutionInterface substitution) throws IOException;
    	
	void staticReadSlotWithFields(short fieldsKey[],ClassMetaDataSlot slot, ObjectInput input, Object obj) throws IOException, ClassNotFoundException;		

	FieldsContainer staticReadSlotWithFields(ClassMetaDataSlot slot, ObjectInput input) throws IOException, ClassNotFoundException;
		
    public boolean canPersist(Object obj);
	
    public void staticWriteSlotWithFieldsContainer(ClassMetaDataSlot slot,ObjectOutput output, FieldsContainer fields , ObjectSubstitutionInterface substitution) throws IOException;
}
