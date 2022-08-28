package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.annotation.Param;
import com.maxsavteam.ciconia.annotation.ValueConstants;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import com.maxsavteam.ciconia.exception.ParameterNotPresentException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * {@link Param} processor
 * @author Max Savitsky
 * */
public class ParamHandler implements ParameterAnnotationHandler {

	@Override
	public boolean supports(Class<? extends Annotation> annotationClass) {
		return Param.class.equals(annotationClass);
	}

	@Override
	public Optional<Object> handle(Annotation annotation, Class<?> parameterType, Converter converter, RequestContext context) {
		Param param = (Param) annotation;
		JSONObject parameters = context.getParametersJsonObject();
		Object paramObject = parameters.opt(param.value());
		if (paramObject == null) {
			if (param.required()) {
				throw new ParameterNotPresentException(
						String.format(
								"Parameter \"%s\" is not present, but required for method \"%s\" (%s)",
								param.value(),
								context.getMethodMapping(),
								context.getMethodDeclarationPath()
						)
				);
			} else {
				paramObject = ValueConstants.DEFAULT_NONE.equals(param.defaultValue()) ? null : param.defaultValue();
			}
		}
		if(paramObject == null)
			return Optional.of(NULL_VALUE);
		Optional<Object> op = converter.convertToParameterType(paramObject);
		if(op.isPresent())
			return op;
		throw new IncompatibleClassException(
				String.format(
						"Parameter \"%s\" cannot be converted to declared type %s",
						param.value(),
						parameterType.getName()
				)
		);
	}
	
}

