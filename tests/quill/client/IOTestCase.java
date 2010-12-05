/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    protected static String loadFileIntoString(String filename) {
        final StringBuilder str;
        final FileReader in;
        int b;

        str = new StringBuilder(1024);

        try {
            in = new FileReader(filename);
            while ((b = in.read()) != -1) {
                str.append((char) b);
            }
            in.close();
        } catch (IOException ioe) {
            fail(ioe.getMessage());
            return null;
        }

        return str.toString();
    }

    public static void ensureDirectory(String path) {
        File dir;

        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
