package com.hi.datacleaner.ruby;

import java.util.Collections;

import javax.inject.Inject;

import org.eobjects.analyzer.beans.api.Close;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Initialize;
import org.eobjects.analyzer.beans.api.OutputColumns;
import org.eobjects.analyzer.beans.api.OutputRowCollector;
import org.eobjects.analyzer.beans.api.Provided;
import org.eobjects.analyzer.beans.api.StringProperty;
import org.eobjects.analyzer.beans.api.Transformer;
import org.eobjects.analyzer.beans.api.TransformerBean;
import org.eobjects.analyzer.data.InputRow;
import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

@TransformerBean("Ruby transformer (simple)")
public class SimpleRubyTransformer implements Transformer<Object> {

    @Inject
    @Configured
    @StringProperty(multiline = true, mimeType = "text/ruby")
    String _code = "";

    @Inject
    @Provided
    OutputRowCollector _rowCollector;

    private Ruby _ruby;
    private RubyRuntimeAdapter _runtimeAdapter;
    private RubyTransformer _rubyTransformer;

    public OutputColumns getOutputColumns() {
        return new OutputColumns(1);
    }

    @Initialize
    public void initialize() {
        _ruby = JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
        _runtimeAdapter = JavaEmbedUtils.newRuntimeAdapter();

        final IRubyObject rubyClass = _runtimeAdapter.eval(_ruby, _code);
        final Object[] parameters = {};

        _rubyTransformer = (RubyTransformer) JavaEmbedUtils.invokeMethod(_ruby, rubyClass, "new", parameters,
                RubyTransformer.class);

        _rubyTransformer.initialize();
    }

    @Close
    public void close() {
        try {
            _rubyTransformer.close();
        } finally {
            JavaEmbedUtils.terminate(_ruby);
        }
    }

    public Object[] transform(InputRow row) {
        _rubyTransformer.transform(row, _rowCollector);
        return null;
    }

}
