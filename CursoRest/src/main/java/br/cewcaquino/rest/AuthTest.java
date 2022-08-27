package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class AuthTest {

	@Test
	public void deveAcessarSWAPI() {
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
		;
	}
	
	/* ACESSANDO API COM CHAVE
	 * Teste de valida��o utilizando uma api de previs�o do tempo
	 * Utiliza-se queryParam no pr�-requisito da requisi��o
	 * Utiliza-se tamb�m a chave de autentica��o solicitada pela api, sem ela, 
	 * n�o � poss�vel realizar a solicita��o 
	 * Caminho com a query montada
	 * https://api.openweathermap.org/data/2.5/weather?q=S�o Jos� dos Campos,BR&appid=f0746547e65b17f60469ab43c7b7098e&&units=metric
	 * */
	@Test
	public void deveObterClima() {
		given()
			.log().all()
			//q=S�o Jos� dos Campos,BR (Cidade que se quer obter a informa��o)
			.queryParam("q","S�o Jos� dos Campos,BR")
			//appid: � a chave de autentica��o da api que deve ser enviada na requisi��o  
			.queryParam("appid", "f0746547e65b17f60469ab43c7b7098e")
			//par�metro para especificar o formato da temperatura em graus celcius na resposta 
			.queryParam("units", "metric")
		.when()
			//rota da api
			.get("http://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("S�o Jos� dos Campos"))
			.body("main.temp", greaterThan(20f))
		;
	}
	
	@Test
	public void naoDeveFazerAutenticacaoBasica() {
		given()
			.log().all()
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
			//.body("status", is("logado"))
		;
	}
	
	/* FAZENDO TESTES DE AUTENTICA��O B�SICA 
	 * 
	 * */
	@Test
	public void deveFazerAuteticacaoBasica() {
		given()
			.log().all()
		.when()
			// nessa formato, envia-se o login e senha no corpo da requisi��o
			.get("http://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoBasica2() {
		given()
			.log().all()
			// nesse formato utiliza-se o m�todo .auth().basic("login", "senha") para passar os par�metros da 
			// autentica��o
			.auth().basic("admin", "senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoBasicaComChallengeOuPreemptivo() {
		given()
			.log().all()
			// nesse formato utiliza-se o m�todo .auth().preemptive().basic("login", "senha") 
			// para passar os par�metros da autentica��o, explicitando o preenptive para
			// atender quando a autentica��o for do tipo Challenge/Preemptive
			.auth().preemptive().basic("admin", "senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
		;
	}
	
	/* TESTE UTILIZANDO JWT -JSON WEB TOKEN
	 * 
	 * */
	@Test
	public void deveFazerAutenticacaoComTokenJWT() {
		Map<String, String> login= new HashMap<String, String>();
		login.put("email", "tester@testador.com");
		login.put("senha", "123");
		
		// extraindo o token
		String token = given()
			.log().all()
			//quando utiliza-se o body para passar os par�metros, o m�todo da requisi��o deve ser post
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			// extraindo o token, que � uma cadeia de caracteres via path()
			.extract().path("token")
		;
		
		// acessando a lista de contas criadas, utlizando o token extra�do anteriormente
		given()
			.log().all()
			// passamos o token via header
			.header("Authorization", "JWT " + token)
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			// validando o nome da conta criada no usu�rio Tester testador
			.body("nome", hasItem("Conta teste"))
		;
	}
	
	@Test
	public void deveAcessarAplicacaoWeb() {
		String cookie = given()
			.log().all()
			// preenchimento a partir do formul�rio html 
			.formParam("email", "tester@testador.com")
			.formParam("senha", "123")
			// ao interagir com html � preciso definir o ContentType como URLENC (URL Enconding)
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			// extra��o da informa��o do par�metro set-cookie do header
			.extract().header("set-cookie")
		;
		
		// Os par�metros recebidos da extra��o precisam ser refinadas, pois na extra��o vem sujeira
		// � preciso extrair aapenas a chave do cookie e para isso utiliza-se o m�todo abaixo
		// split seleciona um inicio e fim da cadeia de caracter
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.println(cookie);
		
		//obter conta
		// nessa etapa, vamos extrair o nome da conta criada
		// e validar se � o resultado esperado
		String body = given()
					.log().all()
					//m�todo para envio da chave co cookie
					.cookie("connect.sid", cookie)
				.when()
					.get("http://seubarriga.wcaquino.me/contas")
				.then()
					.log().all()
					.statusCode(200)
					// navega��o pelo html de resposta da requisi��o para valida��o
					.body("html.body.table.tbody.tr[0].td[0]", is("Conta teste"))
					//extra��o do conte�do do body navegado
					.extract().body().asString();
				;
		
		System.out.println("----------------------------------------------");
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
				/**/
	}
	
}
