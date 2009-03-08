/*
 * HelpWidget.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Label;

class HelpWidget extends Label
{
    private Label label;

    HelpWidget() {
        super();
        label = this;

        label.setUseMarkup(true);
        label.setLineWrap(true);

        label.setLabel("<big>Help</big>\n" + "There is help for you after all. "
                + "The real question is whether or not you'll be willing to listen. "
                + "And that, truly, is something I'm going to need help with.");
    }
}
