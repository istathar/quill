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

import java.math.BigDecimal;

import org.gnome.gdk.Color;
import org.gnome.gdk.EventFocus;
import org.gnome.gtk.Editable;
import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.StateType;
import org.gnome.gtk.Widget;

import static org.gnome.gtk.Alignment.RIGHT;

/**
 * A simple Entry constrained to be 1 digits precision, and having a "mm"
 * suffix indicating milimeters
 * 
 * @author Andrew Cowie
 */
class MilimetreEntry extends HBox
{
    private final Entry entry;

    private MilimetreEntry.Changed handler;

    private String safeValue;

    private String originalValue;

    private boolean valid;

    MilimetreEntry() {
        super(false, 0);
        final Label suffix;

        this.entry = new Entry();

        entry.setWidthChars(6);
        entry.setAlignment(RIGHT);
        super.packStart(entry, false, false, 0);

        suffix = new Label("mm");
        super.packStart(suffix, false, false, 3);

        /*
         * Colourize the entry depending on whether it has meaningful digits
         * in it. Will set safeValue if the value is valid.
         */

        entry.connect(new Entry.Changed() {
            public void onChanged(Editable source) {
                final String str;
                final double d;

                str = entry.getText();
                if (str.equals("")) {
                    return;
                }

                try {
                    d = Double.valueOf(str);
                    safeValue = constrainDecimal(d);
                    valid = true;
                    entry.modifyText(StateType.NORMAL, Color.BLACK);
                } catch (NumberFormatException nfe) {
                    /*
                     * if the user input is invalid, then we warn about it.
                     */
                    valid = false;
                    entry.modifyText(StateType.NORMAL, Color.RED);
                }
            }
        });

        /*
         * When the user "leaves" the Widget, we tidy up the number, and then
         * call the handler which causes a new Stylesheet to be made,
         * presumably.
         */

        entry.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                if (!valid) {
                    entry.setText(originalValue);
                    return false;
                }

                if (originalValue.equals(safeValue)) {
                    return false;
                } else {
                    source.activate();
                    return false;
                }
            }
        });
        entry.connect(new Entry.Activate() {
            public void onActivate(Entry source) {
                if (originalValue.equals(safeValue)) {
                    return;
                }
                entry.setText(safeValue);
                handler.onChanged(safeValue);
                originalValue = safeValue;
                valid = true;
            }
        });
    }

    /**
     * When the value has (commleted) being changed by the user. This is more
     * a "commit" than Entry.Changed, but hey.
     * 
     * @author Andrew Cowie
     */
    interface Changed
    {
        void onChanged(String value);
    }

    void connect(MilimetreEntry.Changed handler) {
        this.handler = handler;
    }

    /**
     * Given an input number, constrain to two decimal places
     * 
     * @throws NumberFormatException
     */
    static String constrainDecimal(double d) {
        final BigDecimal original, reduced;
        final long num;
        final String str;
        final StringBuffer buf;
        final int i;

        original = new BigDecimal(d);

        reduced = original.setScale(1, BigDecimal.ROUND_HALF_UP);
        num = reduced.unscaledValue().longValue();

        str = Long.toString(num);
        buf = new StringBuffer(str);
        i = str.length() - 1;

        buf.insert(i, ".");
        return buf.toString();
    }

    /**
     * Given a String value, set the Entry to show it. We assume you are
     * calling this with a valid value. This sets safeValue internally, which
     * will be used when anything requests the value of this MilimeterEntry.
     */
    void setText(String str) {
        if (str.equals(safeValue)) {
            return;
        }

        entry.setText(str);

        /*
         * Record a copy, so we can "revert" to a known good value if
         * validation fails.
         */

        originalValue = str;
        valid = true;
    }

    String getText() {
        return safeValue;
    }
}
