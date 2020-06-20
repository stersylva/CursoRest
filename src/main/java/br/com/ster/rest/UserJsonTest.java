package br.com.ster.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class UserJsonTest {
	
	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18));
	}
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		
		//path
		System.out.println(response.path("id")); // vai trazer o que tem dentro da tag id
		Assert.assertEquals(new Integer(1), response.path("id"));
		Assert.assertEquals(new Integer(1), response.path("%s", "id"));
		
		//JsonPath
		JsonPath jsonPath = new JsonPath(response.asString());
		Assert.assertEquals(1, jsonPath.getInt("id"));
		
		//from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);
		
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"))
		;
	}
	
	@Test
	public void deveVerificarLista() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho")) //lista
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho")) //se tem zezinho na lista
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
			;
	}
	
	@Test
	public void deveRetornarUsuarioInexistente() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
			;
	}
	
	@Test
	public void deververificarListaRaiz() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
			;
	}
	// aula 16 filtros
	@Test
	public void deverVerificaracoesAvancadas() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("", hasSize(3))
			.body("age.findAll{it <=25}.size()", is(2)) //vai pegar a idade menor ou igual a 25
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1)) //vai pegar a idade menor ou igual a 25 e maior que 20
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia")) // paga o ultimo elemento da lista
			
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))
			
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
			
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			
			.body("id.max()", is (3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
			;
	}
	@Test
	public void devoUnirJsonPathComJava() {
		ArrayList<String> nomes =
				given()
				.when()
					.get("http://restapi.wcaquino.me/users")
				.then()
					.statusCode(200)
					.extract().path("name.findAll{it.startsWith('Maria')}")
					;
		Assert.assertEquals(1, nomes.size());
		Assert.assertTrue(nomes.get(0).equalsIgnoreCase("maria joaquina"));
		Assert.assertEquals(nomes.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}

}
