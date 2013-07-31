class Transformer
	def init
		p "initialized."
	end
	
	def transform(inputValues, outputCollector)
		inputValues.each {
			|value|
			outputCollector.putValues(value)
		}
	end
	
	def close
		p "closed."
	end
end