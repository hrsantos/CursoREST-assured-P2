package br.cewcaquino.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {
	
	public static void main(String[] args) {
		
		//imprimindo na tela o conteúdo do Body do documento
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		//imprimindo na tela o conteúdo do Body do documento
		System.out.println(response.getBody().asString().equals("Ola Mundo!"));
		//imprimindo na tela o Status Code da requisição
		System.out.println(response.statusCode() == 200);
		
		//biblioteca rest assured para facilitar na validação de resultados
		ValidatableResponse validacao = response.then();
		validacao.statusCode(201);
	}
}
