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
	 * Teste de validação utilizando uma api de previsão do tempo
	 * Utiliza-se queryParam no pré-requisito da requisição
	 * Utiliza-se também a chave de autenticação solicitada pela api, sem ela, 
	 * não é possível realizar a solicitação 
	 * Caminho com a query montada
	 * https://api.openweathermap.org/data/2.5/weather?q=São José dos Campos,BR&appid=f0746547e65b17f60469ab43c7b7098e&&units=metric
	 * */
	@Test
	public void deveObterClima() {
		given()
			.log().all()
			//q=São José dos Campos,BR (Cidade que se quer obter a informação)
			.queryParam("q","São José dos Campos,BR")
			//appid: é a chave de autenticação da api que deve ser enviada na requisição  
			.queryParam("appid", "f0746547e65b17f60469ab43c7b7098e")
			//parâmetro para especificar o formato da temperatura em graus celcius na resposta 
			.queryParam("units", "metric")
		.when()
			//rota da api
			.get("http://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("São José dos Campos"))
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
	
	/* FAZENDO TESTES DE AUTENTICAÇÃO BÁSICA 
	 * 
	 * */
	@Test
	public void deveFazerAuteticacaoBasica() {
		given()
			.log().all()
		.when()
			// nessa formato, envia-se o login e senha no corpo da requisição
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
			// nesse formato utiliza-se o método .auth().basic("login", "senha") para passar os parâmetros da 
			// autenticação
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
			// nesse formato utiliza-se o método .auth().preemptive().basic("login", "senha") 
			// para passar os parâmetros da autenticação, explicitando o preenptive para
			// atender quando a autenticação for do tipo Challenge/Preemptive
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
			//quando utiliza-se o body para passar os parâmetros, o método da requisição deve ser post
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			// extraindo o token, que é uma cadeia de caracteres via path()
			.extract().path("token")
		;
		
		// acessando a lista de contas criadas, utlizando o token extraído anteriormente
		given()
			.log().all()
			// passamos o token via header
			.header("Authorization", "JWT " + token)
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			// validando o nome da conta criada no usuário Tester testador
			.body("nome", hasItem("Conta teste"))
		;
	}
	
	@Test
	public void deveAcessarAplicacaoWeb() {
		String cookie = given()
			.log().all()
			// preenchimento a partir do formulário html 
			.formParam("email", "tester@testador.com")
			.formParam("senha", "123")
			// ao interagir com html é preciso definir o ContentType como URLENC (URL Enconding)
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			// extração da informação do parâmetro set-cookie do header
			.extract().header("set-cookie")
		;
		
		// Os parâmetros recebidos da extração precisam ser refinadas, pois na extração vem sujeira
		// é preciso extrair aapenas a chave do cookie e para isso utiliza-se o método abaixo
		// split seleciona um inicio e fim da cadeia de caracter
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.println(cookie);
		
		//obter conta
		// nessa etapa, vamos extrair o nome da conta criada
		// e validar se é o resultado esperado
		String body = given()
					.log().all()
					//método para envio da chave co cookie
					.cookie("connect.sid", cookie)
				.when()
					.get("http://seubarriga.wcaquino.me/contas")
				.then()
					.log().all()
					.statusCode(200)
					// navegação pelo html de resposta da requisição para validação
					.body("html.body.table.tbody.tr[0].td[0]", is("Conta teste"))
					//extração do conteúdo do body navegado
					.extract().body().asString();
				;
		
		System.out.println("----------------------------------------------");
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
				/**/
	}
	
}
