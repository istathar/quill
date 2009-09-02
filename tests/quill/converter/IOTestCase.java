/*
 * FileTestCase.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

public abstract class IOTestCase extends TestCase
{
    protected static String combine(String[] elements) {
        StringBuilder buf;

        buf = new StringBuilder(128);

        for (String element : elements) {
            buf.append(element);
            buf.append('\n');
        }

        return buf.toString();
    }

    /**
     * Take the md5 sum of the given file.
     */
    /*
     * Output conversion code from http://www.spiration.co.uk/post/1199
     */
    protected static String hash(File file) throws FileNotFoundException, IOException {
        final MessageDigest md;
        final FileInputStream in;
        final StringWriter str;
        final PrintWriter out;
        byte buf[];

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new Error("How can there be no md5 algorithm?");
        }
        md.reset();

        buf = new byte[1024];

        in = new FileInputStream(file);
        while (in.read(buf) != -1) {
            md.update(buf);
        }
        in.close();

        str = new StringWriter();
        out = new PrintWriter(str);

        for (byte b : md.digest()) {
            out.printf("%02x", 0xFF & b);
        }
        return str.toString();
    }

}
