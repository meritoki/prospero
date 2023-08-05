package org.meritoki.prospero.library.model.provider.r.tsclust;

import org.meritoki.prospero.library.model.provider.r.R;

public class TSClust extends R {

	public TSClust() {
		super("TSClust");
		// TODO Auto-generated constructor stub
	}
	
	public TSClust(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		super.init();
		//If Super Goes Well, Download and Install TSClust and Dependencies
	}

	//Wrap Calls to comparison.R, Make More Advanced Calls
}
