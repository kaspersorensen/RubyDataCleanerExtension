package com.hi.datacleaner.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eobjects.analyzer.beans.api.OutputRowCollector;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.MockInputColumn;
import org.eobjects.analyzer.data.MockInputRow;

public class SimpleRubyTransformerTest extends TestCase {

    public void testSourceCodeLoading() throws Exception {
        String code = new SimpleRubyTransformer()._code;
        assertNotNull(code);
    }

    public void testInitializeAndClose() throws Exception {
        final List<Object[]> objectsCollected = new ArrayList<Object[]>();

        SimpleRubyTransformer transformer = new SimpleRubyTransformer();
        MockInputColumn<String> col1 = new MockInputColumn<String>("foo");
        MockInputColumn<String> col2 = new MockInputColumn<String>("bar");
        transformer._inputValues = new InputColumn[] { col1, col2 };
        transformer._rowCollector = new OutputRowCollector() {
            @Override
            public void putValues(Object... values) {
                objectsCollected.add(values);
            }
        };

        assertNull(transformer.getTransformerObject());

        transformer.initialize();

        assertNotNull(transformer.getTransformerObject());

        transformer.transform(new MockInputRow().put(col1, "hello").put(col2, "world"));
        transformer.transform(new MockInputRow().put(col1, "howdy").put(col2, null));

        transformer.close();
        
        assertEquals(4, objectsCollected.size());
        assertEquals("[hello]", Arrays.toString(objectsCollected.get(0)));
        assertEquals("[world]", Arrays.toString(objectsCollected.get(1)));
        assertEquals("[howdy]", Arrays.toString(objectsCollected.get(2)));
        assertEquals("[null]", Arrays.toString(objectsCollected.get(3)));

    }
}
