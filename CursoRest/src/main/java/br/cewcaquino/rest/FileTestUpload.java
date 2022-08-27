package br.cewcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

public class FileTestUpload {
	
	@Test
	public void deveObrigarEnvioDoArquivo() {
		given()
			.log().all()
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404) // deveria ser 400, está fora do padrão
			.body("error", is("Arquivo não enviado"))
		;
	}
	
	@Test
	public void deveFazerUploadDoArquivo() {
		given()
			.log().all()
			// O método multipart() é utilizado para fazer upload do arquivo
			// e deve ser chamado no given, como pré-condição
			.multiPart("arquivo", new File("src/main/resources/users.pdf"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("users.pdf"))
		;
	}
	
	@Test
	public void naoDeveFazerUploadDoArquivo() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/HaloCryptum.pdf"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			// definição de tempo aceitável para a conclusão do upload
			.time(lessThan(5000L))
			.statusCode(413) // arquivo maior que 1mb (o limite desse serv é até 1mb)
		;
	} 
	
	@Test
	public void deveFazerDownloadDoArquivo() throws IOException {
		// Para salvar um arquivo no formato original, deve-se extrair o arquivo e 
		// inserir num byteArray
		byte[] image = given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/download")
		.then()
			.statusCode(200)
			// extração como byteArray
			.extract().asByteArray()
		;
		// convertendo o arquivo para o tipo JPG. (formato original)
		
		// criação do arquivo na rota especificada
		File imagem = new File("src/main/resources/file.jpg");
		// criando um arquivo de saída para escrever os dados
		OutputStream saida = new FileOutputStream(imagem);
		// escrevendo dados
		saida.write(image);
		// fachando a escrita dos dados
		saida.close();
		
		// conferindo o tamanho do arquivo para fazer a asserção
		//System.out.println(imagem.length());
		
		Assert.assertThat(imagem.length(), lessThan(100000L));
		
	}
	

}
