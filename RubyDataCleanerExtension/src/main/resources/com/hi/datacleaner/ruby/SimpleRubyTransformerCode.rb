class Transformer
  java_implements com.hi.datacleaner.ruby.RubyTransformer
  
  java_signature 'void init()'
  def init
    p "initialized."
  end

  java_signature 'void transform(org.eobjects.analyzer.data.InputRow row, org.eobjects.analyzer.beans.api.OutputRowCollector outputCollector)'
  def transform(row, outputCollector)
    p "transforming..."
    outputCollector.putValues("1234");
  end

  java_signature 'void close()'
  def close
    p "closed."
  end
end