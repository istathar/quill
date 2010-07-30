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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.render.RenderEngine;
import quill.client.ImproperFilenameException;
import quill.client.RecoveryFileExistsException;
import quill.textbase.Folio;
import quill.textbase.Series;

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

    /**
     * Create a new Manuscript intermediary.
     */
    public Manuscript() {}

    public Manuscript(String pathname) throws ImproperFilenameException {
        setFilename(pathname);
    }

    /**
     * Load the document: open the .parchment file this Manuscript represents,
     * and then load its chapters. Returns an internal root object
     * representing the complete state of the document as loaded.
     */
    public Folio loadDocument() throws ValidityException, ParsingException, IOException,
            ImproperFilenameException, InvalidDocumentException {
        final File source;
        final Builder parser;
        final Document doc;
        final ManuscriptLoader loader;
        final int size;
        String[] sources;
        int i;
        Series series;
        Chapter chapter;
        final List<Series> components;
        final List<Chapter> chapters;
        final Folio folio;

        components = new ArrayList<Series>();
        chapters = new ArrayList<Chapter>();

        source = new File(filename);
        parser = new Builder();
        doc = parser.build(source);

        loader = new ManuscriptLoader(doc);
        sources = loader.getChapterSources();

        /*
         * FIXME load this; if we know size we can use arrays already.
         * Otherwise, we use List.
         */
        size = sources.length;

        for (i = 0; i < size; i++) {
            /*
             * Setting the chapter filename results in the normal checked
             * exception ImproperFilenameException. When loading it would be
             * reasonable to make the assumption that the given pathname is
             * valid, which it should be when sourcing from a .parchment file
             * that we wrote. However, someone could have manually screwed
             * with the contents of the .parchment file resulting in an
             * illegal chapter filename but if that's happened then we have
             * bigger problems.
             */

            chapter = new Chapter(this);
            chapter.setFilename(sources[i]);

            series = chapter.loadDocument();

            // Hm?
            chapters.add(chapter);
            components.add(series);
        }

        folio = new Folio(this, chapters, components);
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
        final Series series1;
        final Folio folio;
        final Chapter chapter1;

        chapter1 = new Chapter(this);
        series1 = chapter1.createDocument();

        folio = new Folio(this, chapter1, series1);

        try {
            this.setFilename("Untitled.parchment");
            chapter1.setFilename("Chapter1.xml");
        } catch (ImproperFilenameException ife) {
            throw new IllegalStateException();
        }

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

    /**
     * Save the state described by the given Folio into the set of files
     * described by this Manuscript.
     */
    /*
     * FIXME! Process names appropriate to actual inbound Folio, not existing
     * Chapters
     */
    public void saveDocument(Folio folio) throws IOException {
        int i;
        Series series;
        Chapter chapter;

        this.saveDocument0(folio);

        /*
         * Now save chapters
         */

        for (i = 0; i < folio.size(); i++) {
            series = folio.getSeries(i);
            chapter = folio.getChapter(i);
            chapter.saveDocument(series);
        }
    }

    private void saveDocument0(Folio folio) throws IOException {
        final File target, tmp;
        final FileOutputStream out;
        boolean result;
        String dir, path;
        final ManuscriptConverter converter;

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

            converter = new ManuscriptConverter(folio);
            converter.writeManuscript(out);

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
            throw new IOException("Unable to rename temporary file to target document!");
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
        Chapter chapter;
        Series series;
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

            for (i = 0; i < folio.size(); i++) {
                chapter = folio.getChapter(i);
                series = folio.getSeries(i);

                chapter.saveDocument(series);
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
