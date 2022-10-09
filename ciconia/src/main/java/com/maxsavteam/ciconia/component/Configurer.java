package com.maxsavteam.ciconia.component;

import java.util.List;

/**
 * Represents particular configuration class.
 * @see com.maxsavteam.ciconia.annotation.Configuration
 * @author Max Savitsky
 * */
public class Configurer extends Component {

	private final List<ObjectFactoryMethod> methods;
	private final List<PostInitializationMethod> postInitializationMethods;

	public Configurer(Class<?> aClass, List<ObjectFactoryMethod> methods, List<PostInitializationMethod> postInitializationMethods) {
		super(aClass);
		this.methods = methods;
		this.postInitializationMethods = postInitializationMethods;
	}

	/**
	 * @return list of object factory methods
	 * @see com.maxsavteam.ciconia.annotation.ObjectFactory
	 * @see ObjectFactoryMethod
	 * */
	public List<ObjectFactoryMethod> getMethods() {
		return methods;
	}

	/**
	 * @return list of post initialization methods
	 * @see com.maxsavteam.ciconia.annotation.PostInitialization
	 * @see PostInitializationMethod
	 * */
	public List<PostInitializationMethod> getPostInitializationMethods() {
		return postInitializationMethods;
	}

}
