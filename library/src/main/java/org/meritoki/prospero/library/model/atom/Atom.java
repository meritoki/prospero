package org.meritoki.prospero.library.model.atom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.atom.particale.Electron;
import org.meritoki.prospero.library.model.atom.particale.Proton;
import org.meritoki.prospero.library.model.node.Spheroid;

public class Atom extends Spheroid {
	
	public List<Proton> protonList = new ArrayList<>();
	public List<Electron> electronList = new ArrayList<>();
	public Map<Electron,Double> radiusMap;

	public Atom(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public void addElectron(Electron electron) {
		this.electronList.add(electron);
	}
	
	public void addProton(Proton proton) {
		this.protonList.add(proton);
	}
	

}
