package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.xml.sax.SAXParseException;

import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;

public class ValidacaoSchemaXML {
	
	@Test
	public void deveValidarSchemaXML() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(200)
			// Para validar a estrutura do arquivo XML, utiliza-se o método
			// "RestAssuredMatchers.matchesXsdInClasspath()" para comparar o arquivo 
			// recebido no get() com um arquivo .xsd de referência
			.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
		;
	}
	
	/* No caso abaixo, o xml devolvido na requisição é inválido, pois não possui o campo nome
	 * Dessa forma, a valiodação do teste irá quebrar.
	 * Utilizando o parâmetro especificando a exceção esperada logo após o a anotação @Test, 
	 * é possível validar um cenário que se espera que quebre pela exceção.
	 * */
	@Test(expected = SAXParseException.class) // exceção esperada
	public void naoDeveValidarSchemaXML() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/invalidUsersXML")
		.then()
			.log().all()
			.statusCode(200)
			.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
		;
	}
	
	@Test
	public void deveValidarSchemaJson() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(200)
			
			// para validar o schema do json, é preciso incluir no pom uma dependência do mavem
			// https://mvnrepository.com/artifact/io.rest-assured/json-schema-validator
			// também é necessário criar um arquivo (aqui está em resources) com a conversão do schema do json
			// gerador de schema json - https://www.jsonschema.net
			
			// Para validar a estrutura do arquivo Json, utiliza-se o método
			// "JsonSchemaValidator.matchesJsonSchemaInClasspath()" para comparar o arquivo 
			// recebido no get() com um arquivo .json com o schema de referência
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
		;
	}

}
