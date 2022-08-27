package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import com.sun.corba.se.impl.naming.pcosnaming.NameServer;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.runtime.Name;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder.Body;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import sun.management.resources.agent;

public class UserJsonTest {
	
	/*
	 *Validação de elementos Body via Json 
	 */
	
	//Exemplo 1 - Utilizado pelo Wagner
	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
			
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		.then()
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
		;
	}
	
	// Outros exemplos utilizando formas mais complexas de validação
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		//ValidatableResponse validacao = response.then();
		
		//Path
		Assert.assertEquals(new Integer(1), response.path("id"));
		Assert.assertEquals(new Integer(1), response.path("%s","id"));
		
		//JsonPath
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));
		
		//from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);

	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
			
		.when()
			.get("http://restapi.wcaquino.me/users/2")
		.then()
			.body("id", is(2))
			.body("name", containsString("Joaquina"))
			//Para acessar o 2º nível dentro do documento, basta utilizar o ponto + o id do segundo nível
			.body("endereco.rua", is("Rua dos bobos"))
		;
	}
	
	@Test
	public void deveverificarItensLista() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Luizinho"))
			.body("filhos.name", hasItems("Luizinho","Zezinho"))
			;
	}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	}
	
	@Test
	public void deveVerificarListaRaiz() {
		//Valida elementos em lista que compõe a raiz do doc Json
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			//o "$" é apenas uma convensão, pose-se deixar esse parâmetro vazio quando se interage com elementos na raiz
			.body("$", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("name", hasItem("João da Silva"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
	
	@Test
	public void deveFazerVerificacoesAvancadas() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			//verificar se no Json possui 2 elementos com idade <= 25
			.body("age.findAll{it <= 25}.size()", is(2))
			//verificar se no Json possui 1 elemento com idade <= 25 e > 20
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
			//verificar se no Json possui 1 elemento com idade <= 25 e > 20 que possua o nome "Maria Joaquina"
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
			//verificar no primeiro registro se idade é menor que 25 e nome é "Maria Joaquina"
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
			//verificar no último registro se idade é menor que 25 e nome é "Ana Júlia"
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
			//busca o primeiro registro com a condição
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))
			//verifica se os itens passados possuem a letra "n" em sua composição
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina","Ana Júlia"))
			//verifica se os itens do parâmetro possuem mais de 10 caracteres
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva","Maria Joaquina"))
			//converte as strings da coleção para UpperCase e comprara com o parâmetro
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			//busca as ocorrências de itens que iniciem com "Maria", converte para UpperCase e compara com os parâmentros passados
			//verificando se a lista obtida contém o parâmentro procurado e se ele a lista possui apenas um elemento
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", 
					allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			//multiplica cada item da lista por dois e compara cada resultado com os parâmetros passados
			.body("age.collect{it * 2}", hasItems(60,50,40))
			//verifica quantas ocorrências do elemento id possui no Json
			.body("id.max()", is(3))
			//verifica qual é o menor valor de salario e compara com o parâmentro esperado
			.body("salary.min()", is(1234.5678f))
			//soma todos os salários e compara com o parâmetro esperado, ajustando um valor 
			//flutuante com tolerância aceitável
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			//soma todos os salários e compara se o valor obtido está entre os parâmetros esperados
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
		;
	}
	
	@Test
	public void devoUnirJsonPathComJAVA() {
		//nesse porcedimento, utiliza-se deve-se criar uma lista para receber a extração dos elementos do Json
		//que serão manipulado
		ArrayList<String> names =
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			//utiliza-se o extract para extrair a lista de nomes que comecem com Maria do Json via path
			.extract().path("name.findAll{it.startsWith('Maria')}")
		;
		//Após a extração, a lista criada pode ser manipulada com mais facilidade via validações do JUnit 
		
		//verifica o tamanho da lista recebida
		Assert.assertEquals(1, names.size());
		//verifica se o elemento corresponde 
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mAria JoaQuina"));
		Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}

}
