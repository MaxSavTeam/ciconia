package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.InstantiationException;

public interface InstantiatableObjectFactory {

	Object create(ObjectsDatabase database) throws InstantiationException;

}
