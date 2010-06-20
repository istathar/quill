/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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
package parchment.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.ImproperFilenameException;
import quill.quack.QuackConverter;
import quill.quack.QuackLoader;
import quill.quack.QuackNodeFactory;
import quill.textbase.ComponentSegment;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.TextChain;

/**
 * A chapter on disk in an .xml file containing a <chapter> root element.
 * 
 * This class is an intermediary, not an encapsulating container, which is why
 * save takes a Series as argument to be persisted.
 * 
 * @author Andrew Cowie
 */
public class Chapter
{
    /**
     * The Manuscript this Chapter belongs to. We allow this to be null for
     * tests?
     */
    private Manuscript parent;

    /**
     * Full pathname to .xml file
     */
    private String filename;

    /**
     * Relative path to .xml, relative to the directory of the "parent"
     * Manuscript.
     */
    private String relative;

    /**
     * This is only for testing save operations around a single Chapter and
     * it's .xml file.
     */
    Chapter() {
        parent = null;
    }

    public Chapter(Manuscript manuscript) {
        parent = manuscript;
    }

    public Series createDocument() {
        final Segment heading, para;
        TextChain chain;
        final List<Segment> list;
        final Series result;

        heading = new ComponentSegment();
        chain = new TextChain();
        heading.setText(chain);

        para = new NormalSegment();
        chain = new TextChain();
        para.setText(chain);

        list = new ArrayList<Segment>(2);
        list.add(heading);
        list.add(para);

        result = new Series(list);

        return result;
    }

    /**
     * Given this Chapter's current pathname, load and parse the document into
     * a Segment[], wrapped as a Series.
     */
    public Series loadDocument() throws ValidityException, ParsingException, IOException {
        final File source;
        final NodeFactory factory;
        final Builder parser;
        final Document doc;
        final QuackLoader loader; // change to interface or baseclass
        final Series series;

        source = new File(filename);

        factory = new QuackNodeFactory();
        parser = new Builder(factory);
        doc = parser.build(source);

        loader = new QuackLoader();
        series = loader.process(doc);

        return series;
    }

    /**
     * Specify the filename that this chapter will be serialized to. Path will
     * be converted to absolute form if it isn't there already.
     */
    public void setFilename(String path) throws ImproperFilenameException {
        File proposed, absolute;
        final String name, directory;
        final int i;
        int prefix;

        proposed = new File(path);
        if (proposed.isAbsolute()) {
            absolute = proposed;
        } else {
            absolute = proposed.getAbsoluteFile();
        }

        name = absolute.getName();

        i = name.indexOf(".xml");
        if (i == -1) {
            throw new ImproperFilenameException("\n" + "Chapter files must have a .xml extension");
        }

        filename = absolute.getPath();

        if (parent == null) {
            /*
             * For historical reasons, we parent can be null to allow isolated
             * testing of Chapters. But we'd better not hit this path in
             * normal usage. This is guarded in getRelative().
             */
            return;
        }

        directory = parent.getDirectory();

        if (!filename.startsWith(directory)) {
            throw new ImproperFilenameException(
                    "Why isn't this Chapter's filename within the Manuscript's directory?");
        }

        prefix = directory.length();
        if (filename.length() <= prefix) {
            throw new IllegalStateException(
                    "Why is the (absolute) filename not longer than the directory it is in?");
        }

        /*
         * Need to add the trailing '/' character
         */
        relative = filename.substring(prefix + 1);
    }

    /**
     * You need to have set the filename first, of course.
     */
    public void saveDocument(final Series series) throws IOException {
        final File target, tmp;
        final FileOutputStream out;
        boolean result;
        String dir, path;

        if (filename == null) {
            throw new IllegalStateException("save filename not set");
        }

        target = new File(filename);
        if (target.exists() && (!target.canWrite())) {
            throw new IOException("Can't write to document file!\n\n" + "<i>Check permissions?</i>");
        }

        /*
         * We need a temporary file to write to, since writing is descructive
         * and we don't want to blow away the existing file if something goes
         * wrong.
         */

        tmp = new File(filename + ".tmp");
        if (tmp.exists()) {
            tmp.delete();
        }
        result = tmp.createNewFile();
        if (!result) {
            dir = new File(".").getAbsolutePath();
            dir = dir.substring(0, dir.length() - 1);
            path = tmp.toString().substring(dir.length());
            throw new IOException("Can't create temporary file for saving.\n\n"
                    + "<i>Assuming all is well otherwise, remove</i>\n" + "<tt>" + path
                    + "</tt>\n<i>and try again?</i>");
        }

        try {
            out = new FileOutputStream(tmp);
            saveDocument(series, out);
            out.close();
        } catch (IOException ioe) {
            tmp.delete();
            throw ioe;
        }

        /*
         * And now replace the temp file over the actual document.
         */

        result = tmp.renameTo(target);
        if (!result) {
            tmp.delete();
            throw new IOException("Unbale to rename temporary file to target document!");
        }
    }

    public void saveDocument(final Series series, final OutputStream out) throws IOException {
        final QuackConverter converter;
        int i;

        /*
         * Create an output converter and run the segments through it to turn
         * them into DocBook elements.
         */

        converter = new QuackConverter();

        for (i = 0; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        /*
         * Finally, serialize the resultant top level.
         */

        converter.writeChapter(out);
    }

    /**
     * Get the (relative) filename of this Chapter on disk.
     */
    /*
     * Should a Manuscript be an obligitory parent of each and every Chapter?
     * It works out that way in practise, of course, but making that manditory
     * would complciate test isolation.
     */
    String getRelative() {
        if (parent == null) {
            throw new IllegalStateException("Chapter needs to be part of a Manuscript");
        }
        return relative;
    }
}
