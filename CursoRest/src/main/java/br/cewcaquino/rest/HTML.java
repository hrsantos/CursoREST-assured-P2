package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import io.restassured.http.ContentType;

public class HTML {
	
	@Test
	public void deveFazerBuscaComHTML() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.HTML)
			.body("html.body.div.table.tbody.tr.size()", is(3))
			.appendRootPath("html.body.div.table.tbody")
			.body("tr[1].td[2]", is("25"))
			.body("tr.find{it.toString().startsWith('3')}.td[1]", is("Ana Júlia"))
		;
	}

	@Test
	public void deveFazerBuscaComXPathEmHTML() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/v2/users?format=clean")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.HTML)
			.body(hasXPath("count(//table/tr)", is("4")))
			.body(hasXPath("//td[text()=('2')]/../td[2]", is("Maria Joaquina")))
		;
	}

}
