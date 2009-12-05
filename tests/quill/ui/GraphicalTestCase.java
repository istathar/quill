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
package quill.ui;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.gnome.gtk.Container;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Widget;

import static java.lang.Thread.sleep;

/**
 * Test cases which use GTK or other parts of java-gnome (thereby requiring
 * that Gtk.init() have been called).
 * 
 * @author Andrew Cowie
 */
/*
 * Thanks to Mariano Suárez-Alvarez and Rémi Cardona for their guidance on a
 * technique to find an available X display number.
 */
public abstract class GraphicalTestCase extends TestCase
{
    private static boolean initialized;

    private static Process virtual;

    protected void setUp() throws IOException, InterruptedException {
        final int MAX = 30;
        int i;
        File target;
        final String DISPLAY;
        final Runtime runtime;

        if (initialized) {
            checkVirtualServerRunning();
            return;
        }

        /*
         * This seems quite the kludge, but apparently this is the algorithm
         * used by X itself to find an available display number. It's also
         * used by Gentoo's virtualx eclass.
         */

        for (i = 0; i < MAX; i++) {
            target = new File("/tmp/.X" + i + "-lock");
            if (!(target.exists())) {
                break;
            }
        }
        if (i == MAX) {
            fail("\n" + "Can't find an available X server display number to use");
        }
        DISPLAY = ":" + i;

        /*
         * Xvfb arguments:
         * 
         * -ac disable access control (necessary so that other program can
         * draw there)
         * 
         * -wr white background
         * 
         * -fp built-ins workaround "fixed" font not being present.
         * 
         * Also, don't try to force Xvfb to 32 bits per pixed in -screen; for
         * some reason this makes it unable to start.
         */

        runtime = Runtime.getRuntime();

        virtual = runtime.exec("/usr/bin/Xvfb " + DISPLAY
                + " -ac -dpi 96 -screen 0 800x600x24 -wr -fp built-ins");

        sleep(100);
        checkVirtualServerRunning();

        /*
         * Attempt to terminate the virtual X server when the tests are
         * complete. This is far from bullet proof. It would be better if we
         * knew when all the tests were done running and called destroy()
         * then.
         */

        runtime.addShutdownHook(new Thread() {
            public void run() {
                if (virtual == null) {
                    return;
                }
                try {
                    virtual.destroy();
                    virtual.waitFor();
                } catch (InterruptedException e) {
                    // already exiting
                }
            }
        });

        /*
         * Finally, initialize GTK. We close stderr to prevent noise from Xlib
         * (as used by GTK) about "XRANR not being available". This of course
         * means we're missing anything else to stderr. Is that a bad idea?
         */

        System.err.close();

        Gtk.init(new String[] {
            "--display=" + DISPLAY
        });

        initialized = true;
    }

    private void checkVirtualServerRunning() {
        final String msg;

        msg = "\n" + "Xvfb didn't start";

        if (virtual == null) {
            fail(msg);
        }
        try {
            virtual.exitValue();
            fail(msg);
        } catch (IllegalThreadStateException itse) {
            // good
        }
    }

    // recursive
    protected static Widget findEditor(Widget widget) {
        final Container container;
        final Widget[] children;
        Widget child, result;
        int i;

        assertTrue(widget instanceof Container);
        container = (Container) widget;
        children = container.getChildren();

        for (i = 0; i < children.length; i++) {
            child = children[i];

            if (child instanceof NormalEditorTextView) {
                return child;
            }

            if (child instanceof Container) {
                result = findEditor(child);
                if (result != null) {
                    return result;
                }
            }

        }
        return null;
    }

    protected static String combine(String[] elements) {
        StringBuilder buf;

        buf = new StringBuilder(128);

        for (String element : elements) {
            buf.append(element);
            buf.append('\n');
        }

        return buf.toString();
    }
}
