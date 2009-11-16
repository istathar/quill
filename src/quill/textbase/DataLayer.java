/*
 * DataLayer.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.quack.QuackConverter;
import quill.quack.QuackLoader;
import quill.quack.QuackNodeFactory;

/**
 * Our in-memory intermediate representation. Provides access to mutate the
 * textbase, along with mechanisms for loading documents and getting at them
 * in memory.
 * 
 * @author Andrew Cowie
 */
/*
 * Interestingly, this has evolved towards looking less like a "layer" and
 * more like the wrapper object around a "document".
 */
public class DataLayer
{
    private ChangeStack stack;

    /**
     * The document currently being worked on.
     */
    /*
     * This assumes that Quill only edits one work at a time. That will want
     * to change in due course.
     */
    private Folio current;

    private File name;

    public DataLayer() {
        stack = new ChangeStack();

        current = null;
    }

    public void loadDocument(String filename) throws ValidityException, ParsingException, IOException {
        final File source;
        final NodeFactory factory;
        final Builder parser;
        final Document doc;
        final QuackLoader loader; // change to interface or baseclass
        final Series series;
        final Series[] collection;

        source = new File(filename).getAbsoluteFile();
        if (!source.exists()) {
            throw new FileNotFoundException("\n" + filename);
        }

        factory = new QuackNodeFactory();
        parser = new Builder(factory);
        doc = parser.build(source);

        loader = new QuackLoader();
        series = loader.process(doc);

        /*
         * TODO this only handles a work with a single component (chapter) per
         * file. That's probably right, but overall this logic is insufficient
         * to load a work with components spread across multiple files.
         */

        collection = new Series[] {
            series
        };

        loadDocument(source, new Folio(collection));
    }

    public Folio getActiveDocument() {
        return current;
    }

    public void saveDocument(OutputStream out) throws IOException {
        final Series series;
        final QuackConverter converter;
        int i;

        /*
         * On the temporary assumption that there's only one chapter being
         * edited
         */

        series = current.get(0);

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
     * Apply a Change to the data layer.
     */
    public void apply(Change change) {
        stack.apply(change);
    }

    /**
     * Undo. Return the Change which represents the delta from current to one
     * before.
     */
    public Change undo() {
        return stack.undo();
    }

    /**
     * Redo a previous undo. Returns the Change which is the delta you will
     * need to [re]apply.
     */
    public Change redo() {
        return stack.redo();
    }

    void loadDocument(File name, Folio folio) {
        this.name = name;
        this.current = folio;
    }

    /**
     * Create a new blank document with a single Component.
     */
    public void createDocument() {
        final Segment heading, para;
        TextChain chain;
        final Series chapter1;
        final Folio folio;

        heading = new ComponentSegment();
        chain = new TextChain();
        heading.setText(chain);

        para = new NormalSegment();
        chain = new TextChain();
        para.setText(chain);

        chapter1 = new Series(new Segment[] {
                heading, para
        });

        folio = new Folio(new Series[] {
            chapter1
        });

        loadDocument(null, folio);
    }

    /**
     * Returns the File probed allowing you to figure out if this was
     * successful.
     */
    public File setFilename(String filename) {
        final File proposed;

        proposed = new File(filename).getAbsoluteFile();

        if (proposed.exists()) {
            throw new IllegalArgumentException(filename);
        }

        name = proposed;
        return proposed;
    }

    public File getFilename() {
        return name;
    }

    public void saveDocument() throws IOException {
        final File tmp;
        final FileOutputStream out;
        boolean result;
        String dir, path;

        if (name == null) {
            throw new IllegalStateException("save filename not set");
        }

        if (name.exists() && (!name.canWrite())) {
            throw new IOException("Can't write to document file!\n\n" + "<i>Check permissions?</i>");
        }

        /*
         * We need a temporary file to write to, since writing is descructive
         * and we don't want to blow away the existing file if something goes
         * wrong.
         */

        tmp = new File(name.getAbsolutePath() + ".tmp");
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
            saveDocument(out);
            out.close();
        } catch (IOException ioe) {
            tmp.delete();
            throw ioe;
        }

        /*
         * And now replace the temp file over the actual document.
         */

        result = tmp.renameTo(name);
        if (!result) {
            tmp.delete();
            throw new IOException("Unbale to rename temporary file to target document!");
        }
    }

    /**
     * Attempt to serialize out the current document. No guarantees. This
     * method assumes it's being called immediately prior to program
     * termination as a result of an Exception. It doesn't bother throwing
     * anything of its own.
     */
    public void emergencySave() {
        final String fullname, savename;
        File target = null;
        final OutputStream out;
        final PrintStream err;

        err = System.err;
        err.flush();
        err.print("Attempting emergency save... ");
        err.flush();

        if (name == null) {
            fullname = "Untitled.xml"; // gotta call it something
        } else {
            fullname = name.getAbsolutePath();
        }

        try {
            savename = fullname + ".RESCUED";
            target = new File(savename);
            out = new FileOutputStream(target);
            saveDocument(out);
            out.close();
        } catch (Throwable t) {
            // well, we tried
            err.println("failed.");
            err.flush();

            if (target != null) {
                target.delete();
            }

            t.printStackTrace(err);
            return;
        }

        err.println("done. Wrote:");
        err.println(savename);
        err.flush();
    }
}
