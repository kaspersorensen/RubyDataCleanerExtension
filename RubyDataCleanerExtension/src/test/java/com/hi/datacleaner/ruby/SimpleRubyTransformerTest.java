package com.hi.datacleaner.ruby;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.MockInputColumn;
import org.eobjects.analyzer.data.MockInputRow;

public class SimpleRubyTransformerTest extends TestCase {

    public void testCompileAndRun() throws Exception {
        final InputColumn<?> col1 = new MockInputColumn<String>("col1");
        final InputColumn<?> col2 = new MockInputColumn<String>("col2");

        final SimpleRubyTransformer transformer = new SimpleRubyTransformer();
        transformer._columns = new InputColumn[] { col1, col2 };
        transformer.initialize();

        Object[] result = transformer.transform(new MockInputRow().put(col1, "foo").put(col2, "bar"));
        assertEquals("[foo]", Arrays.toString(result));

        transformer.close();
    }
}
