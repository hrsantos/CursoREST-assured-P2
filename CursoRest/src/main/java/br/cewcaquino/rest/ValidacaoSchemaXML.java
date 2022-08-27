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
			// Para validar a estrutura do arquivo XML, utiliza-se o m�todo
			// "RestAssuredMatchers.matchesXsdInClasspath()" para comparar o arquivo 
			// recebido no get() com um arquivo .xsd de refer�ncia
			.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
		;
	}
	
	/* No caso abaixo, o xml devolvido na requisi��o � inv�lido, pois n�o possui o campo nome
	 * Dessa forma, a valioda��o do teste ir� quebrar.
	 * Utilizando o par�metro especificando a exce��o esperada logo ap�s o a anota��o @Test, 
	 * � poss�vel validar um cen�rio que se espera que quebre pela exce��o.
	 * */
	@Test(expected = SAXParseException.class) // exce��o esperada
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
			
			// para validar o schema do json, � preciso incluir no pom uma depend�ncia do mavem
			// https://mvnrepository.com/artifact/io.rest-assured/json-schema-validator
			// tamb�m � necess�rio criar um arquivo (aqui est� em resources) com a convers�o do schema do json
			// gerador de schema json - https://www.jsonschema.net
			
			// Para validar a estrutura do arquivo Json, utiliza-se o m�todo
			// "JsonSchemaValidator.matchesJsonSchemaInClasspath()" para comparar o arquivo 
			// recebido no get() com um arquivo .json com o schema de refer�ncia
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
		;
	}

}
