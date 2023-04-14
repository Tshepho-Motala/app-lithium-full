package org.springframework.cloud.netflix.feign;

import lombok.Synchronized;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LithiumFeignClientsRegistrar implements ApplicationContextAware {

	private GenericApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = (GenericApplicationContext) applicationContext;
	}

	@Synchronized
	public <T> T target(Class<T> apiType, String url, boolean useSystemAuth, T fallback) throws LithiumServiceClientFactoryException {
		
		String beanName = apiType.getName() + "-" + url + ((useSystemAuth)? "-system" : "-user");
		if (!context.containsBeanDefinition(beanName)) {
			
			log.info("No bean named " + beanName + " found, registering it.");
			
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
			
			String className = apiType.getName();
			BeanDefinitionBuilder definition = BeanDefinitionBuilder
					.genericBeanDefinition(LithiumFeignClientFactoryBean.class);
			
			String name = url;
			if ((!url.startsWith("http:")) || url.startsWith("https:")) {
				url = null;
			}
			
			FeignClient annotation = apiType.getAnnotation(FeignClient.class);
			if (annotation == null) throw new LithiumServiceClientFactoryException("The target " + apiType.getName() + " does not contain the @FeignClient annotation");
			
			definition.addPropertyValue("systemAuth", useSystemAuth);
			definition.addPropertyValue("url", null);
			definition.addPropertyValue("path", annotation.path());
			definition.addPropertyValue("name", name);
//			equals to the name if contextId wasn't set directly inside @FeignClient annotation, otherwise see FeignClientRegistrar::getContextId
			definition.addPropertyValue("contextId", name);
			definition.addPropertyValue("type", className);
			definition.addPropertyValue("decode404", annotation.decode404());
			if (fallback != null) {
				definition.addPropertyValue("fallback", fallback);
			}
//			definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
			String alias = beanName;
			AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
			beanDefinition.setPrimary(true);
			BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[] { alias });
			BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanFactory);
			
			log.info("Bean definition " + beanName + " added to context " + context);
			
		}
		
		return (T) context.getBean(beanName, apiType);
	}

}
