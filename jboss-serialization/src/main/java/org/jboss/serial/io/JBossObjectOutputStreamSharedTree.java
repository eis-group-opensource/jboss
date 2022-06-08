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

package org.jboss.serial.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

import org.jboss.serial.objectmetamodel.DataContainer;
import org.jboss.serial.objectmetamodel.DataContainerConstants;
import org.jboss.serial.util.ClassMetaConsts;
import org.jboss.serial.util.StringUtilBuffer;

/** This implementation will respect reset commands. */
public class JBossObjectOutputStreamSharedTree extends JBossObjectOutputStream
{
	ObjectOutput objectOutput;
	DataContainer dataContainer;
	
	public JBossObjectOutputStreamSharedTree(OutputStream output, boolean checkSerializableClass, StringUtilBuffer buffer) throws IOException {
		super(output, checkSerializableClass, buffer);
	}

	public JBossObjectOutputStreamSharedTree(OutputStream output, boolean checkSerializableClass) throws IOException {
		super(output, checkSerializableClass);
	}

	public JBossObjectOutputStreamSharedTree(OutputStream output, StringUtilBuffer buffer) throws IOException {
		super(output, buffer);
	}

	public JBossObjectOutputStreamSharedTree(OutputStream output) throws IOException {
		super(output);
	}

	protected void writeObjectOverride(Object obj) throws IOException {
		checkOutput();
        objectOutput.writeObject(obj);
	}
	
	public void reset() throws IOException {
		checkOutput();
		objectOutput.writeByte(DataContainerConstants.RESET);
		dataContainer.getCache().reset();
	}

	private void checkOutput() throws IOException {
		if (objectOutput==null)
		{
	        dataContainer = new DataContainer(null,this.getSubstitutionInterface(),checkSerializableClass,buffer);
	        if (output instanceof DataOutputStream)
	        {
	            dataOutput = (DataOutputStream) output;
	        }
	        else
	        {
	            dataOutput = new DataOutputStream(output);
	        }
	
	        objectOutput = dataContainer.getDirectOutput(this.dataOutput);
		}
	}
	
	

}
