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

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.PdfSurface;
import org.freedesktop.cairo.Surface;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.PaperSize;
import org.gnome.gtk.Unit;

import parchment.render.RenderEngine;
import parchment.render.ReportRenderEngine;
import quill.textbase.DataLayer;
import quill.textbase.Folio;

public class RenderToPrintHarness
{
    private static DataLayer data;

    public static void main(String[] args) throws ValidityException, ParsingException, IOException {
        initializeDataLayer();
        loadExampleDocument();
        runRenderPipeline();
    }

    private static void initializeDataLayer() {
        data = new DataLayer();
    }

    private static void loadExampleDocument() throws ValidityException, ParsingException, IOException {
        Gtk.init(null);
        data.loadDocument("tests/ExampleProgram.xml");
    }

    private static void runRenderPipeline() throws IOException {
        final Context cr;
        final Surface surface;
        final Folio folio;
        final PaperSize paper;
        final RenderEngine engine;

        /*
         * Setup a bogus target surface
         */

        paper = PaperSize.A4;
        // paper = new CustomPaperSize("Widescreen", 200.0, 150.0, Unit.MM);

        surface = new PdfSurface("tmp/Render.pdf", paper.getWidth(Unit.POINTS),
                paper.getHeight(Unit.POINTS));
        cr = new Context(surface);

        folio = data.getActiveDocument();

        engine = new ReportRenderEngine(paper, data, folio.get(0));
        // engine = new ScreenRenderEngine(paper, folio.get(0));
        engine.render(cr);

        surface.finish();
    }
}
