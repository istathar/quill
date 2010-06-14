/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.render.RenderEngine;
import quill.client.ImproperFilenameException;
import quill.client.RecoveryFileExistsException;
import quill.textbase.ComponentSegment;
import quill.textbase.Folio;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.TextChain;

/**
 * A document on disk in a .parchment file contining a <manuscript> root
 * element.
 * 
 * This class is an intermediary, not an encapsulation of the data itself,
 * which is why you pass a Folio to the save method, and get a Folio after
 * loading.
 * 
 * As the undo/redo capability only makes sense on a document by document
 * basis the action methods are hosted here.
 * 
 * @author Andrew Cowie
 */
public class Manuscript
{
    private String directory;

    private String basename;

    private String filename;

    private Class<RenderEngine> engine;

    private Chapter[] chapters;

    /**
     * Create a new Manuscript intermediary.
     */
    public Manuscript() {
        chapters = null; // FIXME
    }

    public Manuscript(String pathname) throws ImproperFilenameException {
        setFilename(pathname);
        chapters = null; // FIXME
    }

    /**
     * Load the document: open the .parchment file this Manuscript represents,
     * and then load its chapters. Returns an internal root object
     * representing the complete state of the document as loaded.
     */
    public Folio loadDocument() throws ValidityException, ParsingException, IOException {
        String filename;
        final int size;
        int i;
        Series series;
        Chapter chapter;
        final List<Series> components;
        final List<Chapter> files;
        final Folio folio;

        components = new ArrayList<Series>();
        files = new ArrayList<Chapter>();

        /*
         * TODO load .parchment file
         */

        /*
         * FIXME load this; if we know size we can use arrays already.
         * Otherwise, we use List.
         */
        size = 1;

        for (i = 0; i < size; i++) {
            // FIXME get individual filenames out of .parchments file
            filename = "tests/SomeOfEverything.xml";

            chapter = new Chapter();
            chapter.setFilename(filename);

            series = chapter.loadDocument();

            // Hm?
            files.add(chapter);
            components.add(series);
        }

        folio = Folio.create(components);
        return folio;
    }

    /**
     * Get the full (absolute) pathname of the target document.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the full path to the directory containing the target document.
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Get the base component (the file's main name without parent directory
     * or extension suffix) of the target document.
     */
    public String getBasename() {
        return basename;
    }

    /**
     * Create a new blank document with a single Component.
     */
    public Folio createDocument() {
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

        folio = Folio.create(chapter1);

        return folio;
    }

    /**
     * Find out if the given filename exists, is a loadable document, and
     * check to make sure there isn't a RESCUED file lurking. Throws
     * RecoveryFileExistsException if one does.
     */
    public File checkFilename() throws FileNotFoundException, RecoveryFileExistsException {
        final File source, probe;

        source = new File(filename).getAbsoluteFile();
        if (!source.exists()) {
            throw new FileNotFoundException("\n" + filename);
        }

        probe = new File(filename + ".RESCUED");
        if (probe.exists()) {
            throw new RecoveryFileExistsException(probe.toString());
        }

        return source;
    }

    /**
     * Set the filename that this document is being saved to. Things like
     * overwriting confirmation should have been done by the UI already.
     */
    /*
     * configure the filename fields
     */
    public void setFilename(String path) throws ImproperFilenameException {
        File proposed, absolute;
        final String name;
        final int i;

        proposed = new File(path);
        if (proposed.isAbsolute()) {
            absolute = proposed;
        } else {
            absolute = proposed.getAbsoluteFile();
        }

        directory = absolute.getParent();
        name = absolute.getName();

        i = name.indexOf(".parchment");
        if (i == -1) {
            throw new ImproperFilenameException("\n"
                    + "Manuscript files must have a .parchment extension");
        }
        basename = name.substring(0, i);
        filename = absolute.getPath();
    }

    public void saveDocument(Folio folio) throws IOException {
        int i;

        for (i = 0; i < folio.size(); i++) {

        }
    }

    /**
     * Attempt to serialize out the current document. No guarantees. This
     * method assumes it's being called immediately prior to program
     * termination as a result of an Exception. It doesn't bother throwing
     * anything of its own.
     */
    public void emergencySave(final Folio folio) {
        final String savename;
        File target = null;
        final PrintStream err;
        int i;

        err = System.err;
        err.flush();
        err.print("Attempting emergency save... ");
        err.flush();

        if (filename == null) {
            filename = "Untitled.xml"; // gotta call it something
        }

        try {
            savename = filename + ".RESCUED";
            target = new File(savename);

            if (target.exists()) {
                err.println("inhibited.");
                err.println("There's already a recovery file,\n" + savename + "\n"
                        + "and we're not going to overwrite it.");
                return;
            }

            for (i = 0; i < chapters.length; i++) {
                chapters[i].saveDocument(folio.get(i));
            }
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
