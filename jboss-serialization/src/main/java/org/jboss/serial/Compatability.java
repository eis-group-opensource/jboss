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

package org.jboss.serial;

/**
 * @author bmaxwell
 *
 */
public class Compatability {
	
	// JBSER-128 - this is the recommended property to set to true, as it should handle objects doing unbalanced 
	public static final boolean SYNC_BINARY_FORMATS2 = Boolean.parseBoolean( System.getProperty("org.jboss.serial.sync_binary_formats", "false") );
	
	// JBSER-121 - this resolved some compatibilty issues, but did not cover all possible unbalacned use cases
  public static final boolean SYNC_SERIALIZATION_BINARY_FORMATS = Boolean.parseBoolean( System.getProperty("org.jboss.serial.SYNC_SERIALIZATION_BINARY_FORMATS", "false") );   	

}
