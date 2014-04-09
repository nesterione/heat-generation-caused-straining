package by.nesterenya.fem.element;

import by.nesterenya.fem.element.material.*;

/**
 * 
 * @author igor
 * Интерфейс элемента, общий интерфей для всех конечных элементов
 * Имеет два метода, для получения свойств материала
 */
public interface IElement {
	public IMaterial getMatherial();
	
	INode getNode(int number) throws Exception;
}
