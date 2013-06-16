package com.hi.datacleaner.ruby;

import org.eobjects.analyzer.beans.api.OutputRowCollector;
import org.eobjects.analyzer.data.InputRow;

public interface RubyTransformer {

    void initialize();

    void close();

    void transform(InputRow row, OutputRowCollector rowCollector);

}
