package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.http.ContentType;

public class VerbosTest {

	@Test
	public void deveSalvarUsuarioJson() {
		given()
			//configuração de log
			.log().all()
			//definição do tipo de doc vai receber o post
			.contentType("application/json")
			//dados a serem criados no body do Json
			.body("{\"name\": \"Joao\", \"age\": 40}")
		.when()
			//caminho do post
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			//validações
			.statusCode(201) // código 201 = dados enviados
			.body("id", is(notNullValue()))
			.body("name", is("Joao"))
			.body("age", is(40))
		;
	}

	@Test
	public void naoDeveSalvarUsuarioJson() {
		/* Validação do preenchimento do campo name como campo obrigatório
		 * OBS: É possível conferir a mensagem no log da resposta
		 * */
		given()
			.log().all()
			.contentType("application/json")
			.body("{\"age\":40}")
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Name é um atributo obrigatório"))
		;
	}

	@Test
	public void deveSalvarUsuarioXML() {
		given()
			.log().all()
			.contentType(ContentType.XML) //aponta para "application/xml"
			.body("<user><name>Neo</name><age>42</age></user>")
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Neo"))
			.body("user.age", is("42"))
		;
	}
	
	//SERIALIZANDO COM MAP e OBJETO -----------------------------------------------------------
	
	@Test
	public void devoUsarSeriealizacaoUsandoMAP() {
		Map<String, Object> parametro = new HashMap<String, Object>();
		parametro.put("name", "ThomasAnderson.MAP");
		parametro.put("age", 00);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(parametro)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("name", is("ThomasAnderson.MAP"))
			.body("age", is(00))
		;
	}
	
	@Test	
	public void devoSerializarutilizandoOjeto() {
		User usuario = new User("ThomasAnderson.OBJETO", 73);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(usuario)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("name", is("ThomasAnderson.OBJETO"))
			.body("age", is(73))
		;
	}
	
	//Em XML
	@Test
	public void devoSalvarUsuarioXMLComObjeto() {
		User user = new User("ThomasAnderson", 36);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("ThomasAnderson"))
			.body("user.age", is("36"))
		;
	}

	//-----------------------------------------------------------------------------------------
	
	//DESERIALIZANDO --------------------------------------------------------------------------
	
	@Test 
	public void devoDeserializarJsonAoSalvarUsuario() {
		User user = new User("ThomasAnderson.DESERIALIZADO", 42);
		
		User usuarioInserido = 
				given()
					.log().all()
					.contentType(ContentType.JSON)
					.body(user)
				.when()
					.post("http://restapi.wcaquino.me/users")
				.then()
					.log().all()
					.statusCode(201)
					//aponta-se para a classe onde se encontra a estrutura dos dados 
					.extract().body().as(User.class)
				;
		Assert.assertEquals("ThomasAnderson.DESERIALIZADO", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(42));
		Assert.assertThat(usuarioInserido.getId(),is(notNullValue()));
	}
	
	//Em XML
	@Test
	public void devoDeserializarXMLAoSalvaUsuario() {
		User user = new User("Zephod Bibobrox",1256);
		
		User usuarioInserido = 
				given()
					.log().all()
					.contentType(ContentType.XML)
					.body(user)
				.when()
					.post("http://restapi.wcaquino.me/usersXML")
				.then()
					.log().all()
					.statusCode(201)
					//aponta-se para a classe onde se encontra a estrutura dos dados 
					.extract().body().as(User.class)
				;
		Assert.assertThat(usuarioInserido.getName(), is("Zephod Bibobrox"));
		Assert.assertThat(usuarioInserido.getAge(), is(1256));
		Assert.assertThat(usuarioInserido.getId(), is(notNullValue()));
	}
	
	//-----------------------------------------------------------------------------------------
	
	@Test
	public void deveAlterarUsuarioJson() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Thomas Anderson\",\"age\": 25}")
		.when()
			.put("http://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.log().all()
			.body("name", is("Thomas Anderson"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void devoCustomizarURLParte1() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Thomas Anderson\",\"age\": 25}")
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userid}","users", "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Thomas Anderson"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void devoCustomizarURLParte2() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Thomas Anderson\",\"age\": 25}")
			.pathParam("entidade", "users")
			.pathParam("userid", 1)
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userid}")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Thomas Anderson"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void devoDeletarUsuario() {
		
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void naoDevoDeletarUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
}
