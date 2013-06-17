package com.hi.datacleaner.ruby;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eobjects.analyzer.beans.api.Close;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Initialize;
import org.eobjects.analyzer.beans.api.OutputColumns;
import org.eobjects.analyzer.beans.api.StringProperty;
import org.eobjects.analyzer.beans.api.Transformer;
import org.eobjects.analyzer.beans.api.TransformerBean;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.InputRow;
import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

@TransformerBean("Ruby transformer (simple)")
public class SimpleRubyTransformer implements Transformer<Object> {

    private final Object[] emptyParams = {};

    @Inject
    @Configured
    InputColumn<?>[] _columns;

    @Inject
    @Configured
    @StringProperty(multiline = true, mimeType = "text/ruby")
    String _code = "class Transformer\n" + "\tdef init()\n\t\tputs 'Initializing'\n\tend"
            + "\n\tdef transform(values)\n\t\tvalues.get('col1')\n\tend" + "\n\tdef close()\n\tend" + "\nend\n";

    private Ruby _ruby;
    private RubyRuntimeAdapter _runtimeAdapter;
    private IRubyObject _rubyTransformer;

    public OutputColumns getOutputColumns() {
        return new OutputColumns(1);
    }

    @Initialize
    public void initialize() {
        _ruby = JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
        _runtimeAdapter = JavaEmbedUtils.newRuntimeAdapter();

        final IRubyObject rubyClass = _runtimeAdapter.eval(_ruby, _code + "\nTransformer");

        _rubyTransformer = (IRubyObject) JavaEmbedUtils.invokeMethod(_ruby, rubyClass, "new", emptyParams,
                IRubyObject.class);

        JavaEmbedUtils.invokeMethod(_ruby, _rubyTransformer, "init", emptyParams, Object.class);
    }

    @Close
    public void close() {
        try {
            JavaEmbedUtils.invokeMethod(_ruby, _rubyTransformer, "close", emptyParams, Object.class);
        } finally {
            JavaEmbedUtils.terminate(_ruby);
        }
    }

    public Object[] transform(InputRow row) {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        for (int i = 0; i < _columns.length; i++) {
            Object value = row.getValue(_columns[i]);
            String name = _columns[i].getName();
            values.put(name, value);
        }
        final Object[] parameters = new Object[] { values };
        Object result = JavaEmbedUtils.invokeMethod(_ruby, _rubyTransformer, "transform", parameters, Object.class);
        return new Object[] { result };
    }

}
