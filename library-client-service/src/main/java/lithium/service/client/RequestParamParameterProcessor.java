package lithium.service.client;

import static feign.Util.emptyToNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.web.bind.annotation.RequestParam;

import feign.MethodMetadata;

public class RequestParamParameterProcessor implements AnnotatedParameterProcessor {
	private static final Class<RequestParam> ANNOTATION = RequestParam.class;

	public Class<? extends Annotation> getAnnotationType() {
		return ANNOTATION;
	}

	@Override
	public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
		String name = ANNOTATION.cast(annotation).value();
		if (emptyToNull(name) != null) {
			context.setParameterName(name);

			MethodMetadata data = context.getMethodMetadata();
			Collection<String> query = context.setTemplateParameter(name, data.template().queries().get(name));
			data.template().query(name, query);
		} else {
			MethodMetadata data = context.getMethodMetadata();
			data.queryMapIndex(context.getParameterIndex());
		}
		return true;
	}
}