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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import org.jboss.serial.classmetamodel.ClassMetaData;
import org.jboss.serial.classmetamodel.StreamingClass;
import org.jboss.serial.exception.SerializationException;
import org.jboss.serial.objectmetamodel.ObjectSubstitutionInterface;
import org.jboss.serial.objectmetamodel.ObjectsCache;

/**
 * $Id: ProxyPersister.java 231 2006-04-24 23:49:41Z csuconic $
 *
 * @author <a href="mailto:clebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class ProxyPersister implements Persister {
    private byte id;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public void writeData(ClassMetaData metaData, ObjectOutput output, Object obj, ObjectSubstitutionInterface substitution) throws IOException{
        Object handler = Proxy.getInvocationHandler(obj);

        output.writeObject(handler);
        output.writeObject(obj.getClass());
    }

    public Object readData (ClassLoader loader, StreamingClass streaming, ClassMetaData metaData, int referenceId, ObjectsCache cache, ObjectInput input, ObjectSubstitutionInterface substitution) throws IOException{

        try
        {
            Object handler = input.readObject();
            Class proxy = (Class)input.readObject();
            Constructor constructor = proxy.getConstructor(new Class[] { InvocationHandler.class });
            Object obj = constructor.newInstance(new Object[]{handler});
            cache.putObjectInCacheRead(referenceId,obj);
            return obj;
        }
        catch (ClassNotFoundException e)
        {
            throw new SerializationException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new SerializationException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new SerializationException(e);
        }
        catch (InstantiationException e)
        {
            throw new SerializationException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new SerializationException(e);
        }
    }
    
	public boolean canPersist(Object obj)
	{
		// not implemented
		return false;
	}
    
}
