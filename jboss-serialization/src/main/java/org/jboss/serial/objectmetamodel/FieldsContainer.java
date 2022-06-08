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

package org.jboss.serial.objectmetamodel;

import org.jboss.serial.Compatability;
import org.jboss.serial.finalcontainers.*;
import org.jboss.serial.persister.RegularObjectPersister;
import org.jboss.serial.persister.RegularObjectPersister1;
import org.jboss.serial.persister.RegularObjectPersister2;

import org.jboss.serial.classmetamodel.ClassMetaData;
import org.jboss.serial.classmetamodel.ClassMetaDataSlot;
import org.jboss.serial.classmetamodel.ClassMetadataField;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.*;

/**
 * $Id: FieldsContainer.java 442 2013-05-14 14:26:08Z bmaxwell $
 *
 * @author <a href="mailto:clebert.suconic@jboss.com">Clebert Suconic</a>
 * @author bmaxwell
 */
public class FieldsContainer  {

    private FieldsContainer delegate;
	
    private static final FieldsContainerStatic staticDelegate;

    static {
		    if(Compatability.SYNC_BINARY_FORMATS2 == true) {
			      staticDelegate = new FieldsContainer2();
		    } else {
			      staticDelegate = new FieldsContainer1();
		    }
	  }

	  public FieldsContainer() 
    {
		    if(Compatability.SYNC_BINARY_FORMATS2 == true) {
			      delegate = new FieldsContainer2();			
		    } else {
			      delegate = new FieldsContainer1();			
		    }
	  }
	
    public FieldsContainer(ClassMetaDataSlot metaData)
    {
        if(Compatability.SYNC_BINARY_FORMATS2 == true) {
			      delegate = new FieldsContainer2(metaData);			
		    } else {
			      delegate = new FieldsContainer1(metaData);			
		    }
    }

    protected FieldsContainer(FieldsContainerStatic delegate)
    {
        // this prevents infinite loop since the default construtor could be used and would need to create a delegate
    }
	
    public Map getMap()
    {
    	return delegate.getMap();
    }
    
    public ClassMetadataField getMetaField(String name)
    {
    	return delegate.getMetaField(name);
    }
	
    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and writeMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    /*public static void writeNumberOfFields(ObjectOutput out, int fields) throws IOException
    {
        out.writeInt(fields);
    } */

    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and writeMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    public static void writeField(ObjectOutput out, Map.Entry entry) throws IOException
    {
    	staticDelegate.staticWriteField(out, entry);
    }

    public void writeMyself(ObjectOutput output) throws IOException
    {
    	delegate.writeMyself(output);
    }


    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and readMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    /*public static int readNumberOfFields(ObjectInput input) throws IOException
    {
        return input.readInt();
    }*/

    public static Map.Entry readField(ObjectInput input) throws IOException,ClassNotFoundException
    {    
        return staticDelegate.staticReadField(input);
    }

    public void readMyself(ObjectInput input) throws IOException,ClassNotFoundException
    {
    	delegate.readMyself(input);
    }

    public void setField(String name, Object value)
    {
        delegate.setField(name, value);
    }

    public ObjectInputStream.GetField createGet()
    {
    	return delegate.createGet();
    }

    public ObjectOutputStream.PutField createPut()
    {
    	return delegate.createPut();
    }
}
