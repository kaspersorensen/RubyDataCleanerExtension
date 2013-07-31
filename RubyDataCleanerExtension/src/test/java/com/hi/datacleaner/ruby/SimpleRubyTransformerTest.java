package com.hi.datacleaner.ruby;

import junit.framework.TestCase;

import org.eobjects.analyzer.beans.api.OutputRowCollector;
import org.eobjects.analyzer.data.MockInputColumn;

public class SimpleRubyTransformerTest extends TestCase {

    public void testSourceCodeLoading() throws Exception {
        String code = new SimpleRubyTransformer()._code;
        assertNotNull(code);
    }

    public void testInitializeAndClose() throws Exception {
        SimpleRubyTransformer transformer = new SimpleRubyTransformer();
        transformer.foo = new MockInputColumn<String>("foo");
        transformer._rowCollector = new OutputRowCollector() {
            @Override
            public void putValues(Object... arg0) {
                fail("shouldn't happen");
            }
        };

        assertNull(transformer.getRubyTransformer());

        transformer.initialize();

        assertNotNull(transformer.getRubyTransformer());

        transformer.close();
    }
}
