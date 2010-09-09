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
package quill.ui;

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.VBox;

/**
 * Introduction widget. Instead of a splash screen, this starts out being
 * displayed by the PrimaryWindow.
 * 
 * @author Andrew Cowie
 */
class IntroductionWidget extends VBox
{
    private final VBox top;

    IntroductionWidget() {
        super(false, 0);
        final Label title, description, help;
        final HBox box;

        top = this;

        title = new Label("<span font='DejaVu Serif, 34' color='darkgreen'>Quill </span>"
                + "<span font='Inconsolata, 36' color='darkblue'>and</span>"
                + "<span font='Linux Libertine O, 40'> Parchment</span>");
        title.setUseMarkup(true);
        top.packStart(title, true, false, 0);

        description = new Label(
                "This is Quill, a <u>W</u>hat <u>Y</u>ou <u>S</u>ee <u>I</u>s <u>W</u>hat <u>Y</u>ou <u>N</u>eed document editor, "
                        + "attempting to give you a clean display of your content and subtle indications of semantic markup."
                        + "\n\n"
                        + "Parchment a simple but powerful rendering "
                        + "back-end which takes these documents and outputs them as PDF files. "
                        + "Different document types have different rendering engines (ie, stylesheets) customized for the purpose.");
        description.setLineWrap(true);
        description.setUseMarkup(true);
        top.packStart(description, true, true, 0);

        help = new Label("Press F1 for help.");
        help.setLineWrap(true);
        help.setUseMarkup(true);
        top.packStart(help, true, true, 0);

        box = new HBox(true, 6);

        top.packStart(box, true, true, 0);
    }
}
