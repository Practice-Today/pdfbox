/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.Security;

import org.apache.pdfbox.tools.encrypt.KeyBasedFileProcessor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Encrypt a file for E2E secure file transmission.
 * 
 * @author Glenn Wood
 */
public class E2EEncrypt
{
    static void main(String[] args) throws Exception
    {
        if( args.length != 3 )
            usage(args);
        Security.addProvider(new BouncyCastleProvider());

        File inpFile = new File(args[0]);
        File outFile = new File(args[1]);

        InputStream keyIn = (args[2].startsWith("-----BEGIN"))
        		? new ByteArrayInputStream(args[2].getBytes())
        		: new BufferedInputStream(new FileInputStream(new File(args[2])));
        		
        KeyBasedFileProcessor.encryptFile(outFile.getCanonicalPath(),
                inpFile.getCanonicalPath(), keyIn, false, false);
    }

    private static void usage(String args[])
    {
    	String crlf = System.getProperty("line.separator");
        String message = "\nUsage: java -jar " + Version.getJarName(E2EEncrypt.class) + " "+Version.getClassName(MethodHandles.lookup())+" <input-file> <output-file> <public-key-file>" + crlf;
        System.err.println(message);
        System.exit(1);
    }

}
