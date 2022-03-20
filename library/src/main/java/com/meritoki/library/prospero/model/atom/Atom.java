package com.meritoki.library.prospero.model.atom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.meritoki.library.prospero.model.atom.particale.Electron;
import com.meritoki.library.prospero.model.atom.particale.Proton;
import com.meritoki.library.prospero.model.solar.unit.Spheroid;

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
