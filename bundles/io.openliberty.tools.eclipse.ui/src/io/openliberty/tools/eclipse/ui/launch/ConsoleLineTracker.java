package io.openliberty.tools.eclipse.ui.launch;

import java.net.URL;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.IHyperlink;

public class ConsoleLineTracker implements IConsoleLineTracker {

	class AbstractHyperlink implements IHyperlink {
        @Override
        public void linkEntered() {
            // do nothing
        }

        @Override
        public void linkExited() {
            // do nothing
        }

        @Override
        public void linkActivated() {
            // do nothing
        }
    }

    private IConsole console;

    @Override
    public void init(IConsole console) {
        this.console = console;
    }

    @Override
    public void lineAppended(IRegion line) {
        try {
            int offset = line.getOffset();
            int length = line.getLength();
            String text = console.getDocument().get(offset, length);

            if (text == null || length == 0)
                return;

          if (text.contains("http")) {
              addBrowserLinkToConsole(text, offset, length, "http");
          } else if(text.contains("https")) {
              addBrowserLinkToConsole(text, offset, length, "https");
          }

        } catch (Exception e) {
            // ignore, not critical
        }
           
    }
    

	 /**
     * Parse the given console message text to determine the URL and add a browser link to the console.
     */
    private void addBrowserLinkToConsole(String text, int offset, int length, String prefix) {
        if (console == null)
            return;

        int start = text.indexOf(prefix, prefix.length());
        final String url = text.substring(start, length);;
        IHyperlink link = new AbstractHyperlink() {
            @Override
            public void linkActivated() {
                openBrowser(url);
            }
        };
        console.addLink(link, offset + start, length - start);
    }

    @SuppressWarnings("deprecation")
	protected void openBrowser(String urlString) {
        try {
            IWorkbenchBrowserSupport bs = PlatformUI.getWorkbench().getBrowserSupport();
            IWebBrowser b = bs.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);
            b.openURL(new URL(urlString));
        } catch (Exception e) {
           
        }
    }


	@Override
	public void dispose() {
		// ignore
		
	}
}
