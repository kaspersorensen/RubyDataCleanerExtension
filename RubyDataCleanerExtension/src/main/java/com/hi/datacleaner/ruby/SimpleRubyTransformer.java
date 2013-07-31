package com.hi.datacleaner.ruby;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

import javax.inject.Inject;

import org.eobjects.analyzer.beans.api.Categorized;
import org.eobjects.analyzer.beans.api.Close;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Initialize;
import org.eobjects.analyzer.beans.api.OutputColumns;
import org.eobjects.analyzer.beans.api.OutputRowCollector;
import org.eobjects.analyzer.beans.api.Provided;
import org.eobjects.analyzer.beans.api.StringProperty;
import org.eobjects.analyzer.beans.api.Transformer;
import org.eobjects.analyzer.beans.api.TransformerBean;
import org.eobjects.analyzer.beans.categories.ScriptingCategory;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.InputRow;
import org.eobjects.datacleaner.util.ResourceManager;
import org.eobjects.metamodel.util.FileHelper;
import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransformerBean("Ruby transformer (simple)")
@Categorized(ScriptingCategory.class)
public class SimpleRubyTransformer implements Transformer<Object> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRubyTransformer.class);

    @Inject
    @Configured
    @StringProperty(multiline = true, mimeType = "text/ruby")
    String _code = getInitialCode();

    @Configured
    InputColumn<?> foo;

    @Inject
    @Provided
    OutputRowCollector _rowCollector;

    private Ruby _ruby;
    private RubyRuntimeAdapter _runtimeAdapter;
    private RubyTransformer _rubyTransformer;

    public OutputColumns getOutputColumns() {
        return new OutputColumns("Ruby output");
    }

    private String getInitialCode() {
        final String resourceName = "com/hi/datacleaner/ruby/SimpleRubyTransformerCode.rb";
        final URL url = ResourceManager.getInstance().getUrl(resourceName, getClass().getClassLoader());
        try {
            final InputStream in = url.openStream();
            try {
                final String code = FileHelper.readInputStreamAsString(in, "UTF8");
                return code;
            } finally {
                FileHelper.safeClose(in);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read resource: " + resourceName, e);
        }
    }

    @Initialize
    public void initialize() {
        _ruby = JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
        _runtimeAdapter = JavaEmbedUtils.newRuntimeAdapter();
        
        // load the class def
        _runtimeAdapter.eval(_ruby, _code);
        
        IRubyObject rubyObject = _runtimeAdapter.eval(_ruby, "Transformer.new()");
        
        logger.info("Evaluated Ruby code to: {}", rubyObject);
        
        _rubyTransformer = (RubyTransformer) JavaEmbedUtils.rubyToJava(_ruby, rubyObject, RubyTransformer.class);

        _rubyTransformer.init();
    }

    @Close
    public void close() {
        try {
            _rubyTransformer.close();
        } catch (Exception e) {
            logger.error("Failed to call close() on RubyTransformer", e);
        }

        try {
            JavaEmbedUtils.terminate(_ruby);
        } catch (Exception e) {
            logger.error("Failed to terminate JRuby instance", e);
        }
    }

    public Object[] transform(InputRow row) {
        _rubyTransformer.transform(row, _rowCollector);
        return null;
    }

    public RubyTransformer getRubyTransformer() {
        return _rubyTransformer;
    }
}