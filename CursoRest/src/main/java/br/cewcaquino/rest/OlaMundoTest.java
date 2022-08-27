package br.cewcaquino.rest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void test01OlaMundo() {
		// imprimindo na tela o conte�do do Body do documento
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		// Modelos de assertivas para valida��o da requisi��o

		// verifica se o conte�do do corpo da mensagem e o par�metro passado s�o iguais
		// e retorna true/false
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		// verifica se o status code da mensagem e o par�metro passado s�o iguais e
		// retorna true/false
		Assert.assertTrue(response.statusCode() == 200);
		/*
		 * verifica se o status code e o par�metro passado s�o iguais e retorna
		 * true/false juntamente com uma mensagem de erro personalizada
		 */
		Assert.assertTrue("O status deve ser 200", response.statusCode() == 200);
		// verifica se o par�metro passado e o status code s�o iguais e retorna
		// true/false
		Assert.assertEquals(200, response.statusCode());

		// biblioteca rest assured para facilitar na valida��o de resultadosFO
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		get("http://restapi.wcaquino.me:80/ola").then().statusCode(200);

		given() // pr�-condi��o

				.when() // a��o
				.get("http://restapi.wcaquino.me:80/ola").then() // assertivas
				.statusCode(200);
	}

	// Trabalhando com a biblioteca Hamcrest para implementar asser��es
	@Test
	public void devoConhecerMatchersHamcrest() {
		// igualdade
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		// se � do tipo referido
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		// se � maior
		Assert.assertThat(128d, Matchers.greaterThan(120d));
		// se � menor
		Assert.assertThat(128d, Matchers.lessThan(130d));

		// Trabalhando com listas
		List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
		// verifica o tamanho
		assertThat(impares, hasSize(5));
		// verifica se cont�m todos os elementos
		assertThat(impares, contains(1, 3, 5, 7, 9));
		// verifica se cont�m todos os elementos em qualquer ordem
		assertThat(impares, containsInAnyOrder(1, 3, 7, 5, 9));
		// verifica se cont�m um item espec�fico
		assertThat(impares, hasItem(1));
		// verifica mais de um item espec�fico
		assertThat(impares, hasItems(3, 9));
		// verifica se n�o � igual
		assertThat("Maria", is(not("Jo�o")));
		// verifica se n�o � igual
		assertThat("Maria", not("Jo�o"));
		// verifica se alguma op��o corresponde ao par�metro
		assertThat("Maria", anyOf(is("Maria"), is("Jo�o")));
		// verifica de alguma parte est� contida no par�metro
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("na"), containsString("qui")));

	}
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me:80/ola")
		.then()
			//verifica todo o conte�do do body
			.body(is("Ola Mundo!"))
			//verifica se a string est� contida no body
			.body(containsString("Mundo"))
			//verifica se o body n�o est� vazio
			.body(is(not(nullValue())));
	}
}
