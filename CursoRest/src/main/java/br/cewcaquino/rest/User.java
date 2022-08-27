package br.cewcaquino.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user") //define o nome da estrutura da tag
@XmlAccessorType(XmlAccessType.FIELD) //pega as demais vari�veis e cria os elementos necess�rios respectivos
public class User {
	
	@XmlAttribute
	private Long id;
	private String name;
	private Integer age;
	private Double salary;
	
	//construtor utilizado para permitir a serializa��o doXML
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	//construtor utilizado para a serializa��o do Json
	public User(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", salary=" + salary + "]";
	}

}
