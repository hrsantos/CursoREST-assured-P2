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
		// imprimindo na tela o conteúdo do Body do documento
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		// Modelos de assertivas para validação da requisição

		// verifica se o conteúdo do corpo da mensagem e o parâmetro passado são iguais
		// e retorna true/false
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		// verifica se o status code da mensagem e o parâmetro passado são iguais e
		// retorna true/false
		Assert.assertTrue(response.statusCode() == 200);
		/*
		 * verifica se o status code e o parâmetro passado são iguais e retorna
		 * true/false juntamente com uma mensagem de erro personalizada
		 */
		Assert.assertTrue("O status deve ser 200", response.statusCode() == 200);
		// verifica se o parâmetro passado e o status code são iguais e retorna
		// true/false
		Assert.assertEquals(200, response.statusCode());

		// biblioteca rest assured para facilitar na validação de resultadosFO
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		get("http://restapi.wcaquino.me:80/ola").then().statusCode(200);

		given() // pré-condição

				.when() // ação
				.get("http://restapi.wcaquino.me:80/ola").then() // assertivas
				.statusCode(200);
	}

	// Trabalhando com a biblioteca Hamcrest para implementar asserções
	@Test
	public void devoConhecerMatchersHamcrest() {
		// igualdade
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		// se é do tipo referido
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		// se é maior
		Assert.assertThat(128d, Matchers.greaterThan(120d));
		// se é menor
		Assert.assertThat(128d, Matchers.lessThan(130d));

		// Trabalhando com listas
		List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
		// verifica o tamanho
		assertThat(impares, hasSize(5));
		// verifica se contém todos os elementos
		assertThat(impares, contains(1, 3, 5, 7, 9));
		// verifica se contém todos os elementos em qualquer ordem
		assertThat(impares, containsInAnyOrder(1, 3, 7, 5, 9));
		// verifica se contém um item específico
		assertThat(impares, hasItem(1));
		// verifica mais de um item específico
		assertThat(impares, hasItems(3, 9));
		// verifica se não é igual
		assertThat("Maria", is(not("João")));
		// verifica se não é igual
		assertThat("Maria", not("João"));
		// verifica se alguma opção corresponde ao parâmetro
		assertThat("Maria", anyOf(is("Maria"), is("João")));
		// verifica de alguma parte está contida no parâmetro
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("na"), containsString("qui")));

	}
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me:80/ola")
		.then()
			//verifica todo o conteúdo do body
			.body(is("Ola Mundo!"))
			//verifica se a string está contida no body
			.body(containsString("Mundo"))
			//verifica se o body não está vazio
			.body(is(not(nullValue())));
	}
}
