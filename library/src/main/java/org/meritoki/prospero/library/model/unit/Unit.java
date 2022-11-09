/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.unit;

public class Unit {
	
	public static void main(String[] args) {
		System.out.println(Unit.C);
		System.out.println(Unit.GRAVITY_CHARGE_CONSTANT/C);
		System.out.println(G/k);
		System.out.println(A);
		System.out.println(B);
		//System.out.println((Unit.G_k)*(Unit.ELECTRON_MASS/Unit.COULOMBS));
		//System.out.println(Unit.GRAVITY_CHARGE_CONSTANT/OSVALDO);
	}
	
	public static double ASTRONOMICAL = 149597828.67728;//Kilometers
	public static double EARTH_RADII = 6371;//Kilometers
	public static double GRAVITATIONAL_CONSTANT = 6.673e-11;
	public static double G=GRAVITATIONAL_CONSTANT;
	public static double LIGHT_SPEED = 299792458;
	public static double COULOMBS = 1/6.25e18;//1.6e-19;
	public static double COULOMBS_CONSTANT= 8.9875517923e9;
	public static double k = COULOMBS_CONSTANT;
	public static double c = LIGHT_SPEED;
	public static double ELECTRON_RADIUS = (2.8179403227e-15);
	public static double ELECTRON_MASS = 9.10938356e-31;
	public static double GRAVITY_CHARGE_CONSTANT=-2.406676576927117E-43;//-1.9253412615416936E-42;
	public static double G_k = G/k;
	public static double C = (Unit.G_k)*Unit.A;
	public static double A = ((Unit.ELECTRON_MASS*Unit.ELECTRON_MASS)/(Unit.COULOMBS*Unit.COULOMBS));
	public static double B = (1/(Unit.COULOMBS*Unit.COULOMBS));
}
//public static double OSVALDO=-Math.pow(5,11)*1e29;//Eureka//-4.8828125e36;////-3.90625e37/8;//5e11
//public static double CANZIANI=-1.79e23;
//public static double PROSPERO = (Unit.GRAVITATIONAL_CONSTANT/Unit.k)/Unit.CHARGE/Unit.OSVALDO;//*Unit.CANZIANI;
