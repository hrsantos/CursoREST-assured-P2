package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserXMLTest {
	
	// Nota��o que permite parametrizar algumas a��es antes de disparar os testes
	// possibilitando configurar a rota base 
	@BeforeClass
	public static void setup() {
		//configura��o da base da rota
		RestAssured.baseURI = "http://restapi.wcaquino.me";
		//configura��o da porta da rota
		//RestAssured.port = "";
		//configura��o do path/caminho da rota
		//RestAssured.basePath = "";
		
		/* � poss�vel definir especifica��es comuns de requests e responses a diversos cen�rios   
		 * para isso deve-se utilizar o RequestBuilder/ResponseBuilder para criar os par�metros 
		 * da request/response e em seguida atribuir a um RequestSpecification/ResponseSpecification 
		 * para criar a spec (modelo)
		 * Esse recurso pode ser implementado dentro do teste ou na classe utilizando vari�veis
		 * est�ticas, ou ainda, dentro do m�todo de "setup" conforme adotado no exmplo abaixo 
		 */
		
		//criando uma spec de request
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL); //solicita na requisi��o o log da request
		RequestSpecification reqSpec = reqBuilder.build();
		
		//criando uma spec de request
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200); // solicita o statusCode da response
		ResponseSpecification resSpec = resBuilder.build();
		
		/* atribui��o das specs que j� ser�o verificadas antes da execu��o dos testes, n�o sendo nesse caso
		*  necess�rio inserir a chamada da spec diretamente no teste
		*/
		RestAssured.requestSpecification = reqSpec;
		RestAssured.responseSpecification= resSpec;
	}
	
	@Test
	public void devoTrabalharComXML() {
//		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
//		reqBuilder.log(LogDetail.ALL);
//		RequestSpecification reqSpec = reqBuilder.build();
//		
//		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
//		resBuilder.expectStatusCode(200);
//		ResponseSpecification resSpec = resBuilder.build();
		
		given()
			//.spec(reqSpec)
		.when()
			//.get("http://restapi.wcaquino.me/usersXML/3")
			.get("/usersXML/3")
		.then()
			//.statusCode(200)
			//.spec(resSpec)
			.body("user.name", is("Ana Julia"))
			.body("user.@id", is("3"))
			.body("user.name.size()", is(1))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItem("Zezinho"))
			.body("user.filhos.name", hasItems("Zezinho","Luizinho"))
		;
	}
	
	@Test
	public void devoConhecerNoRaiz() {
		//� poss�vel simplificar a navega��o entre os n�s estabelecendo o inicio da navega��o pretendida
		given()
		.when()
			.get("/usersXML/3")
		.then()
			//.statusCode(200)
			
			//utiliza-se "rootPath" para suprimir a necessidade da declara��o do elemento para se navegar em seu conte�do 
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			//pode-se encadear essa supress�o
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			//para retornar a declara��o de um elemento suprimido, utiliza-se "detachRootPath"
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			//para voltar a suprimir o elemento, utiliza-se "appendRootPath"
			.appendRootPath("filhos")
			.body("name", hasItem("Zezinho"))
			.body("name", hasItems("Zezinho","Luizinho"))
		;
	}
	
	@Test
	public void devoFazerPesquisasAvancadasEmXML() {
		given()
		.when()
			.get("/usersXML")
		.then()
			//.statusCode(200)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id.size()", is(3))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it.salary = 1234.5678}.toDouble()", is(1234.5678d))
			.body("users.user.salary.find{it.salary = 1234.5678}", is("1234.5678"))
			.body("users.user.collect{it.age.toInteger() * 2}", hasItems(40,50,60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}

	@Test
	public void devoFazerPesquisasAvancadasEmXMLEJava() {
		Object path = given()
		.when()
			.get("/usersXML")
		.then()
			//.statusCode(200)
			.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
		;
		
		Assert.assertEquals(path.toString().toUpperCase(),"MARIA JOAQUINA");
	}
	
	@Test
	public void devoFazerPesquisasAvancadasEmXMLEJavaComArray() {
		//para trabalhar com extra��es de listas de elemento de um XML via path
		// utiliza-se a classe "NodeImpl" da biblioteca io.restassured
		ArrayList<NodeImpl> nomes = given()
		.when()
			.get("/usersXML")
		.then()
			//.statusCode(200)
			// Extrai do XML, via path, os nomes que cont�m a letra "n" em sua composi��o
			.extract().path("users.user.name.findAll{it.toString().contains('n')}")
		;
		Assert.assertEquals(2, nomes.size()); // verifica a quantidade de nomes recebida na lista
		// faz as compara��es entre par�metros validando se os conte�dos s�o iguais ap�s tranformados em caixa alta
		Assert.assertEquals(nomes.get(0).toString().toUpperCase(),"MARIA JOAQUINA");
		Assert.assertEquals("Maria Joaquina".toString().toUpperCase(), nomes.get(0).toString().toUpperCase());
		// faz as compara��es entre os conte�dos ignorando o a ordem de caixa da estrutura  das strings 
		Assert.assertTrue(nomes.get(1).toString().equalsIgnoreCase("ANA Julia"));
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	@Test
	public void devoFazerpesquisasAvancadasComXPath() {
		given()
		.when()
			.get("/usersXML")
		.then()
			//.statusCode(200)
			// para fazer buscas e  valida��es utilizando XPath no Rest Assured utiliza-se
			// hasXPath
			
			// validar a quantidade de usuarios
			.body(hasXPath("count(/users/user)", is("3")))
			// validar usu�rio com id = 1
			.body(hasXPath("/users/user[@id='1']"))
			// validar usu�rio com id = 2
			.body(hasXPath("//user[@id='2']"))
			// validar o nome da m�e do Luizinho subindo camadas
			.body(hasXPath("//filhos/name[text()='Luizinho']/../../name[text()='Ana Julia']"))
			// validar os nomes dos filhos de Ana Julia acessando dados adjacentes
			.body(hasXPath("/users/user/name[text()='Ana Julia']/following-sibling::filhos", 
					allOf(containsString("Zezinho"),containsString("Luizinho"))))
			// validar seo o nome "Jo�o da Silva" est� no doc
			.body(hasXPath("/users/user/name", is("Jo�o da Silva")))
			.body(hasXPath("//name", is("Jo�o da Silva")))
			// validar se o nome "Maria Joaquina" � o segundo nome
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
			// validar se o nome "Ana Julia" � o �ltimo nome
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
			// validar se existem 2 nomes que possuam "n" em sua estrutura
			.body(hasXPath("count(/users/user/name[contains(.,'n')])", is("2")))
			// validar se a idade de "Ana Julia" � menor que 25 anos
			.body(hasXPath("/users/user[age< 25]/name", is("Ana Julia")))
			// validar se a idade de "Maria Joaquina" � maior que 20 anos e menor que trinta anos
			.body(hasXPath("user[age > 20 and age < 30]/name", is("Maria Joaquina")))
			.body(hasXPath("user[age > 20][age < 30]/name", is("Maria Joaquina")))
		;
	}
}
