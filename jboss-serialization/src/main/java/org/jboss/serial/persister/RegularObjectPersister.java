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

package org.jboss.serial.persister;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jboss.serial.Compatability;
import org.jboss.serial.classmetamodel.ClassMetaData;
import org.jboss.serial.classmetamodel.ClassMetaDataSlot;
import org.jboss.serial.classmetamodel.ClassMetadataField;
import org.jboss.serial.classmetamodel.StreamingClass;
import org.jboss.serial.objectmetamodel.FieldsContainer;
import org.jboss.serial.objectmetamodel.ObjectSubstitutionInterface;
import org.jboss.serial.objectmetamodel.ObjectsCache;

/**
 * This is the persister of a regular object.
 * @author clebert suconic
 * @author bmaxwell
 */
public class RegularObjectPersister implements Persister
{   
    private RegularObjectPersister delegate;
	
    private static final RegularObjectPersisterStatic staticDelegate;

    // used to remove all but the last caused by to prevent very deep stacktraces from causing disk space issues
    protected static final boolean COLLAPSE_CAUSEDBYS = Boolean.parseBoolean( System.getProperty("org.jboss.serial.collapse.causedbys", "false") );
	
    static {			
        if(Compatability.SYNC_BINARY_FORMATS2 == true) {
            staticDelegate = new RegularObjectPersister2();
        }
        else {
            staticDelegate = new RegularObjectPersister1();
        }
    }
	
    public RegularObjectPersister() {
        if(Compatability.SYNC_BINARY_FORMATS2 == true) {
            delegate = new RegularObjectPersister2();	
        } else {
            delegate = new RegularObjectPersister1();			
        }
    }

    protected RegularObjectPersister(RegularObjectPersisterStatic delegate)
    {
        // this prevents infinite loop since the default construtor could be used and would need to create a delegate
    }
	
    public byte getId() {
        return delegate.getId();
    }

    public void setId(byte id) {
        delegate.setId(id);
    }

    public void writeData(ClassMetaData metaData, ObjectOutput out, Object obj, ObjectSubstitutionInterface substitution) throws IOException
    {
    	delegate.writeData(metaData, out, obj, substitution);        
    }

    public static void defaultWrite(ObjectOutput output, Object obj, ClassMetaData metaClass, ObjectSubstitutionInterface substitution) throws IOException
    {
    	staticDelegate.staticDefaultWrite(output, obj, metaClass, substitution);
    }

	  static void writeSlotWithFields(ClassMetaDataSlot slot, ObjectOutput output, Object obj, ObjectSubstitutionInterface substitution) throws IOException
	  {		
		  staticDelegate.staticWriteSlotWithFields(slot, output, obj, substitution);
	  }
		
    public Object readData (ClassLoader loader, StreamingClass streaming, ClassMetaData metaData, int referenceId, ObjectsCache cache, ObjectInput input, ObjectSubstitutionInterface substitution) throws IOException
    {
    	return delegate.readData(loader, streaming, metaData, referenceId, cache, input, substitution);
    }

    public static Object defaultRead(ObjectInput input, Object obj, StreamingClass streaming, ClassMetaData metaData, ObjectSubstitutionInterface substitution) throws IOException  
    {
    	return staticDelegate.staticDefaultRead(input, obj, streaming, metaData, substitution);
    }

	  static void readSlotWithFields(short fieldsKey[],ClassMetaDataSlot slot, ObjectInput input, Object obj) throws IOException, ClassNotFoundException {
		  staticDelegate.staticReadSlotWithFields(fieldsKey, slot, input, obj);
	  }

	  static FieldsContainer readSlotWithFields(ClassMetaDataSlot slot, ObjectInput input) throws IOException, ClassNotFoundException {
		  return staticDelegate.staticReadSlotWithFields(slot, input);
	  }

    public boolean canPersist(Object obj)
	  {
    	return delegate.canPersist(obj);
	  }
    
    public static void writeSlotWithFieldsContainer(ClassMetaDataSlot slot,ObjectOutput output, FieldsContainer fields , ObjectSubstitutionInterface substitution) throws IOException
    {
    	staticDelegate.staticWriteSlotWithFieldsContainer(slot, output, fields, substitution);
    }
}
