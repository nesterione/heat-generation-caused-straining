package by.nesterenya.fem.element.material;

/**
 * Thermal property material
 * 
 */
public class Material {

	private String name;

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

	public double getThermalConductivity() {
		return thermalConductivity;
	}

	public void setThermalConductivity(double thermalConductivity) {
		this.thermalConductivity = thermalConductivity;
	}

	public double getSpecificHeatCapacity() {
		return specificHeatCapacity;
	}

	public void setSpecificHeatCapacity(double specificHeatCapacity) {
		this.specificHeatCapacity = specificHeatCapacity;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Плотность материала
	 */
	private double density;

	/**
	 * Коэффициент теплопроводности
	 */
	private double thermalConductivity;

	/**
	 * Удельная теплоемкость
	 */
	private double specificHeatCapacity;

	public String getName() {

		return name;
	}

	/**
	 * Модуль упругости, Юнга
	 */
	private double elasticModulus;

	/**
	 * Коэффициента пуассона
	 */
	private double poissonsRatio;

	/**
	 * Коэффициента линейного расширения
	 */
	// private double thermalExpansion;

	public double getPoissonsRatio() {
		return poissonsRatio;
	}

	public void setPoissonsRatio(double poissonsRatio) {
		this.poissonsRatio = poissonsRatio;
	}

	public double getElasticModulus() {
		return elasticModulus;
	}

	public void setElasticModulus(double elasticModulus) {
		this.elasticModulus = elasticModulus;
	}
}
