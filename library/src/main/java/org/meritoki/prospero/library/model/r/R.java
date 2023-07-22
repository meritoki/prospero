package org.meritoki.prospero.library.model.r;

import org.meritoki.prospero.library.model.provider.Provider;

public class R extends Provider {

	public R() {
		super("R");
		// TODO Auto-generated constructor stub
	}
	
	public R(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		//Check for R Installation, Provide Scripts to Install
	}

}
