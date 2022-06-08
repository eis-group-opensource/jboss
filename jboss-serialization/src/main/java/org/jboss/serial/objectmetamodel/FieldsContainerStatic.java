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

package org.jboss.serial.objectmetamodel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

/**
 *
 * @author bmaxwell
 */
public interface FieldsContainerStatic  
{

	
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
    public void staticWriteField(ObjectOutput out, Map.Entry entry) throws IOException;

    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and readMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    /*public static int readNumberOfFields(ObjectInput input) throws IOException
    {
        return input.readInt();
    }*/


    public Map.Entry staticReadField(ObjectInput input) throws IOException,ClassNotFoundException;
}
