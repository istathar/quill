/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.PdfSurface;
import org.freedesktop.cairo.Surface;
import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.PaperSize;
import org.gnome.gtk.Unit;

import parchment.format.Manuscript;
import parchment.render.RenderEngine;
import parchment.render.ReportRenderEngine;
import quill.textbase.Folio;
import quill.ui.UserInterface;

/**
 * Front end allowing you to render the Quack XML document named on the
 * command line using the Parchment rendering engine to produce printable
 * output as a PDF.
 * 
 * @author Andrew Cowie
 */
/*
 * Forked from quill.client.Quill; seeing as how much is common between these
 * two pieces of code, it may be that more code can push out of here and go
 * elsewhere in quill.client
 */
public class Render
{
    public static UserInterface ui;

    private static Manuscript manuscript;

    private static Folio folio;

    public static void main(String[] args) throws Exception {
        try {
            initializeUserInterface(args);
            parseCommandLine(args);
            runRenderPipeline();
        } catch (SafelyTerminateException ste) {
            // quietly supress
            return;
        }
    }

    /*
     * We do need to initialize java-gnome. We don't need to initialize the
     * Quill UI.
     */
    static void initializeUserInterface(String[] args) {
        Glib.setProgramName("quill");
        Gtk.init(args);
    }

    /**
     * Parse arguments from command line. See
     * {@link Quill#parseCommandLine(String[])} for a discussion of how hard
     * this is.
     */
    /*
     * TODO parse arguments properly here.
     */
    static void parseCommandLine(String[] args) throws Exception {
        if (args.length == 1) {
            loadDocumentFile(args[0]);
        } else {
            System.err.println("ERROR: Please supply one filename to render.");
            throw new SafelyTerminateException();
        }
    }

    static void loadDocumentFile(String filename) throws Exception {
        final Manuscript attempt;

        try {
            attempt = new Manuscript(filename);
            attempt.checkFilename();
            manuscript = attempt;
            folio = manuscript.loadDocument();
        } catch (FileNotFoundException fnfe) {
            System.err.println("ERROR: File not found?" + "\n" + fnfe.getMessage());
            throw new SafelyTerminateException();
        }
    }

    /**
     * Run a Parchment RenderEngine. The filename logic is copied from
     * {@link quill.ui.UserInterface}'s printDocument(), and probably
     * shouldn't be duplicated, BUT see the discussion there about forthcoming
     * document level output targets.
     */
    static void runRenderPipeline() throws IOException {
        final String parentdir, basename, targetname;
        final Context cr;
        final Surface surface;
        final PaperSize paper;
        final RenderEngine engine;

        paper = PaperSize.A4;

        parentdir = manuscript.getDirectory();
        basename = manuscript.getBasename();
        targetname = parentdir + "/" + basename + ".pdf";

        surface = new PdfSurface(targetname, paper.getWidth(Unit.POINTS), paper.getHeight(Unit.POINTS));
        cr = new Context(surface);

        engine = new ReportRenderEngine(paper, manuscript, folio.get(0));
        engine.render(cr);

        surface.finish();
    }
}
